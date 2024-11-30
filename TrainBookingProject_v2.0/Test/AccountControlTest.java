package Test;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import Main.TrainTicketSystem;
import DAO.UserDAO;
import DataModel.PlatinumMember;
import DataModel.User;



public class AccountControlTest {
  
  @Test
  public void testRegisterSucessful() {
	  TrainTicketSystem tts  = TrainTicketSystem.getInstance();
	  boolean result = tts.register("123", "123");
	  tts.displayUserList();
  }
  
  @Test
  public void testRegisterFail() {
	  TrainTicketSystem tts  = TrainTicketSystem.getInstance();
	  boolean result = tts.register("test", "test");
  }
  
  
  @Test
  public void testLoginSucessful() {
	  TrainTicketSystem tts  = TrainTicketSystem.getInstance();
	  String userName = tts.login("test", "test").getUsername();
	  assertEquals("test", userName);
	  tts.checkIn();
	  tts.checkIn();
  }
  
  @Test
  public void testLoginFail() {
	  TrainTicketSystem tts  = TrainTicketSystem.getInstance();
	  
	  assertEquals( tts.login("test", "test1"), null);
  }
  
  @Test
  public void testListUser() {
	  TrainTicketSystem tts  = TrainTicketSystem.getInstance();
	  tts.listAllUsers();
  }

  @Test
  public void testAddUserFail() {
	  TrainTicketSystem tts  = TrainTicketSystem.getInstance();
	  tts.addNewUser("test", "test", "normal");
  }
  
  @Test
  public void testAddUserFail2() {
	  TrainTicketSystem tts  = TrainTicketSystem.getInstance();
	  tts.addNewUser("test", "test", "normal1");
  }

  @Test
  public void testAddUserPass() {
	  TrainTicketSystem tts  = TrainTicketSystem.getInstance();
	  tts.addNewUser("k", "k", "normal");
  }
  
  @Test
  public void testRemoveUserPass() {
	  TrainTicketSystem tts  = TrainTicketSystem.getInstance();
	  tts.removeUser("userID_11");
  }
  
  @Test
  public void testRemoveUserFail() {
	  TrainTicketSystem tts  = TrainTicketSystem.getInstance();
	  tts.removeUser("p");
  }
  @Test
  public void testRemoveUserFail2() {
	  TrainTicketSystem tts  = TrainTicketSystem.getInstance();
	  tts.login("test", "test");
	  
	  tts.removeUser("test");
  }
  
  @Test
  public void testChangeRoleFail() {
	  TrainTicketSystem tts  = TrainTicketSystem.getInstance();
	  tts.changeUserRole("userID_11", 1);
  }
  
  @Test
  public void testChangeRolePass() {
	  TrainTicketSystem tts  = TrainTicketSystem.getInstance();
	  tts.changeUserRole("q", 1);
  }
  
  
  
}
