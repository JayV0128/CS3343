package Test;

import org.junit.jupiter.api.*;

import DAO.OrderRecordDAO;
import DB_init.Database;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import DataModel.OrderRecord;
import DataModel.Ticket;
import Main.TrainTicketSystem;

public class OrderSummaryTest {
	@BeforeEach
	public void setUp() {
		Database.getInstance().resetDB();
	}
	
	@Test
    public void testAverageRating() {
		TrainTicketSystem tts = TrainTicketSystem.getInstance();
		tts.login("q", "q");
		
		OrderRecord order1 = new OrderRecord();
		OrderRecord order2 = new OrderRecord();
		OrderRecord order3 = new OrderRecord();
		order1.setRating(1);
		order2.setRating(2);
		order3.setRating(5);
		order1.setUserId("userID_2");
		order2.setUserId("userID_2");
		order3.setUserId("userID_2");
		
		OrderRecordDAO orderRecordDAO = new OrderRecordDAO();
		orderRecordDAO.addOrderRecord(order1);
		orderRecordDAO.addOrderRecord(order2);
		orderRecordDAO.addOrderRecord(order3);
		
		assertEquals(2.67, tts.calculateAverageRating(), 0.01);
    }
	
	@Test
	public void testDestinationVisitCount() {
		TrainTicketSystem tts = TrainTicketSystem.getInstance();
		tts.login("q", "q");
		
		tts.createOrder("trainID_1" , 0, new ArrayList<Ticket>()); // Dest: Chicago
		tts.createOrder("trainID_2" , 0, new ArrayList<Ticket>()); // Dest: Miami
		tts.createOrder("trainID_2" , 0, new ArrayList<Ticket>());
		tts.createOrder("trainID_4" , 0, new ArrayList<Ticket>()); // Dest: Dallas
		tts.createOrder("trainID_4" , 0, new ArrayList<Ticket>());

		HashMap<String, Integer> expectedResult = new HashMap<>();
		expectedResult.put("Chicago", 1);
		expectedResult.put("Miami", 2);
		expectedResult.put("Dallas", 2);

		assertEquals(expectedResult, tts.calculateDestinationVisitCount());
	}
	
	@Test
	public void testDestinationAvgRatiing() {
		TrainTicketSystem tts = TrainTicketSystem.getInstance();
		tts.login("q", "q");
		
		OrderRecord order1 = new OrderRecord();
		OrderRecord order2 = new OrderRecord();
		OrderRecord order3 = new OrderRecord();
		OrderRecord order4 = new OrderRecord();
		OrderRecord order5 = new OrderRecord();
		order1.setRating(1);
		order2.setRating(2);
		order3.setRating(5);
		order4.setRating(4);
		order5.setRating(2);
		order1.setUserId("userID_2");
		order2.setUserId("userID_2");
		order3.setUserId("userID_2");
		order4.setUserId("userID_2");
		order5.setUserId("userID_2");
		order1.setTrainId("trainID_1");// Dest: Chicago
		order2.setTrainId("trainID_2");// Dest: Miami
		order3.setTrainId("trainID_2");
		order4.setTrainId("trainID_4");// Dest: Dallas
		order5.setTrainId("trainID_4");
		
		OrderRecordDAO orderRecordDAO = new OrderRecordDAO();
		orderRecordDAO.addOrderRecord(order1);
		orderRecordDAO.addOrderRecord(order2);
		orderRecordDAO.addOrderRecord(order3);
		orderRecordDAO.addOrderRecord(order4);
		orderRecordDAO.addOrderRecord(order5);
		
		
		HashMap<String, Double> expectedResult = new HashMap<>();
		expectedResult.put("Chicago", 1.0);
		expectedResult.put("Miami", 3.5);
		expectedResult.put("Dallas", 3.0);

		assertEquals(expectedResult, tts.calculateDestinationAvgRating());
	}

    @Test
    public void testMostVisitedDestinations_01() {
		TrainTicketSystem tts = TrainTicketSystem.getInstance();
		tts.login("q", "q");
		
		tts.createOrder("trainID_2" , 0, new ArrayList<Ticket>()); // Dest: Miami
		tts.createOrder("trainID_2" , 0, new ArrayList<Ticket>());
		tts.createOrder("trainID_2" , 0, new ArrayList<Ticket>());
		tts.createOrder("trainID_4" , 0, new ArrayList<Ticket>()); // Dest: Dallas
		tts.createOrder("trainID_4" , 0, new ArrayList<Ticket>());
		
		ArrayList<String> expectedResult = new ArrayList<>();
		expectedResult.add("Miami");
        assertEquals(expectedResult, tts.getMostVisitedDestination());
    }
    
    @Test
    public void testMostVisitedDestinations_02() {
		TrainTicketSystem tts = TrainTicketSystem.getInstance();
		tts.login("q", "q");
		
		tts.createOrder("trainID_1" , 0, new ArrayList<Ticket>()); // Dest: Chicago
		tts.createOrder("trainID_2" , 0, new ArrayList<Ticket>()); // Dest: Miami
		tts.createOrder("trainID_2" , 0, new ArrayList<Ticket>());
		tts.createOrder("trainID_4" , 0, new ArrayList<Ticket>()); // Dest: Dallas
		tts.createOrder("trainID_4" , 0, new ArrayList<Ticket>());
		
		ArrayList<String> expectedResult = new ArrayList<>();
		expectedResult.add("Dallas");
		expectedResult.add("Miami");
        
        assertEquals(expectedResult, tts.getMostVisitedDestination());
    }

    @Test
    public void testMaxRatingDestinations_01() {
		TrainTicketSystem tts = TrainTicketSystem.getInstance();
		tts.login("q", "q");

		OrderRecord order1 = new OrderRecord();
		OrderRecord order2 = new OrderRecord();
		OrderRecord order3 = new OrderRecord();
		OrderRecord order4 = new OrderRecord();
		OrderRecord order5 = new OrderRecord();
		order1.setRating(5);
		order2.setRating(3);
		order3.setRating(4);
		order4.setRating(5);
		order5.setRating(4);
		order1.setUserId("userID_2");
		order2.setUserId("userID_2");
		order3.setUserId("userID_2");
		order4.setUserId("userID_2");
		order5.setUserId("userID_2");
		order1.setTrainId("trainID_1");// Dest: Chicago
		order2.setTrainId("trainID_2");// Dest: Miami
		order3.setTrainId("trainID_2");
		order4.setTrainId("trainID_4");// Dest: Dallas
		order5.setTrainId("trainID_4");
		
		OrderRecordDAO orderRecordDAO = new OrderRecordDAO();
		orderRecordDAO.addOrderRecord(order1);
		orderRecordDAO.addOrderRecord(order2);
		orderRecordDAO.addOrderRecord(order3);
		orderRecordDAO.addOrderRecord(order4);
		orderRecordDAO.addOrderRecord(order5);
		
		ArrayList<String> expectedResult = new ArrayList<>();
		expectedResult.add("Chicago");
        
        assertEquals(expectedResult, tts.getMaxAvgRatingDestination());
    }

    @Test
    public void testMaxRatingDestinations_02() {
		TrainTicketSystem tts = TrainTicketSystem.getInstance();
		tts.login("q", "q");

		OrderRecord order1 = new OrderRecord();
		OrderRecord order2 = new OrderRecord();
		OrderRecord order3 = new OrderRecord();
		OrderRecord order4 = new OrderRecord();
		OrderRecord order5 = new OrderRecord();
		order1.setRating(4);
		order2.setRating(4);
		order3.setRating(4);
		order4.setRating(2);
		order5.setRating(4);
		order1.setUserId("userID_2");
		order2.setUserId("userID_2");
		order3.setUserId("userID_2");
		order4.setUserId("userID_2");
		order5.setUserId("userID_2");
		order1.setTrainId("trainID_1");// Dest: Chicago
		order2.setTrainId("trainID_2");// Dest: Miami
		order3.setTrainId("trainID_2");
		order4.setTrainId("trainID_4");// Dest: Dallas
		order5.setTrainId("trainID_4");
		
		OrderRecordDAO orderRecordDAO = new OrderRecordDAO();
		orderRecordDAO.addOrderRecord(order1);
		orderRecordDAO.addOrderRecord(order2);
		orderRecordDAO.addOrderRecord(order3);
		orderRecordDAO.addOrderRecord(order4);
		orderRecordDAO.addOrderRecord(order5);
		
		ArrayList<String> expectedResult = new ArrayList<>();
		expectedResult.add("Chicago");
		expectedResult.add("Miami");
        assertEquals(expectedResult, tts.getMaxAvgRatingDestination());
    }
}
