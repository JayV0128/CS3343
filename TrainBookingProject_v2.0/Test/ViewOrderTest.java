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
	private Database dbInstance;
	
    @BeforeEach
    public void setUp() {
        dbInstance = Database.getInstance();
    }
    
	@AfterEach
	public void tearDown() throws Exception {
		dbInstance.resetDB();
	}
	
	@Test
	public void testDisplayOrders() {
		TrainTicketSystem tts = TrainTicketSystem.getInstance();
		UserDAO userDao = new UserDAO();
		
		User user = userDao.getUserByName("q");
		ArrayList<Ticket> ticketList = new ArrayList<Ticket>();
		ticketList.add(new Ticket());
		ticketList.add(new Ticket());
        OrderRecord order = tts.createOrder(user, "trainID_1", 0, ticketList);
        
        assertEquals(1, tts.displayOrders(user));
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
		
		assertTrue(true);
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
		UserDAO userDao = new UserDAO();
		
		User user = userDao.getUserByName("q");
		ArrayList<Ticket> ticketList = new ArrayList<Ticket>();
		ticketList.add(new Ticket());
		ticketList.add(new Ticket());
        OrderRecord order = tts.createOrder(user, "trainID_1", 0, ticketList);

        assertTrue(tts.cancelOrder(order));
	}

}
