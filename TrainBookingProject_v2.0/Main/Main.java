package Main;

import java.util.*;
import DataModel.*;

public class Main {
    public static void main(String[] args) {
        TrainTicketSystem train_ticket_system = TrainTicketSystem.getInstance();
        Scanner scanner = new Scanner(System.in);
        User current_LoginedUser = null;

        // Main menu
        // Scenario: current_LoginedUser == null

        while (current_LoginedUser == null) {
            System.out.println("Welcome to Train Ticket System!"); 
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Please select an option: ");
            int option = scanner.nextInt();
            scanner.nextLine();
 
            switch (option) {
                case 1:
                    
            		System.out.print("Enter username: ");
            		String username = scanner.nextLine();
            		System.out.print("Enter password: ");
            		String password = scanner.nextLine();
            		current_LoginedUser = train_ticket_system.login(username, password);
                    break;

                case 2:
            		System.out.print("Registration Form:\nEnter username: ");
            		username = scanner.nextLine();
            		System.out.print("Enter password: ");
            		password = scanner.nextLine();
            		
                    train_ticket_system.register(username, password);
                    break;

                case 3:
                    System.out.println("Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid option. Please try again.");
            }

            // while loop for normal user
            while (current_LoginedUser != null && current_LoginedUser.getRole() == "normal") {
                // check announcement
                if (current_LoginedUser.isReceivedAnnouncement()) {
                    System.out.println(
                            "You have received an announcement: " + current_LoginedUser.getLatestAnnouncement());
                }

                // display finished orders
                train_ticket_system.displayFinishedOrders(current_LoginedUser.getId(), scanner);

                System.out.println();
                System.out.println("1. Book Tickets");
                System.out.println("2. View Orders");
                System.out.println("3. Customer Service");
                System.out.println("4. Subscribe and receive messages");
                System.out.println("5. Daily CheckIn");
                System.out.println("6. Logout");
                System.out.print("Please select an option: ");
                option = scanner.nextInt();
                scanner.nextLine();

                switch (option) {
                    case 1:
                        train_ticket_system.bookTickets(scanner);
                        break;

                    case 2:
                        train_ticket_system.viewOrders(scanner);
                        break;

                    // CS function
                    case 3:
                    	cs(train_ticket_system,scanner);
                        break;

                    case 4:
                        // Subscribe and receive messages
                        train_ticket_system.subscribeUser(current_LoginedUser.getId());
                        break;

                    case 5:
                        // Check in
                        train_ticket_system.checkIn();
                        break;

                    case 6:
                        // LOGOUT
                        current_LoginedUser = null;
                        System.out.println("Logged out successfully.\n");
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            }

            while (current_LoginedUser != null && current_LoginedUser.getRole() == "admin") {
                System.out.println("1. Manage Train Schedule");
                System.out.println("2. View Reports");
                System.out.println("3. Display Users");
                System.out.println("4. Logout");
                System.out.println("5. Add Keyword and Answer");
                System.out.println("6. Update & publish an announcement");
                System.out.println("7. Cancel announcement");
                System.out.println("8. Manage User");
                
                option = scanner.nextInt();
                scanner.nextLine();

                switch (option) {
                    case 1:
                        System.out.println("Manage Train Schedule");
                        // ... Implement Manage Train Schedule functionality
                        manageTrainSchedule(train_ticket_system, scanner);
                        break;
                    case 2:
                        viewReports(train_ticket_system,scanner);
                        break;
                    case 3:
                        System.out.println("Display User List");
                        train_ticket_system.displayUserList();
                        break;
                    case 4:
                        // Logout
                        current_LoginedUser = null;
                        System.out.println("Logged out successfully.\n");
                        break;
                    case 5:
                    	csAdmin(train_ticket_system,scanner);
                        break;
                    case 6:
                        // Update an Announcement
                        System.out.println("Enter the announcement:");
                        String announcement = scanner.nextLine();
                        train_ticket_system.updateAnnouncement(announcement);
                        System.out.println("Announcement updated successfully.\n");
                        break;
                    case 7:
                        // Cancel an Announcement
                        train_ticket_system.updateAnnouncement(null);
                        System.out.println("Announcement cancelled successfully.\n");
                        break;
                    case 8:
                        // Cancel an Announcement
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
                					train_ticket_system.listAllUsers();
                					break;
                				case 2:
                					System.out.println("\n--- Add New User ---");
                					System.out.print("Enter Username: ");
                					String username = scanner.nextLine();
                					if (username.isEmpty()) {
                						System.out.println("Username cannot be empty.");
                						return;
                					}
                					System.out.print("Enter Password: ");
                					String password = scanner.nextLine();
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
                					train_ticket_system.addNewUser(username, password, role);
                					break;
                				case 3:
                					System.out.println("\n--- Remove User ---");
                					System.out.print("Enter the User ID or Username to remove: ");
                					String input = scanner.nextLine().trim();
                					train_ticket_system.removeUser(input);
                					break;
                				case 4:
                					System.out.println("\n--- Change User Role ---");
                					System.out.print("Enter the User ID or Username to modify: ");
                					input = scanner.nextLine().trim();
                					
                					
                					System.out.println("Select New Role:");
                					System.out.println("1. Normal User");
                					System.out.println("2. Administrator");
                					System.out.print("Choose an option: ");

                					roleChoice = -1;
                					try {
                						roleChoice = scanner.nextInt();
                						scanner.nextLine();
                					} catch (InputMismatchException e) {
                						System.out.println("Invalid input. Role not changed.");
                						scanner.nextLine();
                						return;
                					}
                					train_ticket_system.changeUserRole(input, roleChoice);
                					break;
                				case 5:
                					managing = false;
                					System.out.println("Returning to Admin Menu.");
                					break;
                				default:
                					System.out.println("Invalid option. Please choose between 1 and 5.");
                			}
                		}
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            }
        }
    }

    // helper function for displaying menu, should not be included in the TrainTicketSystem Controller
    private static void manageTrainSchedule(TrainTicketSystem train_ticket_system, Scanner scanner) {
        System.out.println("\n--- Manage Train Schedule ---");
        System.out.println("1. Add Train");
        System.out.println("2. Remove Train");
        System.out.println("3. Update Train");
        System.out.println("4. View All Trains");
        System.out.println("5. Back to Admin Menu");
        System.out.print("Choose an option: ");
        int choice;
            choice = scanner.nextInt();
            scanner.nextLine();
        

        switch (choice) {
            case 1:
                System.out.println("\n--- Add a New Train ---");
                System.out.println("--- Open Hour: 10:00 - 17:00 ---");
                System.out.print("Enter Departure Station: ");
                String departure = scanner.nextLine();
                System.out.print("Enter Arrival Station: ");
                String arrival = scanner.nextLine();
                System.out.print("Enter Departure Date (YYYY-MM-DD): ");
                String date = scanner.nextLine();
                System.out.print("Enter Departure Time (HH:MM): ");
                String time = scanner.nextLine();
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
                train_ticket_system.addTrain(departure, arrival, date, time, price);
                break;

            case 2:
                System.out.println("\n--- Remove an Existing Train ---");
                System.out.print("Enter Train ID to Delete: ");
                String trainID = scanner.nextLine();
                train_ticket_system.removeTrain(trainID);
                break;

            case 3:
                System.out.println("\n--- Update Train Details ---");
                System.out.print("Enter Train ID to Update: ");
                trainID = scanner.nextLine();
                System.out.println("Leave the field empty if you do not want to change it.");
                System.out.print("Enter New Departure Station: ");
                departure = scanner.nextLine();
                System.out.print("Enter New Arrival Station: ");
                arrival = scanner.nextLine();
                System.out.print("Enter New Departure Date (YYYY-MM-DD): ");
                date = scanner.nextLine();
                System.out.print("Enter New Departure Time (HH:MM): ");
                time = scanner.nextLine();

                System.out.print("Enter New Ticket Price: ");
                String priceStr = scanner.nextLine();

                train_ticket_system.updateTrain(trainID, departure, arrival, date, time, priceStr);
                break;

            case 4:
                System.out.println("\n--- All Available Trains ---");
                train_ticket_system.displayTrains_available();
                break;

            case 5:
                break;

            default:
                System.out.println("Invalid option. Please try again.");
        }
    }
    
    public static void cs(TrainTicketSystem train_ticket_system,Scanner scanner) {
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
			String response = train_ticket_system.getAnswer(question);

			// Print the response
			System.out.println(response);

			// Check if the user wants to exit
			if (question.equalsIgnoreCase("exit")) {
				isStay = false;
			} else {
				System.out
						.println("CSer : Anything else? (type your question or type exit to return to the main menu)");
			}
		}

		// Exit message
		System.out.println(
				"\n=============================================================================================================");
	}
    
    public static void csAdmin(TrainTicketSystem train_ticket_system,Scanner scanner) {
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
			if (isStay != false) {
				System.out.println("Enter answer:");
				answer = scanner.nextLine();
			}
			if (answer.equals("exit")) {
				isStay = false;
			}
			if (isStay != false) {
				System.out.println(train_ticket_system.addQA(keywordInput, answer));
			}
		}
		System.out.println(
				"\n=============================================================================================================");
	}
	public static void viewReports(TrainTicketSystem trainTicketSystem,Scanner scanner) {
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
				    generateUserReport(trainTicketSystem,scanner, db.getTable_user());
					break;
				case 2:
					generateTrainReport(trainTicketSystem,scanner, db.getTable_train());
					break;
				case 3:
					generateOrderReport(trainTicketSystem,scanner, db.getTable_orderRecord(), db.getTable_train());
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

	private static void generateUserReport(TrainTicketSystem trainTicketSystem,Scanner scanner, List<User> users) {
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
					filteredUsers = filterUsersByRole(trainTicketSystem,scanner, users);
					break;
				case 2:
					filteredUsers = searchUsersByUsername(trainTicketSystem,scanner, users);
					break;
				case 3:
					filteredUsers = filterUsersByRole(trainTicketSystem,scanner, users);
					filteredUsers = searchUsersByUsername(trainTicketSystem,scanner, filteredUsers);
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

	private static void generateTrainReport(TrainTicketSystem trainTicketSystem ,Scanner scanner, List<Train> trains) {
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
					filteredTrains = searchTrainsById(trainTicketSystem,scanner, trains);
					break;
				case 2:
					filteredTrains = filterTrainsByStation(trainTicketSystem,scanner, trains);
					break;
				case 3:
					filteredTrains = filterTrainsByDateRange(trainTicketSystem,scanner, trains);
					break;
				case 4:
					filteredTrains = searchTrainsById(trainTicketSystem,scanner, trains);
					filteredTrains = filterTrainsByStation(trainTicketSystem,scanner, filteredTrains);
					filteredTrains = filterTrainsByDateRange(trainTicketSystem,scanner, filteredTrains);
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

	private static void generateOrderReport(TrainTicketSystem trainTicketSystem,Scanner scanner, List<OrderRecord> orders, List<Train> trains) {
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
					filteredOrders = searchOrdersById(trainTicketSystem,scanner, orders);
					break;
				case 2:
					filteredOrders = filterOrdersByUserId(trainTicketSystem,scanner, orders);
					break;
				case 3:
					filteredOrders = filterOrdersByTrainId(trainTicketSystem,scanner, orders);
					break;
				case 4:
					filteredOrders = filterOrdersByDateRange(trainTicketSystem,scanner, orders);
					break;
				case 5:
					filteredOrders = searchOrdersById(trainTicketSystem,scanner, orders);
					filteredOrders = filterOrdersByUserId(trainTicketSystem,scanner, filteredOrders);
					filteredOrders = filterOrdersByTrainId(trainTicketSystem,scanner, filteredOrders);
					filteredOrders = filterOrdersByDateRange(trainTicketSystem,scanner, filteredOrders);
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
    public static List<User> filterUsersByRole(TrainTicketSystem trainTicketSystem, Scanner scanner, List<User> users) {
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
            scanner.nextLine(); 
            return users; 
        }
        return trainTicketSystem.filterUsersByRole(users, roleChoice);
    }

    public static List<Train> filterTrainsByStation(TrainTicketSystem trainTicketSystem,Scanner scanner, List<Train> trains) {
		System.out.print("Enter Departure Station to filter (leave blank to skip): ");
		String departure = scanner.nextLine().trim().toLowerCase();

		System.out.print("Enter Arrival Station to filter (leave blank to skip): ");
		String arrival = scanner.nextLine().trim().toLowerCase();

		return trainTicketSystem.filterTrainsByStation(departure, arrival, trains);
	}
    public static List<Train> filterTrainsByDateRange(TrainTicketSystem trainTicketSystem,Scanner scanner, List<Train> trains) {
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

		return trainTicketSystem.filterTrainsByDateRange(startDateFinal, endDateFinal, trains);
	}
    public static List<OrderRecord> searchOrdersById(TrainTicketSystem trainTicketSystem,Scanner scanner, List<OrderRecord> orders) {
		System.out.print("Enter the Order ID to search: ");
		String orderIdSearch = scanner.nextLine().trim().toLowerCase();

		return trainTicketSystem.searchOrdersById(orderIdSearch, orders);
	}
    public static List<OrderRecord> filterOrdersByUserId(TrainTicketSystem ticketSystem,Scanner scanner, List<OrderRecord> orders) {
		System.out.print("Enter the User ID to filter orders: ");
		String userIdSearch = scanner.nextLine().trim().toLowerCase();

		return ticketSystem.filterOrdersByUserId(userIdSearch, orders);
	}
    public static List<OrderRecord> filterOrdersByTrainId(TrainTicketSystem trainTicketSystem,Scanner scanner, List<OrderRecord> orders) {
		System.out.print("Enter the Train ID to filter orders: ");
		String trainIdSearch = scanner.nextLine().trim().toLowerCase();

        return trainTicketSystem.filterOrdersByTrainId(trainIdSearch, orders);
	}
	public static List<OrderRecord> filterOrdersByDateRange(TrainTicketSystem trainTicketSystem,Scanner scanner, List<OrderRecord> orders) {
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

		return trainTicketSystem.filterOrdersByDateRange(startDateFinal, endDateFinal, orders);
	}
    public static List<User> searchUsersByUsername(TrainTicketSystem trainTicketSystem,Scanner scanner, List<User> users) {
		System.out.print("Enter the username to search (supports partial matches): ");
		String usernameSearch = scanner.nextLine().trim().toLowerCase();

		return trainTicketSystem.searchUsersByUsername(usernameSearch, users);
	}
    public static List<Train> searchTrainsById(TrainTicketSystem trainTicketSystem,Scanner scanner, List<Train> trains) {
		System.out.print("Enter the Train ID to search: ");
		String trainIdSearch = scanner.nextLine().trim().toLowerCase();

		return trainTicketSystem.searchTrainsById(trainIdSearch, trains);
	}
}
