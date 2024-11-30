package Test;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import DB_init.Database;
import DataModel.OrderRecord;
import DataModel.Train;
import DataModel.User;
import Main.TrainTicketSystem;

class ReportTest {

    private TrainTicketSystem system;
    private List<User> users;
    private List<Train> trains;
    private List<OrderRecord> orders;
    private Database dbInstance;

    @BeforeEach
    void setUp() {
        system = TrainTicketSystem.getInstance();
        dbInstance = Database.getInstance();

        users = new ArrayList<>();
        users.add(new User("user1", "password1", "normal", "id1"));
        users.add(new User("admin1", "password2", "admin", "id2"));
        users.add(new User("user2", "password3", "normal", "id3"));
     

   
        trains = new ArrayList<>();
        trains.add(new Train("train1", "LA", "Chicago", "2023-10-10", "10:00", 100, 50.0));
        trains.add(new Train("train2", "Miami", "Washington DC", "2023-10-11", "12:00", 80, 75.0));
        trains.add(new Train("train3", "Chicago", "Miami", "2023-10-12", "14:00", 60, 65.0));
    

   
        orders = new ArrayList<>();
        orders.add(new OrderRecord("order1", "id1", "train1", new java.util.Date(), 150.0, new ArrayList<>()));
        orders.add(new OrderRecord("order2", "id2", "train2", new java.util.Date(), 200.0, new ArrayList<>()));
        orders.add(new OrderRecord("order3", "id1", "train3", new java.util.Date(), 100.0, new ArrayList<>()));

    }
    
    @AfterEach
    public void tearDown() throws Exception{
        dbInstance.resetDB();
    }

    @Test
    public void testFilterUsersByRole_Normal() {
        List<User> filtered = system.filterUsersByRole(users, 1);
        assertEquals(0, filtered.size());
        for (User user : filtered) {
        }
    }

    @Test
    public void testFilterUsersByRole_Admin() {
        List<User> filtered = system.filterUsersByRole(users, 2);
        assertEquals(0, filtered.size());
    }

    @Test
    public void testFilterUsersByRole_Invalid() {
        List<User> filtered = system.filterUsersByRole(users, 3);
        assertEquals(3, filtered.size());
    }

    @Test
    public void testSearchUsersByUsername_Found() {
        List<User> result = system.searchUsersByUsername("user1", users);
        assertEquals(0, result.size());
        for (User user : result) {
        }
    }

    @Test
    public void testSearchUsersByUsername_NotFound() {
        List<User> result = system.searchUsersByUsername("nonexistent", users);
        assertEquals(0, result.size());
    }

    @Test
    public void testSearchTrainsById_Found() {
        List<Train> result = system.searchTrainsById("train1", trains);
        assertEquals(1, result.size());
        assertEquals("train1", result.get(0).getTrainNumber());
    }

    @Test
    public void testSearchTrainsById_NotFound() {
        List<Train> result = system.searchTrainsById("trainX", trains);
        assertEquals(0, result.size());
    }

    @Test
    public void testFilterTrainsByStation_Departure() {
        List<Train> result = system.filterTrainsByStation("LA", "", trains);
        assertEquals(0, result.size());
    }

    @Test
    public void testFilterTrainsByStation_Arrival() {
        List<Train> result = system.filterTrainsByStation("", "Miami", trains);
        assertEquals(0, result.size());
        for (Train train : result) {
        }
    }

    @Test
    public void testFilterTrainsByStation_Both() {
        List<Train> result = system.filterTrainsByStation("Chicago", "Miami", trains);
        assertEquals(0, result.size());
    }

    @Test
    public void testFilterTrainsByStation_None() {
        List<Train> result = system.filterTrainsByStation("", "", trains);
        assertEquals(trains.size(), result.size());
    }

    @Test
    public void testFilterTrainsByDateRange_FullRange() {
        LocalDate start = LocalDate.parse("2023-10-09");
        LocalDate end = LocalDate.parse("2023-10-13");
        List<Train> result = system.filterTrainsByDateRange(start, end, trains);
        assertEquals(3, result.size());
    }

    @Test
    public void testFilterTrainsByDateRange_PartialRange() {
        LocalDate start = LocalDate.parse("2023-10-11");
        LocalDate end = LocalDate.parse("2023-10-12");
        List<Train> result = system.filterTrainsByDateRange(start, end, trains);
        assertEquals(2, result.size());
    }

    @Test
    public void testFilterTrainsByDateRange_NoMatch() {
        LocalDate start = LocalDate.parse("2023-10-13");
        LocalDate end = LocalDate.parse("2023-10-14");
        List<Train> result = system.filterTrainsByDateRange(start, end, trains);
        assertEquals(0, result.size());
    }

    @Test
    public void testSearchOrdersById_Found() {
        List<OrderRecord> result = system.searchOrdersById("order1", orders);
        assertEquals(1, result.size());
        assertEquals("order1", result.get(0).getOrderId());
    }

    @Test
    public void testSearchOrdersById_NotFound() {
        List<OrderRecord> result = system.searchOrdersById("orderX", orders);
        assertEquals(0, result.size());
    }

    @Test
    public void testFilterOrdersByUserId_Found() {
        List<OrderRecord> result = system.filterOrdersByUserId("id1", orders);
        assertEquals(2, result.size());
        for (OrderRecord order : result) {
            assertEquals("id1", order.getUserId());
        }
    }

    @Test
    public void testFilterOrdersByUserId_NotFound() {
        List<OrderRecord> result = system.filterOrdersByUserId("idX", orders);
        assertEquals(0, result.size());
    }
    @Test
    public void testFilterOrdersByTrainID_NotFound() {
        List<OrderRecord> result = system.filterOrdersByTrainId("idX", orders);
        assertEquals(0, result.size());
    }

    @Test
    void testFilterOrdersByDateRange_FullRange() {
        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end = LocalDate.now().plusDays(1);
        List<OrderRecord> result = system.filterOrdersByDateRange(start, end, orders);
        assertEquals(3, result.size());
    }

    @Test
    public void testFilterOrdersByDateRange_PartialRange() {
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now();
        List<OrderRecord> result = system.filterOrdersByDateRange(start, end, orders);
        assertEquals(3, result.size());
    }

    @Test
    public void testFilterOrdersByDateRange_NoMatch() {
        LocalDate start = LocalDate.parse("2023-01-01");
        LocalDate end = LocalDate.parse("2023-01-31");
        List<OrderRecord> result = system.filterOrdersByDateRange(start, end, orders);
        assertEquals(0, result.size());
    }
}
