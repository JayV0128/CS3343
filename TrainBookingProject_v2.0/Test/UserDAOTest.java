package Test;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import DataModel.*;
import DB_init.Database;
import DAO.*;
import java.util.ArrayList;

class UserDAOTest {

    private UserDAO userDAO;
    private User testUser;
    private Database dbInstance;

    @BeforeEach
    public void setUp() {
        dbInstance = Database.getInstance();
        userDAO = new UserDAO();
        // Clear and initialize the user table
        dbInstance.getTable_user().clear();
        testUser = new User("normal", "userID_1", "testUser", "testPass");
        userDAO.addUser_fromUserTable(testUser);
    }

    @Test
    public void testLoginSuccess() throws Exception{
        User user = userDAO.login("testUser", "testPass");
        assertNotNull(user, "User should not be null on successful login");
        assertEquals("testUser", user.getUsername(), "Usernames should match");
    }

    @Test
    public void testLoginFailure()throws Exception {
        User user = userDAO.login("testUser", "wrongPass");
        assertNull(user, "User should be null on failed login");
    }
 
    @Test
    public void testRegisterNewUser() throws Exception{
        boolean isRegistered = userDAO.register("normal", "newUser", "newPass");
        assertTrue(isRegistered, "Registration should succeed for new username");
        User newUser = userDAO.getUserByName("newUser");
        assertNotNull(newUser, "Newly registered user should exist");
    }

    @Test
    public void testRegisterExistingUser() throws Exception{
        boolean isRegistered = userDAO.register("normal", "testUser", "testPass");
        assertFalse(isRegistered, "Registration should fail for existing username");
    }

    @Test
    public void testDeleteUser() throws Exception{
        boolean isDeleted = userDAO.deleteUser_fromUserTable("userID_1");
        assertTrue(isDeleted, "User should be successfully deleted");
        User deletedUser = userDAO.getUserByName("testUser");
        assertNull(deletedUser, "Deleted user should not exist");
    }

    @Test
    public void testUpdateUser() throws Exception{
        testUser.setPassword("newPass");
        boolean isUpdated = userDAO.updateUser_fromUserTable(testUser);
        assertTrue(isUpdated, "User should be successfully updated");
        User updatedUser = userDAO.getUserByName("testUser");
        assertEquals("newPass", updatedUser.getPassword(), "Password should be updated");
    }

    @AfterEach
    public void tearDown() throws Exception{
        // Reset the database
        dbInstance.getTable_user().clear();
        dbInstance.resetDB();
    }
}