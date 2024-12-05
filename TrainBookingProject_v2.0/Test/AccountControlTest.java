package Test;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import DataModel.*;
import Main.TrainTicketSystem;
import DB_init.Database;
import DAO.*;

import java.util.ArrayList;

class AccountControlTest {

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
        TrainTicketSystem tts = TrainTicketSystem.getInstance();
        assertEquals(testUser, tts.login("testUser", "testPass"));
    }

    @Test
    public void testLoginFailure()throws Exception {
        TrainTicketSystem tts = TrainTicketSystem.getInstance();
        assertNull(tts.login("testUser", "wrongPass"));
    }
 
    @Test
    public void testRegisterNewUser() throws Exception{
        TrainTicketSystem tts = TrainTicketSystem.getInstance();
        assertTrue(tts.register("newUser", "newPass"));
    }

    @Test
    public void testRegisterExistingUser() throws Exception{
        TrainTicketSystem tts = TrainTicketSystem.getInstance();
        assertFalse(tts.register("testUser", "testPass"));
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