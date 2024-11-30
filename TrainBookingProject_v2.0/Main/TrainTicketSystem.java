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

	private TrainTicketSystem() {
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
	

	public User login(String username, String password) {

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

	public boolean register(String username, String password) {


		if (!userDAO.usernameExists(username)) {
			System.out.println("Register successfully.");
		} else {
			System.out.println("Username already exists.");
		}

		System.out.println("");
		return userDAO.register("normal", username, password);
	}

	// fn to get finished orders
	public ArrayList<OrderRecord> getFinishedOrders() {
		ArrayList<OrderRecord> finishedOrders = new ArrayList<OrderRecord>();
		ArrayList<OrderRecord> orderRecordList = orderRecordDAO.getOrdersByUserId(currentUser.getId());

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
	
	public Train getTrain(String trainID) {
		return trainDAO.getTrain_fromTrainTable(trainID);
	}
	
	public boolean rateOrder(OrderRecord order, int rating) {
		if (rating < 1 || rating > 5) {
			return false;
		} else {
			order.setRating(rating);
			return orderRecordDAO.updateOrderRecord(order);
		}
	}
	
	public boolean hasOrders() {
		return orderRecordDAO.getOrdersByUserId(currentUser.getId()).size() > 0;
	}
	
	public Train selectTrain(int trainChoice) {
		if (trainChoice < 1 || trainChoice > trainDAO.getTable_train().size()) {
			return null;
		} else {
			return trainDAO.getTable_train().get(trainChoice - 1);
		}
	}

	public int displayTrains_available() {
		System.out.println("\n--- All Available Trains ---");
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

	public double getTotalPriceWithDiscount(User currentUser, double totalPrice) {
		double discount = currentUser.getMember().getDiscount();

		double result = 0;

		result = totalPrice - (totalPrice * discount);
		if (currentUser.getCouponList().size() > 0) {
			double discout = userDAO.useCoupon(result, currentUser);
			result = result - discout;
			System.out.println("You use a coupon. Get discount: " + discout);
		}

		return result;
	}

	// recommend train according to user's order history
	public void displayRecommendations(String id, String location) {
		ArrayList<String> recommendedTrainIds = recommendTrains(id, location);
		ArrayList<Train> availableTrains = new ArrayList<>();

		for (int i = 0; i < recommendedTrainIds.size(); i++) {
			Train train = trainDAO.getTrain_fromTrainTable(recommendedTrainIds.get(i));
			availableTrains.add(train);
		}

		System.out.println(
				"\n=============================================================================================================");
		System.out.println("Top 3 Train Recommendations:");

		if (availableTrains.size() == 0) {
			System.out.println("No recommendations available.");
		} else {
			for (int i = 0; i < availableTrains.size(); i++) {
				System.out.println(availableTrains.get(i).toString());
			}
		}

		System.out.println(
				"\n=============================================================================================================");
	}

	// fn to provide recommendations
	public ArrayList<String> recommendTrains(String id, String location) {
		ArrayList<OrderRecord> orderRecordList = orderRecordDAO.getOrdersByUserId(id);
		ArrayList<OrderRecord> filteredOrderRecordList = new ArrayList<OrderRecord>();

		if (orderRecordList.isEmpty()) {
			return new ArrayList<>();
		} else {
			// Check for available trains
		        for (OrderRecord orderRecord : orderRecordList) {
		            Train train = trainDAO.getTrain_fromTrainTable(orderRecord.getTrainId());
		            if (train.getStatus().equals("active") && train.getAvailableSeats() > 0) {
		                filteredOrderRecordList.add(orderRecord);
		            }
		        }
				
			if (!location.equals("None")) {
				// Filter out orders that do not match the location
		    		filteredOrderRecordList.removeIf(orderRecord -> {
					Train train = trainDAO.getTrain_fromTrainTable(orderRecord.getTrainId());
					return !(train.getDeparture().equals(location) || train.getArrival().equals(location));
				});
			} else {
				filteredOrderRecordList = orderRecordList;
			}
				
			Map<String, Integer> trainRating = new HashMap<>();
			for (OrderRecord orderRecord : filteredOrderRecordList) {
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
			ArrayList<String> recommendedTrainIds = new ArrayList<>(sortedTrainIds.subList(0, endIndex));
			System.out.println(recommendedTrainIds.toString());
			
			return recommendedTrainIds;
		}
	}

	// arrange single seat
	public ArrayList<String> arrangeSeat(String trainNumber) {
		Train targetTrain = trainDAO.getTrain_fromTrainTable(trainNumber);
		int availableSeats = targetTrain.getAvailableSeats();
		ArrayList<String> seatNumbers = new ArrayList<>();

		if (availableSeats == 0) {
			return seatNumbers;
		}

		seatPlan target_seatPlan = seatPlanDAO.getSeatPlan_ByTrainID(trainNumber);
		String result = seatPlanDAO.getOneAvailableSeat(target_seatPlan);

		// test
		System.out.println("Arranged seat: " + result);
		System.out.println("SeatPlan: " + target_seatPlan.toString());

		availableSeats--;
		targetTrain.setAvailableSeats(availableSeats);
		seatNumbers.add(result);
		return seatNumbers;
	}

	// arrange seats together, > 1 passengers
	public ArrayList<String> arrangeSeat(String trainNumber, int passengerCount) {
		Train targetTrain = trainDAO.getTrain_fromTrainTable(trainNumber);
		int availableSeats = targetTrain.getAvailableSeats();
		ArrayList<String> result = new ArrayList<>();

		if (availableSeats < passengerCount) {
			return result;
		}

		seatPlan target_seatPlan = seatPlanDAO.getSeatPlan_ByTrainID(trainNumber);
		result = seatPlanDAO.getMultipleAvailableSeat(target_seatPlan, passengerCount);

		// test
		System.out.println("Arranged seat: " + result);
		System.out.println("SeatPlan: " + target_seatPlan.toString());

		availableSeats -= passengerCount;
		targetTrain.setAvailableSeats(availableSeats);
		return result;

	}
	
	public OrderRecord createOrder(String trainID, double totalPrice, ArrayList<Ticket> ticketList) {
		OrderRecord orderRecord = new OrderRecord(
				String.format("orderID_%s_%s", currentUser.getId(),
						orderRecordDAO.getOrdersByUserId(currentUser.getId()).size()),
				currentUser.getId(), trainID, new Date(), getTotalPriceWithDiscount(currentUser, totalPrice), ticketList);
		orderRecordDAO.addOrderRecord(orderRecord);
		return orderRecord;
    }

//	public void summarizeOrders(User currentUser) {
//		ArrayList<OrderRecord> userOrders = orderRecordDAO.getOrdersByUserId(currentUser.getId());
//		double avgRating = calculateAverageRating(userOrders);
//		HashMap<String, Integer> destVisitCount = calculateDestinationVisitCount(userOrders);
//		Map<String, Double> destAvgRating = calculateDestinationAvgRating(userOrders);
//
//		// Find most visited destinations
//		int maxVisitCount = Collections.max(destVisitCount.values());
//		// Find highest rated destinations
//		double maxAvgRating = Collections.max(destAvgRating.values()); 
//
//		// Output summary
//		System.out.printf("Summary of your orders:\n");
//		System.out.printf("Average rating of your orders: %.2f\n", avgRating);
//		System.out.printf("Your most visited destination(s) (%d times each): %s\n", maxVisitCount,
//				String.join(", ", getMostVisitedDestination(userOrders)));
//		System.out.printf("Your highest rated destination(s) (Average of %.2f rating each): %s\n\n", maxAvgRating,
//				String.join(", ", getMaxAvgRatingDestination(userOrders)));
//	}

	public double calculateAverageRating() {
		ArrayList<OrderRecord> userOrders = orderRecordDAO.getOrdersByUserId(currentUser.getId());
		int ratingSum = 0;
		for (OrderRecord order : userOrders) {
			ratingSum += order.getRating();
		}
		return ratingSum / (double) userOrders.size();
	}

	public ArrayList<String> getMostVisitedDestination() {
		ArrayList<String> mostVisitedDest = new ArrayList<>();
		HashMap<String, Integer> destVisitCount = calculateDestinationVisitCount();
		int maxVisitCount = Collections.max(destVisitCount.values());

		for (Map.Entry<String, Integer> entry : destVisitCount.entrySet()) {
			if (entry.getValue() == maxVisitCount) {
				mostVisitedDest.add(entry.getKey());
			}
		}

		return mostVisitedDest;
	}

	public HashMap<String, Integer> calculateDestinationVisitCount() {
		ArrayList<OrderRecord> userOrders = orderRecordDAO.getOrdersByUserId(currentUser.getId());
		HashMap<String, Integer> destVisitCount = new HashMap<>();
		for (OrderRecord order : userOrders) {
			String dest = trainDAO.getTrain_fromTrainTable(order.getTrainId()).getArrival();
			destVisitCount.put(dest, destVisitCount.getOrDefault(dest, 0) + 1);
		}
		return destVisitCount;
	}

	public ArrayList<String> getMaxAvgRatingDestination() {
		ArrayList<String> maxAvgRatingDest = new ArrayList<>();
		HashMap<String, Double> destAvgRating = calculateDestinationAvgRating();
		double maxAvgRating = Collections.max(destAvgRating.values());

		for (Map.Entry<String, Double> entry : destAvgRating.entrySet()) {
			if (entry.getValue() == maxAvgRating) {
				maxAvgRatingDest.add(entry.getKey());
			}
		}

		return maxAvgRatingDest;
	}

	public HashMap<String, Double> calculateDestinationAvgRating() {
		ArrayList<OrderRecord> userOrders = orderRecordDAO.getOrdersByUserId(currentUser.getId());
		HashMap<String, Double> destTotalRating = new HashMap<>();
		HashMap<String, Double> destAvgRating = new HashMap<>();
		HashMap<String, Integer> destVisitCount = calculateDestinationVisitCount();

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
	
	public int displayOrders() {
		ArrayList<OrderRecord> userOrders = orderRecordDAO.getOrdersByUserId(currentUser.getId());
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
	
	public OrderRecord selectOrder(int orderChoice) {
		if (orderChoice < 1 || orderChoice > trainDAO.getTable_train().size()) {
			return null;
		} else {
			return orderRecordDAO.getTable_orderRecord().get(orderChoice - 1);
		}
	}
	
	public ArrayList<Ticket> getTicketList(OrderRecord order) {
		return order.getTicketList();
	}
	
	public void modifyPassengerInfo(Ticket ticket, String newName, int newAge) {
		ticket.setName(newName);
		ticket.setAge(newAge);
	}
	
	public boolean cancelOrder(OrderRecord order) {
		String trainId = order.getTrainId();
		Train train = trainDAO.getTrain_fromTrainTable(trainId);
		int passengerCount = order.getTicketList().size();
		train.setAvailableSeats(train.getAvailableSeats() + passengerCount);
		trainDAO.updateTrain_fromTrainTable(train);
		return orderRecordDAO.deleteOrderRecord(order.getOrderId());
	}

	// fn to find answer by keyword
	public String getAnswer(String question) {
		if (question.isEmpty()) {
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

		return "CSer : Your question seems to be new. Please ask via email for further assistance.";
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
		final String[] COUPON_TYPES = { "Amount", "Discount" };
		final String COUPON_CODE_PREFIX = "COUPON";

		Random random = new Random();
		String couponType = COUPON_TYPES[random.nextInt(COUPON_TYPES.length)];
		String couponCode = COUPON_CODE_PREFIX + random.nextInt(10000);
		LocalDate expiryDate = LocalDate.now().plusDays(7);
		double discount = 0;
		CouponFactory cuponFactory;
		boolean generateCoupon = random.nextBoolean();
		if (generateCoupon) {

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

	// BEGINNING OF ADMIN FUNCTIONS

	// public void manageTrainSchedule(Scanner scanner) {
	// boolean managing = true;
	// while (managing) {
	// System.out.println("\n--- Manage Train Schedule ---");
	// System.out.println("1. Add Train");
	// System.out.println("2. Remove Train");
	// System.out.println("3. Update Train");
	// System.out.println("4. View All Trains");
	// System.out.println("5. Back to Admin Menu");
	// System.out.print("Choose an option: ");
	// int choice;
	// choice = scanner.nextInt();
	// scanner.nextLine();

	// switch (choice) {
	// case 1:
	// addTrain(scanner);
	// break;

	// case 2:
	// removeTrain(scanner);
	// break;

	// case 3:
	// updateTrain(scanner);
	// break;

	// case 4:
	// System.out.println("\n--- All Available Trains ---");
	// displayTrains_available();
	// break;

	// case 5:
	// managing = false;
	// break;

	// default:
	// System.out.println("Invalid option. Please try again.");
	// }
	// }
	// }

	public ArrayList<String> addTrain(String departure, String arrival, String date, String time, double price) {
		// System.out.println("\n--- Add a New Train ---");
		// System.out.println("--- Open Hour: 10:00 - 17:00 ---");

		// String trainID = ("trainId_" + (trainDAO.getTable_train().size() + 1));

		// System.out.print("Enter Departure Station: ");
		// String departure = scanner.nextLine();

		// System.out.print("Enter Arrival Station: ");
		// String arrival = scanner.nextLine();

		// System.out.print("Enter Departure Date (YYYY-MM-DD): ");
		// String date = scanner.nextLine();

		// System.out.print("Enter Departure Time (HH:MM): ");
		// String time = scanner.nextLine();

		// If there is a train with the same departure, arrival, date, and time
		// --> conflict
		ArrayList<String> returnVal = new ArrayList<>();
		String trainID = ("trainId_" + (trainDAO.getTable_train().size() + 1));
		boolean isConflict = check_TrainConflict(departure, arrival, date, time);
		if (isConflict) {
			String newTimeVal = rearrange_new_train_schedule(departure, arrival, date, time);
			if (newTimeVal == null) {
				System.out.println("No available time slot for the given date. Please try another date.");
				returnVal.add("false");
				returnVal.add("No available time slot");
			} else {
				time = newTimeVal;
				System.out.println(
						"Overlap time slot detected. System automatically rearranged to a compatible time slot. New time slot: "
								+ time);
				returnVal.add("true");
				returnVal.add("Overlap time slot detected, new time slot: " + time);
			}
		}
		int totalSeats = 24;
		Train newTrain = new Train(trainID, departure, arrival, date, time, totalSeats, price);
		trainDAO.addTrain_fromTrainTable(newTrain);
		returnVal.add("true");
		returnVal.add("Train added successfully: " + time);
		System.out.println("Train added successfully.");
		return returnVal;
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

	public ArrayList<String> removeTrain(String trainID) {
		boolean isDeleted = trainDAO.deleteTrain_fromTrainTable(trainID);
		ArrayList<String> returnVal = new ArrayList<>();
		if (isDeleted) {
			returnVal.add("true");
			returnVal.add("Train deleted successfully.");
			System.out.println("Train deleted successfully.");
		} else {
			returnVal.add("false");
			returnVal.add("Train ID not found. Deletion failed.");
			System.out.println("Train ID not found. Deletion failed.");
		}
		return returnVal;
	}

	public ArrayList<String> updateTrain(String trainID, String departure, String arrival, String date, String time,
			String priceStr) {
		ArrayList<String> returnVal = new ArrayList<>();
		Train existingTrain = trainDAO.getTrain_fromTrainTable(trainID);
		if (existingTrain == null) {
			System.out.println("Train ID not found.");
			returnVal.add("false");
			returnVal.add("Train ID not found.");
			return returnVal;
		}

		if (!departure.isEmpty()) {
			existingTrain.setDeparture(departure);
		}

		if (!arrival.isEmpty()) {
			existingTrain.setArrival(arrival);
		}

		if (!date.isEmpty()) {
			existingTrain.setDate(date);
		}

		if (!time.isEmpty()) {
			existingTrain.setTime(time);
		}

		if (!priceStr.isEmpty()) {
			double price = Double.parseDouble(priceStr);
			if (price >= 0) {
				existingTrain.setPrice(price);
			} else {
				System.out.println("Price cannot be negative. Skipping update for price.");
				returnVal.add("false");
				returnVal.add("Price cannot be negative.");
				return returnVal;
			}
		}

		trainDAO.updateTrain_fromTrainTable(existingTrain);
		System.out.println("Train details updated successfully.");
		returnVal.add("true");
		returnVal.add("Train details updated successfully.");
		returnVal.add(existingTrain.toString());
		return returnVal;
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
		if (customerServiceDAO.addQA(new ArrayList<>(keywordSet), answer)) {
			return "QA added successfully.";
		} else {
			return null;
		}
	}

	// Report methods for admin


	public List<User> filterUsersByRole(List<User> users, int roleChoice) {
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

	public List<User> searchUsersByUsername(String usernameSearch, List<User> users) {
		return users.stream()
				.filter(user -> user.getUsername().toLowerCase().contains(usernameSearch))
				.collect(Collectors.toList());
	}

	public List<Train> searchTrainsById(String trainIdSearch, List<Train> trains) {
		return trains.stream()
				.filter(train -> train.getTrainNumber().toLowerCase().contains(trainIdSearch))
				.collect(Collectors.toList());
	}

	public List<Train> filterTrainsByStation(String departure,String arrival, List<Train> trains) {
		return trains.stream()
				.filter(train -> (departure.isEmpty() || train.getDeparture().toLowerCase().contains(departure)) &&
						(arrival.isEmpty() || train.getArrival().toLowerCase().contains(arrival)))
				.collect(Collectors.toList());
	}

	public List<Train> filterTrainsByDateRange(LocalDate startDateFinal,LocalDate endDateFinal, List<Train> trains) {
		
		return trains.stream()
				.filter(train -> {
					LocalDate trainDate = LocalDate.parse(train.getDate());
					boolean afterStart = (startDateFinal == null) || (!trainDate.isBefore(startDateFinal));
					boolean beforeEnd = (endDateFinal == null) || (!trainDate.isAfter(endDateFinal));
					return afterStart && beforeEnd;
				})
				.collect(Collectors.toList());
	}

	public List<OrderRecord> searchOrdersById(String orderIdSearch, List<OrderRecord> orders) {
		return orders.stream()
				.filter(order -> order.getOrderId().toLowerCase().contains(orderIdSearch))
				.collect(Collectors.toList());
	}

	public List<OrderRecord> filterOrdersByUserId(String userIdSearch, List<OrderRecord> orders) {
		return orders.stream()
				.filter(order -> order.getUserId().toLowerCase().contains(userIdSearch))
				.collect(Collectors.toList());
	}

	public List<OrderRecord> filterOrdersByTrainId(String trainIdSearch, List<OrderRecord> orders) {
		return orders.stream()
				.filter(order -> order.getTrainId().toLowerCase().contains(trainIdSearch))
				.collect(Collectors.toList());
	}

	public List<OrderRecord> filterOrdersByDateRange(LocalDate startDateFinal,LocalDate endDateFinal,List<OrderRecord> orders) {

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
	public void listAllUsers() {
		ArrayList<User> users = userDAO.getUserList();
		System.out.println("\n--- Registered Users ---");
		System.out.printf("%-10s %-20s %-10s\n", "User ID", "Username", "Role");
		System.out.println("-------------------------------------------------");
		for (User user : users) {
			System.out.printf("%-10s %-20s %-10s\n", user.getId(), user.getUsername(), user.getRole());
		}
	}
	public void addNewUser(String username, String password, String role) {
		
		if (userDAO.usernameExists(username)) {
			System.out.println("Username already exists. Please choose a different username.");
			return;
		}
		boolean success = userDAO.register(role, username, password);
		if (success) {
			System.out.println("User added successfully.");
		} 
	}
	public void removeUser(String input) {
		
		User userToRemove = null;
		userToRemove = userDAO.getUser_fromUserTable(input);
		if (userToRemove == null) {
			userToRemove = userDAO.getUserByUsername(input);
		}
		if (userToRemove == null) {
			System.out.println("User not found.");
			
		}
		return;
	}
	public void changeUserRole(String input, int roleChoice) {
		User userToModify = null;
		userToModify = userDAO.getUser_fromUserTable(input);
		if (userToModify == null) {
			userToModify = userDAO.getUserByUsername(input);
		}
//		if (userToModify == null) {
//			System.out.println("User not found.");
//			return;
//		}
//
//		if (userToModify.getId().equals(currentUser.getId())) {
//			System.out.println("You cannot change your own role.");
//			return;
//		}
//
//		
//
//		String newRole = userToModify.getRole();
//		if (roleChoice == 1) {
//			newRole = "normal";
//		} else if (roleChoice == 2) {
//			newRole = "admin";
//		} else {
//			System.out.println("Invalid role option. Role not changed.");
//			return;
//		}
//
//		if (newRole.equalsIgnoreCase(userToModify.getRole())) {
//			System.out.println("User already has the role '" + newRole + "'. No changes made.");
//			return;
//		}
//
//		userToModify.setRole(newRole);
//		boolean success = userDAO.updateUser_fromUserTable(userToModify);
//
//		if (success) {
//			System.out.println("User role updated successfully to '" + newRole + "'.");
//		} else {
//			System.out.println("Failed to update user role. Please try again.");
//		}
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
