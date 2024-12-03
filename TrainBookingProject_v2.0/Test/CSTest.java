package Test;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import DAO.CustomerServiceDAO;
import Main.TrainTicketSystem;


public class CSTest{
	
	//test CS
	@Test
	public void testLeaveCS(){
		TrainTicketSystem ticketSystem = TrainTicketSystem.getInstance();
		CustomerServiceDAO csDAO = new CustomerServiceDAO();
		assertEquals("CSer : Goodbye!", ticketSystem.getAnswer("exit"));
		
	}
	
	@Test
	public void testNoInput(){
		TrainTicketSystem ticketSystem = TrainTicketSystem.getInstance();
		CustomerServiceDAO csDAO = new CustomerServiceDAO();
		assertEquals("CSer : Please type your question.", ticketSystem.getAnswer(""));
		
	}
	
	@Test
	public void testNoMatchKeyWord(){
		TrainTicketSystem ticketSystem = TrainTicketSystem.getInstance();
		CustomerServiceDAO csDAO = new CustomerServiceDAO();
		assertEquals("CSer : Your question seems to be new. Please ask via email for further assistance.", ticketSystem.getAnswer("Are you free tonight?"));
		
	}
	
	@Test
	public void testAskBookTicket() {
		TrainTicketSystem ticketSystem = TrainTicketSystem.getInstance();
		CustomerServiceDAO csDAO = new CustomerServiceDAO();
		assertEquals("CSer : To book a ticket, you should press 1 in the main menu after login, then follow the instructions to do booking.", ticketSystem.getAnswer("book ticket"));
		
	}
	
	 @Test
	public void testAskEditTicket() {
			TrainTicketSystem ticketSystem = TrainTicketSystem.getInstance();
			CustomerServiceDAO csDAO = new CustomerServiceDAO();
			assertEquals("CSer : To edit a ticket, you should press 2 in the main menu after login, then select a order to edit.", ticketSystem.getAnswer("edit ticket"));
			
	}
    
    @Test
	public void testAskCheckTicket() {
		TrainTicketSystem ticketSystem = TrainTicketSystem.getInstance();
		CustomerServiceDAO csDAO = new CustomerServiceDAO();
		assertEquals("CSer : To check a ticket, you should press 2 in the main menu after login, then you can see all ticket you booked and select the view the details.", ticketSystem.getAnswer("check ticket"));
		
	}
    
    @Test
	public void testAskCancelTicket() {
		TrainTicketSystem ticketSystem = TrainTicketSystem.getInstance();
		CustomerServiceDAO csDAO = new CustomerServiceDAO();
		assertEquals("CSer : To cancel a ticket, you should press 2 in the main menu after login, then follow the instructions to cancel.", ticketSystem.getAnswer("cancel ticket"));
		
	}
	
	@Test
	public void testAskContact() {
		TrainTicketSystem ticketSystem = TrainTicketSystem.getInstance();
		CustomerServiceDAO csDAO = new CustomerServiceDAO();
		assertEquals("CSer : How to find us : email : 123@123.com, phone : 1234567890", ticketSystem.getAnswer("cs contact method ?"));
		
	}
	
	@Test
	public void testSeekHelp() {
		TrainTicketSystem ticketSystem = TrainTicketSystem.getInstance();
		CustomerServiceDAO csDAO = new CustomerServiceDAO();
		assertEquals("CSer : If you have any problem, please press 3 to contact customer service.", ticketSystem.getAnswer("help! i am lost!"));
		
	}
	
	@Test
	public void testWithUpperCase() {
		TrainTicketSystem ticketSystem = TrainTicketSystem.getInstance();
		CustomerServiceDAO csDAO = new CustomerServiceDAO();
		assertEquals("CSer : If you have any problem, please press 3 to contact customer service.", ticketSystem.getAnswer("i need HELP!"));
		
	}
	
	@Test
	public void testWithMostFrequentKeyWord() {
		TrainTicketSystem ticketSystem = TrainTicketSystem.getInstance();
		CustomerServiceDAO csDAO = new CustomerServiceDAO();
		assertEquals("CSer : To book a ticket, you should press 1 in the main menu after login, then follow the instructions to do booking.", ticketSystem.getAnswer("Hi, I want to book a ticket and check the ticket status. Can you teach me how to book tickets first?"));
	}
	
	@Test
	public void testWithNoSpace() {
		TrainTicketSystem ticketSystem = TrainTicketSystem.getInstance();
		CustomerServiceDAO csDAO = new CustomerServiceDAO();
		assertEquals("CSer : Your question seems to be new. Please ask via email for further assistance.", ticketSystem.getAnswer("Hi, I want to bookticket"));
	}
	
}
