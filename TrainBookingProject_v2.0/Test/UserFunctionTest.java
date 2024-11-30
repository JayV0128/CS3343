package Test;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import DAO.*;
import DataModel.*;

public class UserFunctionTest {

  @Test
  public void getNormalMemberDiscount() {
      UserDAO userDAO = new UserDAO();
      User user = userDAO.login("admin", "admin");
      assertEquals("Normal Member", user.getMemberType());
      assertTrue( user.getDiscount()== 0.1);
      

  }

  @Test
  public void getPlatinumMemberDiscount() {
      UserDAO userDAO = new UserDAO();
      User user = userDAO.login("q", "q");     
      user.setMember(new PlatinumMember());
      assertTrue(0.2 == user.getDiscount());
      assertEquals("Platinum Member", user.getMember().getMemberType());

  }
  
  @Test
  public void changeUserInfo() {
      UserDAO userDAO = new UserDAO();
      User user = userDAO.login("b", "b");     
      user.setUsername("test");
      user.setPassword("test");
      user.setId("test");
      user.setRole("test");
      user.setPoints(100);
      user.setLastSignInDate(LocalDate.now());
      
      
      assertEquals("test", user.getUsername());
      assertEquals("test", user.getPassword());
      assertEquals("test", user.getId());
      assertEquals("test", user.getRole());
      assertEquals(100, user.getPoints());
      assertEquals(LocalDate.now(), user.getLastSignInDate());
  }
  
  @Test
  public void manageUser() {
	  UserDAO userDAO = new UserDAO();
	  User currentUser;
	  currentUser = userDAO.getUserByName("1");
	  currentUser = userDAO.getUserByName("b");
	  
	  

	  assertNull(userDAO.getUser_fromUserTable("userID_111"));
	  User b = userDAO.getUser_fromUserTable("userID_3");
	  userDAO.updateUser_fromUserTable(b);
	  assertNull(userDAO.getUserByUsername("d"));
	  userDAO.getUserByUsername("c");
	  userDAO.deleteUser_fromUserTable("userID_4");
	  userDAO.printUserList();
	  
  
  }
  
  
}