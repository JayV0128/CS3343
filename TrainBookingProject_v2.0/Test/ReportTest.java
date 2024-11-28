package Test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import Main.TrainTicketSystem;
import DataModel.*;
import DAO.*;
import DB_init.Database;

import java.util.*;
import java.time.LocalDate;

class TrainTicketSystemReportTest {

    private TrainTicketSystem trainTicketSystem;
    private Database dbInstance;

    @BeforeEach
    void setUp() {
        trainTicketSystem = new TrainTicketSystem();
        dbInstance = Database.getInstance();

        // Initialize data for testing
        dbInstance.getTable_user().clear();
        dbInstance.getTable_train().clear();
        dbInstance.getTable_orderRecord().clear();

        // Add Users
        dbInstance.getTable_user().add(new User("admin", "userID_1", "adminUser", "pass"));
        dbInstance.getTable_user().add(new User("normal", "userID_2", "normalUser1", "pass"));
        dbInstance.getTable_user().add(new User("normal", "userID_3", "normalUser2", "pass"));

        // Add Trains
        dbInstance.getTable_train().add(new Train("trainID_1", "CityA", "CityB", "2024-10-01", "12:00", 100, 50.0));
        dbInstance.getTable_train().add(new Train("trainID_2", "CityB", "CityC", "2024-10-02", "13:00", 80, 60.0));

        // Add Order Records
        dbInstance.getTable_orderRecord().add(new OrderRecord("orderID_1", "userID_2", "trainID_1", new Date(), 100.0, new ArrayList<>()));
        dbInstance.getTable_orderRecord().add(new OrderRecord("orderID_2", "userID_3", "trainID_2", new Date(), 120.0, new ArrayList<>()));
    }

    @AfterEach
    void tearDown() {
        dbInstance.getTable_user().clear();
        dbInstance.getTable_train().clear();
        dbInstance.getTable_orderRecord().clear();
        dbInstance.resetDB();
    }

    @Test
    void testGenerateUserReport_FilterByRole() {
        List<User> users = dbInstance.getTable_user();
        List<User> filteredUsers = trainTicketSystem.generateUserReport(users, "normal", null);

        assertEquals(2, filteredUsers.size(), "Should return 2 normal users");
        for (User user : filteredUsers) {
            assertEquals("normal", user.getRole(), "User role should be normal");
        }
    }

    @Test
    void testGenerateUserReport_SearchByUsername() {
        List<User> users = dbInstance.getTable_user();
        List<User> filteredUsers = trainTicketSystem.generateUserReport(users, null, "User1");

        assertEquals(1, filteredUsers.size(), "Should return 1 user with username containing 'User1'");
        assertEquals("normalUser1", filteredUsers.get(0).getUsername(), "Username should be 'normalUser1'");
    }

    @Test
    void testGenerateUserReport_FilterByRoleAndUsername() {
        List<User> users = dbInstance.getTable_user();
        List<User> filteredUsers = trainTicketSystem.generateUserReport(users, "normal", "User2");

        assertEquals(1, filteredUsers.size(), "Should return 1 normal user with username containing 'User2'");
        assertEquals("normalUser2", filteredUsers.get(0).getUsername(), "Username should be 'normalUser2'");
    }

    @Test
    void testGenerateTrainReport_FilterByStation() {
        // Assume similar refactoring for generateTrainReport, which accepts parameters
        List<Train> trains = dbInstance.getTable_train();
        List<Train> filteredTrains = trainTicketSystem.generateTrainReport(trains, null, "CityB", null, null, null);

        assertEquals(2, filteredTrains.size(), "Should return 2 trains departing or arriving at CityB");
    }

    @Test
    void testGenerateTrainReport_FilterByDateRange() {
        List<Train> trains = dbInstance.getTable_train();
        List<Train> filteredTrains = trainTicketSystem.generateTrainReport(trains, null, null, "2024-10-01", "2024-10-01", null);

        assertEquals(1, filteredTrains.size(), "Should return 1 train on 2024-10-01");
        assertEquals("trainID_1", filteredTrains.get(0).getTrainNumber(), "Train ID should be 'trainID_1'");
    }

    @Test
    void testGenerateOrderReport_FilterByUserId() {
        List<OrderRecord> orders = dbInstance.getTable_orderRecord();
        List<OrderRecord> filteredOrders = trainTicketSystem.generateOrderReport(orders, "userID_2", null, null, null, null);

        assertEquals(1, filteredOrders.size(), "Should return 1 order for userID_2");
        assertEquals("orderID_1", filteredOrders.get(0).getOrderId(), "Order ID should be 'orderID_1'");
    }

    @Test
    void testGenerateOrderReport_FilterByDateRange() {
        List<OrderRecord> orders = dbInstance.getTable_orderRecord();
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(1);

        List<OrderRecord> filteredOrders = trainTicketSystem.generateOrderReport(orders, null, null, startDate.toString(), endDate.toString(), null);

        assertEquals(2, filteredOrders.size(), "Should return 2 orders within date range");
    }
}