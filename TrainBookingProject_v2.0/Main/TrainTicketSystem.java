package Main;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import DAO.*;
import DB_init.Database;
import DataModel.*;

import java.time.format.DateTimeParseException;

public class TrainTicketSystem {
	private static TrainTicketSystem instance = null;
	private UserDAO userDAO;
	private TrainDAO trainDAO;
	private SeatDAO seatPlanDAO;
	private CustomerServiceDAO customerServiceDAO;
	private OrderRecordDAO orderRecordDAO;
	private User currentUser;
	private MessageCenter messageCenter;

	public TrainTicketSystem() {
		userDAO = new UserDAO();
		trainDAO = new TrainDAO();
		seatPlanDAO = new SeatDAO();
		customerServiceDAO = new CustomerServiceDAO();
		orderRecordDAO = new OrderRecordDAO();
		messageCenter = new MessageCenter();
		currentUser = null;
	}

	public static TrainTicketSystem getInstance() {
		if (instance == null) {
			instance = new TrainTicketSystem();
		}
		return instance;
	}

	public User getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

	public User login(Scanner scanner) {
		System.out.print("Enter username: ");
		String username = scanner.nextLine();
		System.out.print("Enter password: ");
		String password = scanner.nextLine();
		
		User user = userDAO.login(username, password);
		if (user != null) {
			System.out.println("Login successful.");
			currentUser = user;
		} else {
			System.out.println("Invalid username or password.");
		}

		System.out.println("");
		return user;
	}

	public boolean register(Scanner scanner) {
		System.out.print("Registration Form:\nEnter username: ");
		String username = scanner.nextLine();
		System.out.print("Enter password: ");
		String password = scanner.nextLine();

		if (!userDAO.usernameExists(username)) {
			System.out.println("Register successfully.");
		} else {
			System.out.println("Username already exists.");
		}
		
		System.out.println("");
		return userDAO.register("normal", username, password);
	}

	// fn to display finished orders
	public void displayFinishedOrders(String id, Scanner scanner) {
		ArrayList<OrderRecord> finishedOrders = getFinishedOrders(id);
		if (finishedOrders.size() == 0) {
			System.out.println("\nNo finished orders.");
		} else {
			System.out.println("\n===============================================");
			System.out.println("Finished Orders:");
			for (int i = 0; i < finishedOrders.size(); i++) {
				OrderRecord finishedOrder = finishedOrders.get(i);
				Train train = trainDAO.getTrain_fromTrainTable(finishedOrder.getTrainId());

				// print finishedOrder details
				System.out.println((i + 1) + ": " + finishedOrder.getOrderId());
				System.out.println("\nTrain Number: " + train.getTrainNumber());
				System.out.println("Journey: " + "from " + train.getDeparture() + " to " + train.getArrival());
				System.out.println("Date: " + train.getDate() + ", " + train.getTime());
				System.out.println("Price: " + train.getPrice());

				System.out.print("\nPlease rate this order: (1-5, 5 is the best): ");
				int rating = scanner.nextInt();
				finishedOrder.setRating(rating);
			}
			System.out.println("\n===============================================");
		}
	}

	// fn to get finished orders
	public ArrayList<OrderRecord> getFinishedOrders(String id) {
		ArrayList<OrderRecord> finishedOrders = new ArrayList<OrderRecord>();
		ArrayList<OrderRecord> orderRecordList = orderRecordDAO.getOrdersByUserId(id);

		for (int i = 0; i < orderRecordList.size(); i++) {
			OrderRecord orderRecord = orderRecordList.get(i);
			String status = trainDAO.getTrain_fromTrainTable(orderRecord.getTrainId()).getStatus();
			int rating = orderRecord.getRating();

			if (status.equals("active") && rating == 0) {
				finishedOrders.add(orderRecord);
			}
		}
		return finishedOrders;
	}

	// fn to order tickets
	public void bookTickets(Scanner scanner) {
		if (orderRecordDAO.getOrdersByUserId(currentUser.getId()).size() > 0) {
			System.out.print("\nDo you need any recommendations? (Y/N) ");
			String preferences = scanner.nextLine();

			if (preferences.equals("Y")) {
				System.out.print(
						"\nPlease enter the location that you might want to depart or arrive [LA, Washington DC, Miami, Chicago, None]: ");
				String location = scanner.nextLine();

				displayRecommendations(currentUser.getId(), location);
			}
		}

		// Base Case: No train recommendations
		int trainCount = displayTrains_available();
		System.out.println("\nPlease enter the train number you want to order: ");
		int trainChoice = scanner.nextInt() - 1;

		if (trainChoice < 0 || trainChoice > trainCount) {
			System.out.println("Invalid train selection.");
			return;
		}
		Train selectedTrain = trainDAO.getTable_train().get(trainChoice);

		System.out.print("Enter number of passengers: ");
		int passengerCount = scanner.nextInt();

		ArrayList<Ticket> order_ticketList = new ArrayList<>();

		int seatCount = selectedTrain.getAvailableSeats();
		if (passengerCount > seatCount) {
			System.out.println("Not enough seats available.");
		}
		double totalPrice = 0;
		int counter = 0;
		double ticketPrice = selectedTrain.getPrice();

		while (counter < passengerCount) {
			System.out.print("Select ticket type (1. Regular, 2. Upgrade(with meal) price = + $30 ): ");
			int ticketTypeChoice = scanner.nextInt();

			if (ticketTypeChoice == 2) {
				totalPrice += (ticketPrice + 30);
			} else if (ticketTypeChoice == 1) {
				totalPrice += ticketPrice;
			} else {
				System.out.println("Invalid ticket type. Please try again.");
				continue;
			}

			System.out.println("Passenger " + (counter + 1) + ":");
			System.out.print("Name: ");
			String name = scanner.next();

			System.out.print("Age: ");
			int age = scanner.nextInt();
			// scanner.nextLine(); // Consume newline

			order_ticketList.add(new Ticket(name, age));
			counter++;
		}

		// Seats arrangement -> ticket Info:
		ArrayList<String> seatNumbersForticket;
		if (passengerCount > 1 && passengerCount <= 6) {
			System.out.println("Would you like to arrange seats together? (Y/N)");
			String preference = scanner.next();
			if (preference.equals("Y")) {
				seatNumbersForticket = arrangeSeat(selectedTrain.getTrainNumber(), passengerCount);
			} else {
				seatNumbersForticket = arrangeSeat(selectedTrain.getTrainNumber());

			}
		} else if (passengerCount > 6) {
			System.out.println("Seats will be arranged randomly.");
			seatNumbersForticket = arrangeSeat(selectedTrain.getTrainNumber(), passengerCount);
		} else {
			System.out.println("Seats will be arranged randomly.");
			seatNumbersForticket = arrangeSeat(selectedTrain.getTrainNumber());
		}

		// test:
		// System.out.println("test_seats are: " + seatNumbersForticket);
		// System.out.println("test_seats left: " + selectedTrain.getAvailableSeats());

		// Ticket Info:
		// ...

		OrderRecord orderRecord = new OrderRecord(
				String.format("orderID_%s_%s", currentUser.getId(),
						orderRecordDAO.getOrdersByUserId(currentUser.getId()).size()),
				currentUser.getId(),
				selectedTrain.getTrainNumber(),
				new Date(),
				getTotalPriceWithDiscount(totalPrice), // tmp
				// order_passengerList, // tmp
				order_ticketList// tmp
		);

		orderRecordDAO.addOrderRecord(orderRecord);

		System.out.println("Order successful. Order ID: " + orderRecord.getOrderId());
	}

	private int displayTrains_available() {
		ArrayList<Train> availableTrainTable = trainDAO.getTable_train();
		int availableTrain_count = 0;

		System.out.println(
				"\n=============================================================================================================");
		System.out.println("Available trains:");
		// display a table to show all available trains
		for (int i = 0; i < availableTrainTable.size(); i++) {
			Train train = availableTrainTable.get(i);
			if (train.getStatus() == "active" && train.getAvailableSeats() > 0) {
				availableTrain_count++;
				System.out.println((i + 1) + ": " + train.toString());
			}
		}
		System.out.println(
				"\n=============================================================================================================");
		return availableTrain_count;
	}

	public double getTotalPriceWithDiscount(Double totalPrice) {
		double discount = currentUser.getMember().getDiscount();

		double result = 0;

		result = totalPrice - (totalPrice * discount);
		if (currentUser.getCouponList().size() > 0) {
			result = result - userDAO.useCoupon(result, currentUser);
			System.out.println("You use a coupon. ");
		}

		return result;
	}

	// recommend train according to user's order history
	public void displayRecommendations(String id, String location) {
		ArrayList<String> recommendedTrainIds = recommendTrains(id);
		ArrayList<Train> availableTrains = new ArrayList<>();

		for (int i = 0; i < recommendedTrainIds.size(); i++) {
			Train train = trainDAO.getTrain_fromTrainTable(recommendedTrainIds.get(i));

			if (train.getStatus().equals("active") && train.getAvailableSeats() > 0) {
				availableTrains.add(train);
			}
		}

		System.out.println(
				"\n=============================================================================================================");
		System.out.println("Top 3 Train Recommendations:");

		if (availableTrains.size() == 0) {
			System.out.println("No recommendations available.");
		} else {
			if (location.equals("None")) {
				for (int i = 0; i < availableTrains.size(); i++) {
					System.out.println(availableTrains.get(i).toString());
				}
			} else {
				for (int i = 0; i < availableTrains.size(); i++) {
					if (availableTrains.get(i).getDeparture().equals(location)
							|| availableTrains.get(i).getArrival().equals(location)) {
						System.out.println(availableTrains.get(i).toString());
					}
				}
			}
		}

		System.out.println(
				"\n=============================================================================================================");
	}

	// fn to provide recommendations
	public ArrayList<String> recommendTrains(String id) {
		ArrayList<OrderRecord> orderRecordList = orderRecordDAO.getOrdersByUserId(id);

		if (orderRecordList.isEmpty()) {
			return new ArrayList<>();
		} else {
			Map<String, Integer> trainRating = new HashMap<>();
			for (OrderRecord orderRecord : orderRecordList) {
				String trainId = orderRecord.getTrainId();
				int rating = orderRecord.getRating();
				if (rating > 0) {
					trainRating.put(trainId, rating);
				} else {
					trainRating.put(trainId, 0);
				}
			}

			List<String> sortedTrainIds = trainRating.entrySet().stream()
					.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
					.map(Map.Entry::getKey)
					.toList();

			int endIndex = Math.min(3, sortedTrainIds.size());
			return new ArrayList<>(sortedTrainIds.subList(0, endIndex));
		}
	}

	// arrange single seat
	public ArrayList<String> arrangeSeat(String trainNumber) {

		seatPlan target_seatPlan = seatPlanDAO.getSeatPlan_ByTrainID(trainNumber);
		String result = seatPlanDAO.getOneAvailableSeat(target_seatPlan);

		// test
		System.out.println("test_seats result: " + result);
		System.out.println("test_seats target_seatPlan: " + target_seatPlan.toString());
		ArrayList<String> seatNumbers = new ArrayList<>();

		if (result != null) {
			Train targetTrain = trainDAO.getTrain_fromTrainTable(trainNumber);
			int availableSeats = targetTrain.getAvailableSeats();
			availableSeats--;
			targetTrain.setAvailableSeats(availableSeats);
			seatNumbers.add(result);
			return seatNumbers;
		}

		return null;
	}

	// arrange seats together, > 1 passengers
	public ArrayList<String> arrangeSeat(String trainNumber, int passengerCount) {

		seatPlan target_seatPlan = seatPlanDAO.getSeatPlan_ByTrainID(trainNumber);
		ArrayList<String> result = seatPlanDAO.getMultipleAvailableSeat(target_seatPlan, passengerCount);

		// test
		System.out.println("test_seats result: " + result);
		System.out.println("test_seats target_seatPlan: " + target_seatPlan.toString());

		if (result != null) {
			Train targetTrain = trainDAO.getTrain_fromTrainTable(trainNumber);
			int availableSeats = targetTrain.getAvailableSeats();
			availableSeats -= passengerCount;
			targetTrain.setAvailableSeats(availableSeats);
			return result;
		}

		return null;
	}

	// fn to check tickets (more like check orders)
	public void viewOrders(Scanner scanner) {
		while (true) {
			ArrayList<OrderRecord> userOrders = orderRecordDAO.getOrdersByUserId(currentUser.getId());
			if (userOrders.isEmpty()) {
				System.out.println("You currently have no orders.");
				return;
			}

			if (!userOrders.isEmpty()) {
				summarizeOrders(userOrders);
			}
			displayOrders(userOrders);

			System.out.print("Enter the Order No. to EDIT or CANCEL it, 0 to return to the main menu: ");
			int orderNo = scanner.nextInt(); // not to be confused with Order Id

			if (orderNo == 0) {
				return;
			} else if (orderNo > userOrders.size()) {
				System.out.println("Invalid option.");
			} else {
				System.out.println("1. Edit Order");
				System.out.println("2. Cancel Order");
				System.out.println("3. Return");
				System.out.print("Please select an option: ");
				int option = scanner.nextInt();
				scanner.nextLine();

				switch (option) {
					case 1:
						editTicket(scanner, userOrders.get(orderNo - 1));
						break;

					case 2:
						cancelOrder(scanner, userOrders.get(orderNo - 1));
						break;

					case 3:
						return;

					default:
						System.out.println("Invalid option. Please try again.");
				}
			}
		}
	}

	private int displayOrders(ArrayList<OrderRecord> userOrders) {
		System.out.println("Your Order Records:");
		for (int i = 0; i < userOrders.size(); i++) {
			OrderRecord order = userOrders.get(i);
			String orderNoAndId = String.format("%s %d: %s %s", "==========", i + 1, order.getOrderId(),
					"==========");
			System.out.println(orderNoAndId);
			System.out.println(order.toString());
			for (int j = 0; j < orderNoAndId.length(); j++) {
				System.out.print("=");
			}
			System.out.println();
		}
		return userOrders.size();
	}

	private void summarizeOrders(ArrayList<OrderRecord> userOrders) {
	    double avgRating = calculateAverageRating(userOrders);
	    HashMap<String, Integer> destVisitCount = calculateDestinationVisitCount(userOrders);
	    Map<String, Double> destAvgRating = calculateDestinationAvgRating(userOrders);

	    // Find most visited destinations
	    int maxVisitCount = Collections.max(destVisitCount.values());
	    // Find highest rated destinations
	    double maxAvgRating = Collections.max(destAvgRating.values());

	    // Output summary
	    System.out.printf("Summary of your orders:\n");
	    System.out.printf("Average rating of your orders: %.2f\n", avgRating);
	    System.out.printf("Your most visited destination(s) (%d times each): %s\n", maxVisitCount, String.join(", ", getMostVisitedDestination(userOrders)));
	    System.out.printf("Your highest rated destination(s) (Average of %.2f rating each): %s\n\n", maxAvgRating, String.join(", ",  getMaxAvgRatingDestination(userOrders)));
	}
	
	public double calculateAverageRating(ArrayList<OrderRecord> userOrders) {
	    int ratingSum = 0;
	    for (OrderRecord order : userOrders) {
	        ratingSum += order.getRating();
	    }
	    return ratingSum / (double) userOrders.size();
	}

	public ArrayList<String> getMostVisitedDestination(ArrayList<OrderRecord> userOrders) {
		ArrayList<String> mostVisitedDest = new ArrayList<>();
		HashMap<String, Integer> destVisitCount = calculateDestinationVisitCount(userOrders);
		int maxVisitCount = Collections.max(destVisitCount.values());
		
	    for (Map.Entry<String, Integer> entry : destVisitCount.entrySet()) {
	        if (entry.getValue() == maxVisitCount) {
	            mostVisitedDest.add(entry.getKey());
	        }
	    }
	    
	    return mostVisitedDest;
	}
	
	private HashMap<String, Integer> calculateDestinationVisitCount(ArrayList<OrderRecord> userOrders) {
	    HashMap<String, Integer> destVisitCount = new HashMap<>();
	    for (OrderRecord order : userOrders) {
	        String dest = trainDAO.getTrain_fromTrainTable(order.getTrainId()).getArrival();
	        destVisitCount.put(dest, destVisitCount.getOrDefault(dest, 0) + 1);
	    }
	    return destVisitCount;
	}
	
	public ArrayList<String> getMaxAvgRatingDestination(ArrayList<OrderRecord> userOrders) {
		ArrayList<String> maxAvgRatingDest = new ArrayList<>();
		HashMap<String, Double> destAvgRating = calculateDestinationAvgRating(userOrders);
	    double maxAvgRating = Collections.max(destAvgRating.values());
		
	    for (Map.Entry<String, Double> entry : destAvgRating.entrySet()) {
	        if (entry.getValue() == maxAvgRating) {
	        	maxAvgRatingDest.add(entry.getKey());
	        }
	    }
	    
	    return maxAvgRatingDest;
	}
	
	private HashMap<String, Double> calculateDestinationAvgRating(ArrayList<OrderRecord> userOrders) {
		HashMap<String, Double> destTotalRating = new HashMap<>();
		HashMap<String, Double> destAvgRating = new HashMap<>();
		HashMap<String, Integer> destVisitCount = calculateDestinationVisitCount(userOrders);
	    
	    for (OrderRecord order : userOrders) {
	        String dest = trainDAO.getTrain_fromTrainTable(order.getTrainId()).getArrival();
	        destTotalRating.put(dest, destTotalRating.getOrDefault(dest, 0.0) + order.getRating());
	    }
	    
	    for (Map.Entry<String, Double> entry : destTotalRating.entrySet()) {
	    	String dest = entry.getKey();
	    	double avgRating = entry.getValue() / destVisitCount.get(dest);
	    	destAvgRating.put(dest, avgRating);
	    }
	    
	    return destAvgRating;
	}
	
	private void editTicket(Scanner scanner, OrderRecord order) {
		System.out.println("Current Order Details:");
		System.out.println(order.toString());
		System.out.println("You can perform the following actions:");
		System.out.println("1. Modify Passenger Information");
		System.out.println("2. Return");
		System.out.print("Choose an option: ");
		int editOption = scanner.nextInt();
		scanner.nextLine();

		switch (editOption) {
			case 1:
				// Modify Passenger Information
				ArrayList<Ticket> tickets = order.getTicketList();
				for (int i = 0; i < tickets.size(); i++) {
					Ticket t = tickets.get(i);
					System.out.printf("Passenger %d: %s, Age: %d\n", i + 1, t.getName(), t.getAge());
					System.out.print("Do you want to modify this passenger's information? (Y/N): ");
					String choice = scanner.nextLine();
					if (choice.equalsIgnoreCase("Y")) {
						System.out.print("Enter new name: ");
						String newName = scanner.nextLine();
						System.out.print("Enter new age: ");
						int newAge = scanner.nextInt();
						scanner.nextLine();

						t.setName(newName);
						t.setAge(newAge);
					}
				}
				System.out.println("Passenger information updated.");
				break;
			case 2:
				return;

			default:
				System.out.println("Invalid option.");
		}

		System.out.println("Order has been updated.");
	}

	private void cancelOrder(Scanner scanner, OrderRecord order) {
		System.out.print("Are you sure you want to cancel this order? (Y/N): ");
		String confirm = scanner.nextLine();
		if (!confirm.equalsIgnoreCase("Y")) {
			System.out.println("Cancel operation aborted.");
			return;
		}

		String trainId = order.getTrainId();
		Train train = trainDAO.getTrain_fromTrainTable(trainId);
		int passengerCount = order.getTicketList().size();
		train.setAvailableSeats(train.getAvailableSeats() + passengerCount);
		trainDAO.updateTrain_fromTrainTable(train);

		boolean deleted = orderRecordDAO.deleteOrderRecord(order.getOrderId());
		if (deleted) {
			System.out.println("Order has been successfully canceled.");
		} else {
			System.out.println("Failed to cancel the order.");
		}
	}

	public void cs(Scanner scanner) {
	    boolean isStay = true;

	    // Welcome message
	    System.out.println(
	            "\n=============================================================================================================");
	    System.out.println(
	            "CSer : Hi, welcome to customer service, how can I help you? (type your question or type exit to return to the main menu)");

	    // Main loop
	    while (isStay) {
	        System.out.print("You : ");
	        String question = scanner.nextLine(); // Get user input

	        // Process the input
	        String response = getAnswer(question);

	        // Print the response
	        System.out.println(response);

	        // Check if the user wants to exit
	        if (question.equalsIgnoreCase("exit")) {
	            isStay = false;
	        } else {
	            System.out.println("CSer : Anything else? (type your question or type exit to return to the main menu)");
	        }
	    }

	    // Exit message
	    System.out.println(
	            "\n=============================================================================================================");
	}

	// fn to find answer by keyword
	 public String getAnswer(String question) {
		    if (question == null || question.trim().isEmpty()) {
		        return "CSer : Please type your question.";
		    }

		    ArrayList<CsQuestion> questionList = customerServiceDAO.getTable_question();
		    String lowerQ = question.replaceAll("[^a-zA-Z0-9\\s]", " ").toLowerCase().trim();

		    if (lowerQ.equalsIgnoreCase("exit")) {
		        return "CSer : Goodbye!";
		    }

		    LinkedHashMap<CsQuestion, Integer> keywordCount = new LinkedHashMap<>();
		    for (int i = 0; i < questionList.size(); i++) {
		        int count = 0;
		        for (String keyword : questionList.get(i).getQuestion()) {
		            String regex = "\\b" + keyword.toLowerCase() + "\\b";
		            Matcher matcher = Pattern.compile(regex).matcher(lowerQ);
		            while (matcher.find()) {
		                count++;
		            }
		        }
		        if (count > 0) {
		            keywordCount.put(questionList.get(i), keywordCount.getOrDefault(questionList.get(i), 0) + count);
		        }
		    }

		    if (keywordCount.isEmpty()) {
		        return "CSer : Your question seems to be new. Please ask via email for further assistance.";
		    }

		    CsQuestion maxKey = null;
		    int maxVal = 0;
		    for (CsQuestion key : keywordCount.keySet()) {
		        if (keywordCount.get(key) > maxVal) {
		            maxKey = key;
		            maxVal = keywordCount.get(key);
		        }
		    }

		    for (int i = 0; i < questionList.size(); i++) {
		        if (maxKey == questionList.get(i)) {
		            return "CSer : " + questionList.get(i).getAnswer();
		        }
		    }

		    return "";
		}

	// Subscribe and receive messages
	public void subscribeUser(String id) {
		User user = userDAO.getUser_fromUserTable(id);
		messageCenter.registerObserver(user);
		System.out.println("You have subscribed to receive messages.");
	}

	public void checkIn() {
        LocalDate today = LocalDate.now();

        if (currentUser.getLastSignInDate() != null && currentUser.getLastSignInDate().equals(today)) {
            System.out.println("You have already checked in today.");
            return;
        }

        currentUser.setLastSignInDate(today);

        int pointsEarned = 10; // 
        currentUser.setPoints(currentUser.getPoints() + pointsEarned);
        System.out.println(currentUser.getUsername() + " successful CheckIn " + pointsEarned + " Points Earned!");

        
        if (generateRandomCoupon(currentUser)) {
            System.out.println("Congratulations! You have received a coupon.");
        } else {
            System.out.println("Sorry, you did not receive a coupon this time.");
        }

		System.out.println(currentUser.getUsername() + " Points: " + currentUser.getPoints());
		
    }

    public boolean generateRandomCoupon(User currentUser) {
		final String[] COUPON_TYPES = {"Amount", "Discount"};
		final String COUPON_CODE_PREFIX = "COUPON";

        Random random = new Random();
        String couponType = COUPON_TYPES[random.nextInt(COUPON_TYPES.length)];
        String couponCode = COUPON_CODE_PREFIX + random.nextInt(10000);
        LocalDate expiryDate = LocalDate.now().plusDays(7);
        double discount=0;
        CouponFactory cuponFactory;
        boolean generateCoupon = random.nextBoolean();
        if(generateCoupon) {

	        if (couponType.equals("Amount")) {
	            discount = 10 + (50 - 10) * random.nextDouble();
	           cuponFactory = new AmountCouponFactory();
	            
	        } else {
	            discount = 0.01 + (0.2 - 0.05) * random.nextDouble();
	            cuponFactory = new DiscountCouponFactory();
	        }
	        currentUser.addCoupon(cuponFactory.createCoupon(discount, couponCode, expiryDate));
        }
        
        return generateCoupon;
    }

	// BEGINNING OF ADMIN FUNCTIONS //

	public void manageTrainSchedule(Scanner scanner) {
		boolean managing = true;
		while (managing) {
			System.out.println("\n--- Manage Train Schedule ---");
			System.out.println("1. Add Train");
			System.out.println("2. Remove Train");
			System.out.println("3. Update Train");
			System.out.println("4. View All Trains");
			System.out.println("5. Back to Admin Menu");
			System.out.print("Choose an option: ");
			int choice;
			try {
				choice = scanner.nextInt();
				scanner.nextLine();
			} catch (InputMismatchException e) {
				System.out.println("Invalid input. Please enter a number between 1 and 5.");
				scanner.nextLine(); // Clear invalid input
				continue;
			}

			switch (choice) {
				case 1:
					addTrain(scanner);
					break;

				case 2:
					removeTrain(scanner);
					break;

				case 3:
					updateTrain(scanner);
					break;

				case 4:
					System.out.println("\n--- All Available Trains ---");
					displayTrains_available();
					break;

				case 5:
					managing = false;
					break;

				default:
					System.out.println("Invalid option. Please try again.");
			}
		}
	}

	private void addTrain(Scanner scanner) {
		System.out.println("\n--- Add a New Train ---");
		System.out.println("--- Open Hour: 10:00 - 17:00 ---");

		String trainID = ("trainId_" + (trainDAO.getTable_train().size() + 1));

		System.out.print("Enter Departure Station: ");
		String departure = scanner.nextLine();

		System.out.print("Enter Arrival Station: ");
		String arrival = scanner.nextLine();

		System.out.print("Enter Departure Date (YYYY-MM-DD): ");
		String date = scanner.nextLine();

		System.out.print("Enter Departure Time (HH:MM): ");
		String time = scanner.nextLine();

		// check if there is a train with the same departure, arrival, date, and time
		// --> conflict
		boolean isConflict = check_TrainConflict(departure, arrival, date, time);
		if (isConflict) {
			String newTimeVal = rearrange_new_train_schedule(departure, arrival, date, time);
			if (newTimeVal == null) {
				System.out.println("No available time slot for the given date. Please try another date.");
				return;
			} else {
				time = newTimeVal;
				System.out.println(
						"Overlap time slot detected. System automatically rearranged to a compatible time slot. New time slot: "
								+ time);

			}
		}
		int totalSeats = 24;

		System.out.print("Enter Ticket Price: ");
		double price;
		try {
			price = Double.parseDouble(scanner.nextLine());
			if (price < 0) {
				System.out.println("Price cannot be negative.");
				return;
			}
		} catch (NumberFormatException e) {
			System.out.println("Invalid number format for price.");
			return;
		}

		Train newTrain = new Train(trainID, departure, arrival, date, time, totalSeats, price);
		trainDAO.addTrain_fromTrainTable(newTrain);
		System.out.println("Train added successfully.");
	}

	private boolean check_TrainConflict(String departure, String arrival, String date, String time) {
		for (Train train : trainDAO.getTable_train()) {
			if (train.getDeparture().equals(departure) && train.getArrival().equals(arrival)
					&& train.getDate().equals(date) && train.getTime().equals(time)) {
				return true;
			}
		}
		return false;
	}

	private String rearrange_new_train_schedule(String departure, String arrival, String date, String time) {
		List<Train> sortedTrains;

		// Step 1: Sort out trains with the same departure, arrival, and date
		sortedTrains = trainDAO.getTable_train().stream()
				.filter(train -> train.getDeparture().equals(departure) && train.getArrival().equals(arrival)
						&& train.getDate().equals(date))
				.sorted(Comparator.comparing(Train::getTime))
				.collect(Collectors.toList());

		// Step 2: Find the first available time slot
		String newTime = "10:00";

		for (Train train : sortedTrains) {
			if (train.getTime().equals(newTime)) {
				newTime = getNextAvailableTime(newTime);
				if (newTime.equals("18:00")) {
					return null;
				}
			} else {
				break;
			}
		}

		return newTime;
	}

	private String getNextAvailableTime(String currentTime) {
		// Logic to get the next available time
		// This is a placeholder implementation
		// You can implement a more sophisticated logic based on your requirements
		String[] timeParts = currentTime.split(":");
		int hour = Integer.parseInt(timeParts[0]);
		int minute = Integer.parseInt(timeParts[1]);

		// Increment time by 15 minutes
		hour += 1;
		return String.format("%02d:%02d", hour, minute);
	}

	private void removeTrain(Scanner scanner) {
		System.out.println("\n--- Remove an Existing Train ---");
		System.out.print("Enter Train ID to Delete: ");
		String trainID = scanner.nextLine();

		boolean isDeleted = trainDAO.deleteTrain_fromTrainTable(trainID);
		if (isDeleted) {
			System.out.println("Train deleted successfully.");
		} else {
			System.out.println("Train ID not found. Deletion failed.");
		}
	}

	private void updateTrain(Scanner scanner) {
		System.out.println("\n--- Update Train Details ---");
		System.out.print("Enter Train ID to Update: ");
		String trainID = scanner.nextLine();

		Train existingTrain = trainDAO.getTrain_fromTrainTable(trainID);
		if (existingTrain == null) {
			System.out.println("Train ID not found.");
			return;
		}

		System.out.println("Leave the field empty if you do not want to change it.");

		System.out.print("Enter New Departure Station (Current: " + existingTrain.getDeparture() + "): ");
		String departure = scanner.nextLine();
		if (!departure.isEmpty()) {
			existingTrain.setDeparture(departure);
		}

		System.out.print("Enter New Arrival Station (Current: " + existingTrain.getArrival() + "): ");
		String arrival = scanner.nextLine();
		if (!arrival.isEmpty()) {
			existingTrain.setArrival(arrival);
		}

		System.out.print("Enter New Departure Date (YYYY-MM-DD) (Current: " + existingTrain.getDate() + "): ");
		String date = scanner.nextLine();
		if (!date.isEmpty()) {
			existingTrain.setDate(date);
		}

		System.out.print("Enter New Departure Time (HH:MM) (Current: " + existingTrain.getTime() + "): ");
		String time = scanner.nextLine();
		if (!time.isEmpty()) {
			existingTrain.setTime(time);
		}

		System.out.print("Enter New Total Seats (Current: " + existingTrain.getAvailableSeats() + "): ");
		String totalSeatsStr = scanner.nextLine();
		if (!totalSeatsStr.isEmpty()) {
			try {
				int totalSeats = Integer.parseInt(totalSeatsStr);
				if (totalSeats > 0) {
					existingTrain.setAvailableSeats(totalSeats);
				} else {
					System.out.println("Total seats must be a positive number. Skipping update for seats.");
				}
			} catch (NumberFormatException e) {
				System.out.println("Invalid number format for seats. Skipping update for seats.");
			}
		}

		System.out.print("Enter New Ticket Price (Current: " + existingTrain.getPrice() + "): ");
		String priceStr = scanner.nextLine();
		if (!priceStr.isEmpty()) {
			try {
				double price = Double.parseDouble(priceStr);
				if (price >= 0) {
					existingTrain.setPrice(price);
				} else {
					System.out.println("Price cannot be negative. Skipping update for price.");
				}
			} catch (NumberFormatException e) {
				System.out.println("Invalid number format for price. Skipping update for price.");
			}
		}

		trainDAO.updateTrain_fromTrainTable(existingTrain);
		System.out.println("Train details updated successfully.");
	}

	public void csAdmin(Scanner scanner) {
		boolean isStay = true;
		System.out.println(
				"\n=============================================================================================================");
		while (isStay) {
			String answer = "";
			String keywordInput = null;
			ArrayList<String> keywords = new ArrayList<>();
			
			System.out.println("Enter keywords (separated by commas):");
			keywordInput = scanner.nextLine();
			if (keywordInput.equals("exit")) {
				isStay = false;
			}
			if(isStay != false) {
				System.out.println("Enter answer:");
				answer = scanner.nextLine();
			}
			if (answer.equals("exit")) {
				isStay = false;
			}
			if(isStay != false) {
				System.out.println(addQA(keywordInput, answer));
			}
		}
		System.out.println(
				"\n=============================================================================================================");
	}

	// fn to find answer by keyword
	public String addQA(String keywords, String answer) {
	    ArrayList<CsQuestion> questionList = customerServiceDAO.getTable_question();
	    Set<String> keywordSet = new HashSet<>();
	    
	    if (keywords == null || keywords.trim().isEmpty()) {
	        return "Keywords cannot be empty.";
	    }
	    if (answer == null || answer.trim().isEmpty()) {
	        return "Answer cannot be empty.";
	    }
	    String[] keywordArray = keywords.split(",");
	    for (String keyword : keywordArray) {
	        keyword = keyword.trim().toLowerCase();
	        if (keyword.isEmpty()) {
	            return "Keywords list fields cannot be empty.";
	        }
	        keywordSet.add(keyword);
	    }
	    
	    for (CsQuestion question : questionList) {
	        for (String existingKeyword : question.getQuestion()) {
	            if (keywordSet.contains(existingKeyword)) {
	                return "Keywords already exist.";
	            }
	        }
	    }
	    if (customerServiceDAO.addQA(new CsQuestion(new ArrayList<>(keywordSet), answer))) {
	        return "QA added successfully.";
	    }else {
	    	return null;
	    }
	}

	// Report methods for admin
	public void viewReports(Scanner scanner) {
		Database db = Database.getInstance();
		boolean viewing = true;

		while (viewing) {
			System.out.println("\n===== SYSTEM REPORT MENU =====");
			System.out.println("1. User Report");
			System.out.println("2. Train Report");
			System.out.println("3. Order Report");
			System.out.println("4. Exit Report Menu");
			System.out.print("Choose a report to view: ");
			int reportChoice = -1;

			try {
				reportChoice = scanner.nextInt();
				scanner.nextLine(); // Consume newline
			} catch (InputMismatchException e) {
				System.out.println("Invalid input. Please enter a number between 1 and 4.");
				scanner.nextLine(); // Clear invalid input
				continue;
			}

			switch (reportChoice) {
				case 1:
					generateUserReport(scanner, db.getTable_user());
					break;
				case 2:
					generateTrainReport(scanner, db.getTable_train());
					break;
				case 3:
					generateOrderReport(scanner, db.getTable_orderRecord(), db.getTable_train());
					break;
				case 4:
					viewing = false;
					System.out.println("Exiting Report Menu.");
					break;
				default:
					System.out.println("Invalid option. Please choose between 1 and 4.");
			}
		}
	}

	private void generateUserReport(Scanner scanner, List<User> users) {
		System.out.println("\n--- User Report ---");
		System.out.println("Would you like to apply filters? (Y/N)");
		String applyFilter = scanner.nextLine().trim().toUpperCase();

		List<User> filteredUsers = new ArrayList<>(users);

		if (applyFilter.equals("Y")) {
			System.out.println("Select filter option:");
			System.out.println("1. Filter by Role");
			System.out.println("2. Search by Username");
			System.out.println("3. Both Role and Username");
			System.out.print("Choose an option: ");
			int filterChoice = -1;

			try {
				filterChoice = scanner.nextInt();
				scanner.nextLine();
			} catch (InputMismatchException e) {
				System.out.println("Invalid input. Skipping filters.");
				scanner.nextLine();
				filterChoice = -1;
			}

			switch (filterChoice) {
				case 1:
					filteredUsers = filterUsersByRole(scanner, users);
					break;
				case 2:
					filteredUsers = searchUsersByUsername(scanner, users);
					break;
				case 3:
					filteredUsers = filterUsersByRole(scanner, users);
					filteredUsers = searchUsersByUsername(scanner, filteredUsers);
					break;
				default:
					System.out.println("Invalid filter option. Showing all users.");
			}
		}

		System.out.println("\nTotal Users: " + filteredUsers.size());
		long normalUsers = filteredUsers.stream()
				.filter(user -> user.getRole().equalsIgnoreCase("normal"))
				.count();
		long adminUsers = filteredUsers.stream()
				.filter(user -> user.getRole().equalsIgnoreCase("admin"))
				.count();
		System.out.println("Normal Users: " + normalUsers);
		System.out.println("Admin Users: " + adminUsers);

		System.out.print("Would you like to list user details? (Y/N): ");
		String listDetails = scanner.nextLine().trim().toUpperCase();
		if (listDetails.equals("Y")) {
			System.out.println("\n--- User Details ---");
			for (User user : filteredUsers) {
				System.out.println("Username: " + user.getUsername() +
						", Role: " + user.getRole() +
						", ID: " + user.getId());
			}
		}
	}

	private void generateTrainReport(Scanner scanner, List<Train> trains) {
		System.out.println("\n--- Train Report ---");
		System.out.println("Would you like to apply filters? (Y/N)");
		String applyFilter = scanner.nextLine().trim().toUpperCase();

		List<Train> filteredTrains = new ArrayList<>(trains);

		if (applyFilter.equals("Y")) {
			System.out.println("Select filter option:");
			System.out.println("1. Search by Train ID");
			System.out.println("2. Filter by Departure/Arrival Station");
			System.out.println("3. Filter by Date Range");
			System.out.println("4. Combine Filters");
			System.out.print("Choose an option: ");
			int filterChoice = -1;

			try {
				filterChoice = scanner.nextInt();
				scanner.nextLine();
			} catch (InputMismatchException e) {
				System.out.println("Invalid input. Skipping filters.");
				scanner.nextLine();
				filterChoice = -1;
			}

			switch (filterChoice) {
				case 1:
					filteredTrains = searchTrainsById(scanner, trains);
					break;
				case 2:
					filteredTrains = filterTrainsByStation(scanner, trains);
					break;
				case 3:
					filteredTrains = filterTrainsByDateRange(scanner, trains);
					break;
				case 4:
					filteredTrains = searchTrainsById(scanner, trains);
					filteredTrains = filterTrainsByStation(scanner, filteredTrains);
					filteredTrains = filterTrainsByDateRange(scanner, filteredTrains);
					break;
				default:
					System.out.println("Invalid filter option. Showing all trains.");
			}
		}

		System.out.println("\nTotal Trains: " + filteredTrains.size());
		for (Train train : filteredTrains) {
			int soldSeats = train.getAvailableSeats() - train.getAvailableSeats();
			System.out.println("Train ID: " + train.getTrainNumber() +
					", Departure: " + train.getDeparture() +
					", Arrival: " + train.getArrival() +
					", Date: " + train.getDate() +
					", Time: " + train.getTime() +
					", Total Seats: " + train.getAvailableSeats() +
					", Sold Seats: " + soldSeats +
					", Price: $" + String.format("%.2f", train.getPrice()));
		}
	}

	private void generateOrderReport(Scanner scanner, List<OrderRecord> orders, List<Train> trains) {
		System.out.println("\n--- Order Report ---");
		System.out.println("Would you like to apply filters? (Y/N)");
		String applyFilter = scanner.nextLine().trim().toUpperCase();

		List<OrderRecord> filteredOrders = new ArrayList<>(orders);

		if (applyFilter.equals("Y")) {
			System.out.println("Select filter option:");
			System.out.println("1. Search by Order ID");
			System.out.println("2. Filter by User ID");
			System.out.println("3. Filter by Train ID");
			System.out.println("4. Filter by Date Range");
			System.out.println("5. Combine Filters");
			System.out.print("Choose an option: ");
			int filterChoice = -1;

			try {
				filterChoice = scanner.nextInt();
				scanner.nextLine();
			} catch (InputMismatchException e) {
				System.out.println("Invalid input. Skipping filters.");
				scanner.nextLine();
				filterChoice = -1;
			}

			switch (filterChoice) {
				case 1:
					filteredOrders = searchOrdersById(scanner, orders);
					break;
				case 2:
					filteredOrders = filterOrdersByUserId(scanner, orders);
					break;
				case 3:
					filteredOrders = filterOrdersByTrainId(scanner, orders);
					break;
				case 4:
					filteredOrders = filterOrdersByDateRange(scanner, orders);
					break;
				case 5:
					filteredOrders = searchOrdersById(scanner, orders);
					filteredOrders = filterOrdersByUserId(scanner, filteredOrders);
					filteredOrders = filterOrdersByTrainId(scanner, filteredOrders);
					filteredOrders = filterOrdersByDateRange(scanner, filteredOrders);
					break;
				default:
					System.out.println("Invalid filter option. Showing all orders.");
			}
		}

		System.out.println("\nTotal Orders: " + filteredOrders.size());
		double totalRevenue = filteredOrders.stream()
				.mapToDouble(OrderRecord::getAmount)
				.sum();
		System.out.printf("Total Revenue: $%.2f\n", totalRevenue);

		Map<String, List<OrderRecord>> ordersByTrain = filteredOrders.stream()
				.collect(Collectors.groupingBy(OrderRecord::getTrainId));

		System.out.println("\nOrders and Revenue by Train:");
		for (Map.Entry<String, List<OrderRecord>> entry : ordersByTrain.entrySet()) {
			String trainId = entry.getKey();
			List<OrderRecord> trainOrders = entry.getValue();
			double trainRevenue = trainOrders.stream()
					.mapToDouble(OrderRecord::getAmount)
					.sum();
			System.out.println("Train ID: " + trainId +
					", Orders: " + trainOrders.size() +
					", Revenue: $" + String.format("%.2f", trainRevenue));
		}
	}

	private List<User> filterUsersByRole(Scanner scanner, List<User> users) {
		System.out.println("Select Role to Filter:");
		System.out.println("1. Normal");
		System.out.println("2. Admin");
		System.out.print("Choose an option: ");
		int roleChoice = -1;

		try {
			roleChoice = scanner.nextInt();
			scanner.nextLine();
		} catch (InputMismatchException e) {
			System.out.println("Invalid input. Skipping Role filter.");
			scanner.nextLine(); // Clear invalid input
			return users;
		}

		final String role;
		switch (roleChoice) {
			case 1:
				role = "normal";
				break;
			case 2:
				role = "admin";
				break;
			default:
				System.out.println("Invalid role option. Skipping Role filter.");
				return users;
		}

		return users.stream()
				.filter(user -> user.getRole().equalsIgnoreCase(role))
				.collect(Collectors.toList());
	}

	private List<User> searchUsersByUsername(Scanner scanner, List<User> users) {
		System.out.print("Enter the username to search (supports partial matches): ");
		String usernameSearch = scanner.nextLine().trim().toLowerCase();

		return users.stream()
				.filter(user -> user.getUsername().toLowerCase().contains(usernameSearch))
				.collect(Collectors.toList());
	}

	private List<Train> searchTrainsById(Scanner scanner, List<Train> trains) {
		System.out.print("Enter the Train ID to search: ");
		String trainIdSearch = scanner.nextLine().trim().toLowerCase();

		return trains.stream()
				.filter(train -> train.getTrainNumber().toLowerCase().contains(trainIdSearch))
				.collect(Collectors.toList());
	}

	private List<Train> filterTrainsByStation(Scanner scanner, List<Train> trains) {
		System.out.print("Enter Departure Station to filter (leave blank to skip): ");
		String departure = scanner.nextLine().trim().toLowerCase();

		System.out.print("Enter Arrival Station to filter (leave blank to skip): ");
		String arrival = scanner.nextLine().trim().toLowerCase();

		return trains.stream()
				.filter(train -> (departure.isEmpty() || train.getDeparture().toLowerCase().contains(departure)) &&
						(arrival.isEmpty() || train.getArrival().toLowerCase().contains(arrival)))
				.collect(Collectors.toList());
	}

	private List<Train> filterTrainsByDateRange(Scanner scanner, List<Train> trains) {
		System.out.print("Enter Start Date (YYYY-MM-DD) or leave blank to skip: ");
		String startDateStr = scanner.nextLine().trim();

		System.out.print("Enter End Date (YYYY-MM-DD) or leave blank to skip: ");
		String endDateStr = scanner.nextLine().trim();

		final LocalDate startDateFinal;
		final LocalDate endDateFinal;

		try {

			startDateFinal = !startDateStr.isEmpty() ? LocalDate.parse(startDateStr) : null;
			endDateFinal = !endDateStr.isEmpty() ? LocalDate.parse(endDateStr) : null;
		} catch (DateTimeParseException e) {
			System.out.println("Invalid date format. Skipping Date Range filter.");
			return trains;
		}

		return trains.stream()
				.filter(train -> {
					LocalDate trainDate = LocalDate.parse(train.getDate());
					boolean afterStart = (startDateFinal == null) || (!trainDate.isBefore(startDateFinal));
					boolean beforeEnd = (endDateFinal == null) || (!trainDate.isAfter(endDateFinal));
					return afterStart && beforeEnd;
				})
				.collect(Collectors.toList());
	}

	private List<OrderRecord> searchOrdersById(Scanner scanner, List<OrderRecord> orders) {
		System.out.print("Enter the Order ID to search: ");
		String orderIdSearch = scanner.nextLine().trim().toLowerCase();

		return orders.stream()
				.filter(order -> order.getOrderId().toLowerCase().contains(orderIdSearch))
				.collect(Collectors.toList());
	}

	private List<OrderRecord> filterOrdersByUserId(Scanner scanner, List<OrderRecord> orders) {
		System.out.print("Enter the User ID to filter orders: ");
		String userIdSearch = scanner.nextLine().trim().toLowerCase();

		return orders.stream()
				.filter(order -> order.getUserId().toLowerCase().contains(userIdSearch))
				.collect(Collectors.toList());
	}

	private List<OrderRecord> filterOrdersByTrainId(Scanner scanner, List<OrderRecord> orders) {
		System.out.print("Enter the Train ID to filter orders: ");
		String trainIdSearch = scanner.nextLine().trim().toLowerCase();

		return orders.stream()
				.filter(order -> order.getTrainId().toLowerCase().contains(trainIdSearch))
				.collect(Collectors.toList());
	}

	private List<OrderRecord> filterOrdersByDateRange(Scanner scanner, List<OrderRecord> orders) {
		System.out.print("Enter Start Date (YYYY-MM-DD) or leave blank to skip: ");
		String startDateStr = scanner.nextLine().trim();

		System.out.print("Enter End Date (YYYY-MM-DD) or leave blank to skip: ");
		String endDateStr = scanner.nextLine().trim();
		final LocalDate startDateFinal = !startDateStr.isEmpty() ? LocalDate.parse(startDateStr) : null;
		final LocalDate endDateFinal = !endDateStr.isEmpty() ? LocalDate.parse(endDateStr) : null;

		if (startDateFinal != null && endDateFinal != null && startDateFinal.isAfter(endDateFinal)) {
			System.out.println("Start Date cannot be after End Date. Skipping Date Range filter.");
			return orders;
		}

		return orders.stream()
				.filter(order -> {
					LocalDate orderDate = order.getOrderDate().toInstant()
							.atZone(ZoneId.systemDefault())
							.toLocalDate();
					boolean afterStart = (startDateFinal == null) || (!orderDate.isBefore(startDateFinal));
					boolean beforeEnd = (endDateFinal == null) || (!orderDate.isAfter(endDateFinal));
					return afterStart && beforeEnd;
				})
				.collect(Collectors.toList());
	}

	// User Mangement
	public void manageUsers(Scanner scanner) {
		boolean managing = true;
		while (managing) {
			System.out.println("\n--- Manage Users ---");
			System.out.println("1. List All Users");
			System.out.println("2. Add New User");
			System.out.println("3. Remove User");
			System.out.println("4. Change User Role");
			System.out.println("5. Return to Admin Menu");
			System.out.print("Choose an option: ");

			int choice = -1;
			try {
				choice = scanner.nextInt();
				scanner.nextLine();
			} catch (InputMismatchException e) {
				System.out.println("Invalid input. Please enter a number between 1 and 5.");
				scanner.nextLine();
				continue;
			}

			switch (choice) {
				case 1:
					listAllUsers();
					break;
				case 2:
					addNewUser(scanner);
					break;
				case 3:
					removeUser(scanner);
					break;
				case 4:
					changeUserRole(scanner);
					break;
				case 5:
					managing = false;
					System.out.println("Returning to Admin Menu.");
					break;
				default:
					System.out.println("Invalid option. Please choose between 1 and 5.");
			}
		}
	}

	private void listAllUsers() {
		ArrayList<User> users = userDAO.getUserList();
		if (users.isEmpty()) {
			System.out.println("\nNo users found in the system.");
			return;
		}

		System.out.println("\n--- Registered Users ---");
		System.out.printf("%-10s %-20s %-10s\n", "User ID", "Username", "Role");
		System.out.println("-------------------------------------------------");
		for (User user : users) {
			System.out.printf("%-10s %-20s %-10s\n", user.getId(), user.getUsername(), user.getRole());
		}
	}

	private void addNewUser(Scanner scanner) {
		System.out.println("\n--- Add New User ---");

		System.out.print("Enter Username: ");
		String username = scanner.nextLine().trim();
		if (username.isEmpty()) {
			System.out.println("Username cannot be empty.");
			return;
		}

		if (userDAO.usernameExists(username)) {
			System.out.println("Username already exists. Please choose a different username.");
			return;
		}

		System.out.print("Enter Password: ");
		String password = scanner.nextLine().trim();
		if (password.isEmpty()) {
			System.out.println("Password cannot be empty.");
			return;
		}

		System.out.println("Select Role:");
		System.out.println("1. Normal User");
		System.out.println("2. Administrator");
		System.out.print("Choose an option: ");

		int roleChoice = -1;
		try {
			roleChoice = scanner.nextInt();
			scanner.nextLine();
		} catch (InputMismatchException e) {
			System.out.println("Invalid input. Role set to 'normal user' by default.");
			scanner.nextLine();
			roleChoice = 1;
		}

		String role = "normal";
		if (roleChoice == 2) {
			role = "admin";
		}

		boolean success = userDAO.register(role, username, password);
		if (success) {
			System.out.println("User added successfully.");
		} else {
			System.out.println("Failed to add user. Please try again.");
		}
	}

	private void removeUser(Scanner scanner) {
		System.out.println("\n--- Remove User ---");
		System.out.print("Enter the User ID or Username to remove: ");
		String input = scanner.nextLine().trim();

		User userToRemove = null;
		userToRemove = userDAO.getUser_fromUserTable(input);
		if (userToRemove == null) {
			userToRemove = userDAO.getUserByUsername(input);
		}

		if (userToRemove == null) {
			System.out.println("User not found.");
			return;
		}

		if (userToRemove.getId().equals(currentUser.getId())) {
			System.out.println("You cannot remove your own account.");
			return;
		}

		System.out.print("Are you sure you want to remove user '" + userToRemove.getUsername() + "'? (Y/N): ");
		String confirmation = scanner.nextLine().trim().toUpperCase();
		if (!confirmation.equals("Y")) {
			System.out.println("User removal canceled.");
			return;
		}

		boolean success = userDAO.deleteUser_fromUserTable(userToRemove.getId());
		if (success) {
			System.out.println("User removed successfully.");
		} else {
			System.out.println("Failed to remove user. Please try again.");
		}
	}

	private void changeUserRole(Scanner scanner) {
		System.out.println("\n--- Change User Role ---");
		System.out.print("Enter the User ID or Username to modify: ");
		String input = scanner.nextLine().trim();

		User userToModify = null;
		userToModify = userDAO.getUser_fromUserTable(input);
		if (userToModify == null) {
			userToModify = userDAO.getUserByUsername(input);
		}

		if (userToModify == null) {
			System.out.println("User not found.");
			return;
		}

		if (userToModify.getId().equals(currentUser.getId())) {
			System.out.println("You cannot change your own role.");
			return;
		}

		System.out.println("Current Role: " + userToModify.getRole());
		System.out.println("Select New Role:");
		System.out.println("1. Normal User");
		System.out.println("2. Administrator");
		System.out.print("Choose an option: ");

		int roleChoice = -1;
		try {
			roleChoice = scanner.nextInt();
			scanner.nextLine();
		} catch (InputMismatchException e) {
			System.out.println("Invalid input. Role not changed.");
			scanner.nextLine();
			return;
		}

		String newRole = userToModify.getRole();
		if (roleChoice == 1) {
			newRole = "normal";
		} else if (roleChoice == 2) {
			newRole = "admin";
		} else {
			System.out.println("Invalid role option. Role not changed.");
			return;
		}

		if (newRole.equalsIgnoreCase(userToModify.getRole())) {
			System.out.println("User already has the role '" + newRole + "'. No changes made.");
			return;
		}

		userToModify.setRole(newRole);
		boolean success = userDAO.updateUser_fromUserTable(userToModify);
		
		if (success) {
			System.out.println("User role updated successfully to '" + newRole + "'.");
		} else {
			System.out.println("Failed to update user role. Please try again.");
		}
	}

	public void updateAnnouncement(String announcement) {
		messageCenter.updateAnnouncement(announcement);
	}

	public void notifyAllUsers() {
		messageCenter.notifyObservers();
	}

	public void displayUserList() {
		userDAO.printUserList();
	}
}
