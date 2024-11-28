package Test;

import junit.framework.TestCase;
import Main.TrainTicketSystem;
import org.junit.Test;
import DAO.UserDAO;
import DataModel.PlatinumMember;
import DataModel.User;



public class AccountControlTest extends TestCase{
  
  @Test
  public void testRegisterSucessful() {
	  TrainTicketSystem tts  = new TrainTicketSystem();
	  boolean result = tts.register("123", "123");
	  tts.displayUserList();
  }
  
  @Test
  public void testRegisterFail() {
	  TrainTicketSystem tts  = new TrainTicketSystem();
	  boolean result = tts.register("test", "test");
  }
  
  
  @Test
  public void testLoginSucessful() {
	  TrainTicketSystem tts  = new TrainTicketSystem();
	  String userName = tts.login("test", "test").getUsername();
	  assertEquals("test", userName);
	  tts.checkIn();
	  tts.checkIn();
  }
  
  @Test
  public void testLoginFail() {
	  TrainTicketSystem tts  = new TrainTicketSystem();
	  
	  assertEquals( tts.login("test", "test1"), null);
  }
  
  @Test
  public void testListUser() {
	  TrainTicketSystem tts  = new TrainTicketSystem();
	  tts.listAllUsers();
  }

  @Test
  public void testAddUserFail() {
	  TrainTicketSystem tts  = new TrainTicketSystem();
	  tts.addNewUser("test", "test", "normal");
  }
  
  @Test
  public void testAddUserFail2() {
	  TrainTicketSystem tts  = new TrainTicketSystem();
	  tts.addNewUser("test", "test", "normal1");
  }

  @Test
  public void testAddUserPass() {
	  TrainTicketSystem tts  = new TrainTicketSystem();
	  tts.addNewUser("k", "k", "normal");
  }
  
  @Test
  public void testRemoveUserPass() {
	  TrainTicketSystem tts  = new TrainTicketSystem();
	  tts.removeUser("userID_11");
  }
  
  @Test
  public void testRemoveUserFail() {
	  TrainTicketSystem tts  = new TrainTicketSystem();
	  tts.removeUser("p");
  }
  @Test
  public void testRemoveUserFail2() {
	  TrainTicketSystem tts  = new TrainTicketSystem();
	  tts.login("test", "test");
	  
	  tts.removeUser("test");
  }
  
  @Test
  public void testChangeRoleFail() {
	  TrainTicketSystem tts  = new TrainTicketSystem();
	  tts.changeUserRole("userID_11", 1);
  }
  
  @Test
  public void testChangeRolePass() {
	  TrainTicketSystem tts  = new TrainTicketSystem();
	  tts.changeUserRole("q", 1);
  }
  
  
  
}
