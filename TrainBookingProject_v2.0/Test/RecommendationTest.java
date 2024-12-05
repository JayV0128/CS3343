package Test;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import java.util.Date;

import DB_init.Database;
import DataModel.OrderRecord;
import DataModel.Ticket;
import DataModel.Train;

import Main.TrainTicketSystem;


public class RecommendationTest {
	
	private Database database = Database.getInstance();
	
	@BeforeEach
	public void setup() {
		database.resetDB();
	}
	
	@Test
	public void testRecommendTrains_1() {
		TrainTicketSystem tts = TrainTicketSystem.getInstance();
		ArrayList<String> result = tts.recommendTrains("userID_2", "None");
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void testRecommendTrains_2() {
		TrainTicketSystem tts = TrainTicketSystem.getInstance();

		database.getTable_train().add(new Train("trainID_1", "LA", "Chicago", "2024-10-01", "12:00", 24, 100));
		
		OrderRecord or = new OrderRecord("orderID_userID_2_0", "userID_2", "trainID_1", new Date(), 1, new ArrayList<Ticket>());
		or.setRating(-1);
		
		database.getTable_orderRecord().add(or);
		
		ArrayList<String> result = tts.recommendTrains("userID_2", "None");
		assertEquals("[trainID_1]", result.toString());
	}
	
	@Test
	public void testRecommendTrains_3() {
		TrainTicketSystem tts = TrainTicketSystem.getInstance();
		
		database.getTable_train().add(new Train("trainID_1", "LA", "Chicago", "2024-10-01", "12:00", 24, 100));
		database.getTable_train().add(new Train("trainID_2", "Washington DC", "Miami", "2024-10-02", "14:00", 24, 150));
		database.getTable_train().add(new Train("trainID_3", "Washington DC", "Miami", "2024-10-03", "14:00", 21, 150));
		
		OrderRecord or1 = new OrderRecord("orderID_userID_2_0", "userID_2", "trainID_1", new Date(), 1, new ArrayList<Ticket>());
		or1.setRating(1);
		
		OrderRecord or2 = new OrderRecord("orderID_userID_2_1", "userID_2", "trainID_2", new Date(), 1, new ArrayList<Ticket>());
		or2.setRating(2);
		
		OrderRecord or3 = new OrderRecord("orderID_userID_2_2", "userID_2", "trainID_3", new Date(), 1, new ArrayList<Ticket>());
		or3.setRating(3);
		
		database.getTable_orderRecord().add(or1);
		database.getTable_orderRecord().add(or2);
		database.getTable_orderRecord().add(or3);
		
		ArrayList<String> result = tts.recommendTrains("userID_2", "None");
		assertEquals("[trainID_3, trainID_2, trainID_1]", result.toString());
	}	
	
	@Test
	public void testRecommendTrains_4() {
		TrainTicketSystem tts = TrainTicketSystem.getInstance();
		
		database.getTable_train().add(new Train("trainID_1", "LA", "Chicago", "2024-10-01", "12:00", 24, 100));
		database.getTable_train().add(new Train("trainID_2", "Washington DC", "Miami", "2024-10-02", "14:00", 24, 150));
		database.getTable_train().add(new Train("trainID_3", "Washington DC", "Miami", "2024-10-03", "14:00", 21, 150));
		
		OrderRecord or1 = new OrderRecord("orderID_userID_2_0", "userID_2", "trainID_1", new Date(), 1, new ArrayList<Ticket>());
		or1.setRating(1);
		
		OrderRecord or2 = new OrderRecord("orderID_userID_2_1", "userID_2", "trainID_2", new Date(), 1, new ArrayList<Ticket>());
		or2.setRating(2);
		
		OrderRecord or3 = new OrderRecord("orderID_userID_2_2", "userID_2", "trainID_3", new Date(), 1, new ArrayList<Ticket>());
		or3.setRating(3);
		
		OrderRecord or4 = new OrderRecord("orderID_userID_2_3", "userID_2", "trainID_1", new Date(), 1, new ArrayList<Ticket>());
		or4.setRating(5);
		
		database.getTable_orderRecord().add(or1);
		database.getTable_orderRecord().add(or2);
		database.getTable_orderRecord().add(or3);
		database.getTable_orderRecord().add(or4);
		
		ArrayList<String> result = tts.recommendTrains("userID_2", "None");
		assertEquals("[trainID_1, trainID_3, trainID_2]", result.toString());
	}	
	
	@Test
	public void testRecommendTrains_5() {
		TrainTicketSystem tts = TrainTicketSystem.getInstance();
		
		database.getTable_train().add(new Train("trainID_1", "LA", "Chicago", "2024-10-01", "12:00", 24, 100));
		database.getTable_train().add(new Train("trainID_2", "Washington DC", "Miami", "2024-10-02", "14:00", 24, 150));
		
		OrderRecord or1 = new OrderRecord("orderID_userID_2_0", "userID_2", "trainID_1", new Date(), 1, new ArrayList<Ticket>());
		or1.setRating(1);
		
		OrderRecord or2 = new OrderRecord("orderID_userID_2_1", "userID_2", "trainID_2", new Date(), 1, new ArrayList<Ticket>());
		or2.setRating(2);
		
		database.getTable_orderRecord().add(or1);
		database.getTable_orderRecord().add(or2);
		
		ArrayList<String> result = tts.recommendTrains("userID_2", "LA");
		assertEquals("[trainID_1]", result.toString());
	}
	
//	@AfterEach
//    	public void tearDown() throws Exception{
//		database.resetDB();
//    	} 
}
