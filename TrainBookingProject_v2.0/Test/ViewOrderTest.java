package Test;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import DAO.OrderRecordDAO;
import DAO.UserDAO;
import DB_init.Database;
import DataModel.OrderRecord;
import DataModel.Ticket;
import DataModel.User;
import Main.TrainTicketSystem;

public class ViewOrderTest {
	@BeforeEach
	public void setup() {
		Database.getInstance().resetDB();

	}
	
	@Test
	public void testDisplayOrders() {
		TrainTicketSystem tts = TrainTicketSystem.getInstance();
		tts.login("q", "q");
		
		ArrayList<Ticket> ticketList = new ArrayList<Ticket>();
		ticketList.add(new Ticket());
		ticketList.add(new Ticket());
        tts.createOrder("trainID_1", 0, ticketList);
        
        assertEquals(1, tts.displayOrders());
	}
	
	@Test
	public void testSelectOrder_01() {
		TrainTicketSystem tts = TrainTicketSystem.getInstance();
		OrderRecord order1 = new OrderRecord();
		OrderRecord order2 = new OrderRecord();
		OrderRecord order3 = new OrderRecord();
        
		OrderRecordDAO orderRecordDAO = new OrderRecordDAO();
		orderRecordDAO.addOrderRecord(order1);
		orderRecordDAO.addOrderRecord(order2);
		orderRecordDAO.addOrderRecord(order3);
		
		assertEquals(order2, tts.selectOrder(2));
	}
	
	@Test
	public void testSelectOrder_02() {
		TrainTicketSystem tts = TrainTicketSystem.getInstance();
		OrderRecord order1 = new OrderRecord();
		OrderRecord order2 = new OrderRecord();
		OrderRecord order3 = new OrderRecord();
        
		OrderRecordDAO orderRecordDAO = new OrderRecordDAO();
		orderRecordDAO.addOrderRecord(order1);
		orderRecordDAO.addOrderRecord(order2);
		orderRecordDAO.addOrderRecord(order3);
		
		assertNull(tts.selectOrder(0));
	}
	
	@Test
	public void testSelectOrder_03() {
		TrainTicketSystem tts = TrainTicketSystem.getInstance();
		OrderRecord order1 = new OrderRecord();
		OrderRecord order2 = new OrderRecord();
		OrderRecord order3 = new OrderRecord();
        
		OrderRecordDAO orderRecordDAO = new OrderRecordDAO();
		orderRecordDAO.addOrderRecord(order1);
		orderRecordDAO.addOrderRecord(order2);
		orderRecordDAO.addOrderRecord(order3);
		
		assertNull(tts.selectOrder(15));
	}
	
	@Test
	public void testGetTicketList() {
		TrainTicketSystem tts = TrainTicketSystem.getInstance();
        OrderRecord order = new OrderRecord();
        ArrayList<Ticket> ticketList = new ArrayList<Ticket>();
        ticketList.add(new Ticket());
        ticketList.add(new Ticket());
        ticketList.add(new Ticket());
        order.setTicketList(ticketList);
        
        assertEquals(ticketList, tts.getTicketList(order));
	}
	
	@Test
	public void testCancelOrder() {
		TrainTicketSystem tts = TrainTicketSystem.getInstance();
		tts.login("q", "q");
		
		ArrayList<Ticket> ticketList = new ArrayList<Ticket>();
		ticketList.add(new Ticket());
		ticketList.add(new Ticket());
        OrderRecord order = tts.createOrder("trainID_1", 0, ticketList);

        assertEquals("Order has been successfully canceled.", tts.cancelOrder(order));
	}
	
	@Test
	public void testGetFinishedOrders_01() {
		TrainTicketSystem tts = TrainTicketSystem.getInstance();
		tts.login("q", "q");
		
		OrderRecord order1 = tts.createOrder("trainID_1", 10, new ArrayList<Ticket>());
		OrderRecord order2 = tts.createOrder("trainID_2", 20, new ArrayList<Ticket>());
		
		ArrayList<OrderRecord> finishedOrders = new ArrayList<>();
		finishedOrders.add(order1);
		finishedOrders.add(order2);

		assertEquals(finishedOrders, tts.getFinishedOrders());
	}
	
	@Test
	public void testRateOrder_01() {
		TrainTicketSystem tts = TrainTicketSystem.getInstance();
		tts.login("q", "q");
		
		ArrayList<Ticket> ticketList = new ArrayList<Ticket>();
		ticketList.add(new Ticket());
		ticketList.add(new Ticket());
		OrderRecord order = tts.createOrder("trainID_1", 0, ticketList);

		assertTrue(tts.rateOrder(order, 3));
	}

	@Test
	public void testRateOrder_02() {
		TrainTicketSystem tts = TrainTicketSystem.getInstance();
		tts.login("q", "q");
		
		ArrayList<Ticket> ticketList = new ArrayList<Ticket>();
		ticketList.add(new Ticket());
		ticketList.add(new Ticket());
		OrderRecord order = tts.createOrder("trainID_1", 0, ticketList);

		assertFalse(tts.rateOrder(order, 0));
	}
	
	@Test
	public void testRateOrder_03() {
		TrainTicketSystem tts = TrainTicketSystem.getInstance();
		tts.login("q", "q");
		
		ArrayList<Ticket> ticketList = new ArrayList<Ticket>();
		ticketList.add(new Ticket());
		ticketList.add(new Ticket());
		OrderRecord order = tts.createOrder("trainID_1", 0, ticketList);

		assertFalse(tts.rateOrder(order, 6));
	}
	
	@Test
	public void testGetTrain() {
		TrainTicketSystem tts = TrainTicketSystem.getInstance();
		assertNotNull(tts.getTrain("trainID_1"));
	}
}
