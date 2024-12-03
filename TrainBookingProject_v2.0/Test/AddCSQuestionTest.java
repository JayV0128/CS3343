package Test;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import DAO.CustomerServiceDAO;
import Main.TrainTicketSystem;


public class AddCSQuestionTest{
	
	@Test
	public void testAddEmptyKeywordList() {
		TrainTicketSystem ticketSystem = TrainTicketSystem.getInstance();
		CustomerServiceDAO csDAO = new CustomerServiceDAO();
		assertEquals("Keywords cannot be empty.", ticketSystem.addQA("","hi, how are you"));
	}
	
	@Test
	public void testAddEmptyAnswer() {
		TrainTicketSystem ticketSystem = TrainTicketSystem.getInstance();
		CustomerServiceDAO csDAO = new CustomerServiceDAO();
		assertEquals("Answer cannot be empty.", ticketSystem.addQA("hello",""));
	}
	
	@Test
	public void testAddWithOneKeyword() {
		TrainTicketSystem ticketSystem = TrainTicketSystem.getInstance();
		CustomerServiceDAO csDAO = new CustomerServiceDAO();
		assertEquals("QA added successfully.", ticketSystem.addQA("hello","hi, how are you"));
	}
	
	@Test
	public void testAddWithManyKeyword() {
		TrainTicketSystem ticketSystem =TrainTicketSystem.getInstance();
		CustomerServiceDAO csDAO = new CustomerServiceDAO();
		assertEquals("QA added successfully.", ticketSystem.addQA("delay,miss","If you miss your train, please contact Customer Service via email or phone immediately to arrange an alternative."));
	}
	
	@Test
	public void testAddWithMissKeyword(){
		TrainTicketSystem ticketSystem = TrainTicketSystem.getInstance();
		CustomerServiceDAO csDAO = new CustomerServiceDAO();
		assertEquals("Keywords list fields cannot be empty.", ticketSystem.addQA("smoke,,cigarettes","smoking is not allowed in all train"));
	}
	
	@Test
	public void testAddExistingKeyword(){
		TrainTicketSystem ticketSystem = TrainTicketSystem.getInstance();
		CustomerServiceDAO csDAO = new CustomerServiceDAO();
		assertEquals("Keywords already exist.", ticketSystem.addQA("book","to book a train,please back to main page to book"));
	}
	
}
