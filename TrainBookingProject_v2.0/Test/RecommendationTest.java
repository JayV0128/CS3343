package Test;

import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;
import org.junit.jupiter.api.AfterEach;

import DAO.OrderRecordDAO;
import DB_init.Database;
import DataModel.OrderRecord;
import DataModel.Ticket;

import static org.junit.Assert.*;

import Main.TrainTicketSystem;

public class RecommendationTest {
	
	private OrderRecordDAO orderRecordDAO = new OrderRecordDAO();
	private Database dbInstance = Database.getInstance();
	
	@Test
	public void testRecommendTrains_1() {
		TrainTicketSystem tts = TrainTicketSystem.getInstance();
		ArrayList<String> result = tts.recommendTrains("userID_2", "None");
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void testRecommendTrains_2() {
		TrainTicketSystem tts = TrainTicketSystem.getInstance();
		ArrayList<Ticket> tickets = new ArrayList<Ticket>();
		tickets.add(new Ticket("q", 18));
		
		OrderRecord orderRecord = new OrderRecord(
			String.format("orderID_%s_%s", "userID_2", 0), 
			"userID_2", 
			"trainID_1", 
			new Date(), 
			0, 
			tickets
		);
		orderRecord.setRating(-1);

		orderRecordDAO.addOrderRecord(orderRecord);
		
		ArrayList<String> result = tts.recommendTrains("userID_2", "None");
		assertEquals("[trainID_1]", result.toString());
	}
	
	@Test
	public void testRecommendTrains_3() {
		TrainTicketSystem tts = TrainTicketSystem.getInstance();

		ArrayList<Ticket> tickets = new ArrayList<Ticket>();
		tickets.add(new Ticket("q", 18));
		
		OrderRecord orderRecord1 = new OrderRecord(
			String.format("orderID_%s_%s", "userID_2", 0), 
			"userID_2", 
			"trainID_1", 
			new Date(), 
			0, 
			tickets
		);
		orderRecord1.setRating(1);
		
		OrderRecord orderRecord2 = new OrderRecord(
			String.format("orderID_%s_%s", "userID_2", 1), 
			"userID_2", 
			"trainID_2", 
			new Date(), 
			0, 
			tickets
		);
		orderRecord2.setRating(2);
		
		OrderRecord orderRecord3 = new OrderRecord(
			String.format("orderID_%s_%s", "userID_2", 2), 
			"userID_2", 
			"trainID_3", 
			new Date(), 
			0, 
			tickets
		);
		orderRecord3.setRating(3);

		orderRecordDAO.addOrderRecord(orderRecord1);
		orderRecordDAO.addOrderRecord(orderRecord2);
		orderRecordDAO.addOrderRecord(orderRecord3);
		
		ArrayList<String> result = tts.recommendTrains("userID_2", "None");
		assertEquals("[trainID_3, trainID_2, trainID_1]", result.toString());
	}	
	
	@Test
	public void testRecommendTrains_4() {
		TrainTicketSystem tts = TrainTicketSystem.getInstance();

		ArrayList<Ticket> tickets = new ArrayList<Ticket>();
		tickets.add(new Ticket("q", 18));
		
		OrderRecord orderRecord1 = new OrderRecord(
			String.format("orderID_%s_%s", "userID_2", 0), 
			"userID_2", 
			"trainID_1", 
			new Date(), 
			0, 
			tickets
		);
		orderRecord1.setRating(1);
		
		OrderRecord orderRecord2 = new OrderRecord(
			String.format("orderID_%s_%s", "userID_2", 1), 
			"userID_2", 
			"trainID_2", 
			new Date(), 
			0, 
			tickets
		);
		orderRecord2.setRating(2);
		
		OrderRecord orderRecord3 = new OrderRecord(
			String.format("orderID_%s_%s", "userID_2", 2), 
			"userID_2", 
			"trainID_3", 
			new Date(), 
			0, 
			tickets
		);
		orderRecord3.setRating(3);
		
		OrderRecord orderRecord4 = new OrderRecord(
			String.format("orderID_%s_%s", "userID_2", 3), 
			"userID_2", 
			"trainID_1", 
			new Date(), 
			0, 
			tickets
		);
		orderRecord4.setRating(5);

		orderRecordDAO.addOrderRecord(orderRecord1);
		orderRecordDAO.addOrderRecord(orderRecord2);
		orderRecordDAO.addOrderRecord(orderRecord3);
		orderRecordDAO.addOrderRecord(orderRecord4);
		
		ArrayList<String> result = tts.recommendTrains("userID_2", "None");
		assertEquals("[trainID_1, trainID_3, trainID_2]", result.toString());
	}	
	
	@Test
	public void testRecommendTrains_5() {
		TrainTicketSystem tts = TrainTicketSystem.getInstance();
		ArrayList<Ticket> tickets = new ArrayList<Ticket>();
		tickets.add(new Ticket("q", 18));
		
		OrderRecord orderRecord = new OrderRecord(
			String.format("orderID_%s_%s", "userID_2", 0), 
			"userID_2", 
			"trainID_1", 
			new Date(), 
			0, 
			tickets
		);
		orderRecord.setRating(5);

		orderRecordDAO.addOrderRecord(orderRecord);
		
		ArrayList<String> result = tts.recommendTrains("userID_2", "LA");
		assertEquals("[trainID_1]", result.toString());
	}
	
	@AfterEach
    public void tearDown() throws Exception {
        dbInstance.getTable_orderRecord().clear();
        dbInstance.resetDB();
    }
  
}
