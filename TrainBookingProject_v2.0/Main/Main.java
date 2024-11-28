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
                        train_ticket_system.cs(scanner);
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
                        train_ticket_system.viewReports(scanner);
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
                        train_ticket_system.csAdmin(scanner);
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
}
