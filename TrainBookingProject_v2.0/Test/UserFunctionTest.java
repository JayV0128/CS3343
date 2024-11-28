package Test;

import org.junit.Test;
import static org.junit.Assert.*;

import java.time.LocalDate;

import DAO.*;
import DataModel.*;

public class UserFunctionTest {

  @Test
  public void getNormalMemberDiscount() {
      UserDAO userDAO = new UserDAO();
      User user = userDAO.login("admin", "admin");

      

  }

  @Test
  public void getPlatinumMemberDiscount() {
      UserDAO userDAO = new UserDAO();
      User user = userDAO.login("q", "q");     
     

  }
  
  @Test
  public void changeUserInfo() {
      UserDAO userDAO = new UserDAO();
      User user = userDAO.login("b", "b");     

  }
  
  @Test
  public void manageUser() {
	  UserDAO userDAO = new UserDAO();
	  User currentUser;
	  currentUser = userDAO.getUserByName("1");
	  currentUser = userDAO.getUserByName("b");
	  
	  

	  assertNull(userDAO.getUser_fromUserTable("userID_111"));
	  User b = userDAO.getUser_fromUserTable("userID_3");

	  
  
  }
  
  
}