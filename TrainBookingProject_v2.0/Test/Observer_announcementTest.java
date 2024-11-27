package Test;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.io.*;

import org.junit.jupiter.api.Test;

import Main.*;
import DAO.*;
import DataModel.*;


class Observer_announcementTest {

	
    @Test
    // Test case 1: User login, no announcement received
    public void Observer_announcementTest_1() {
        TrainTicketSystem ticketSystem = new TrainTicketSystem();
        UserDAO userDAO = new UserDAO();
        User user = userDAO.login("q", "q"); // user: q
        boolean isReceovedMsg = user.isReceivedAnnouncement();
        String msg = user.getLatestAnnouncement();

        assertEquals(false, isReceovedMsg);
        assertEquals(null, msg);
    }
    
    @Test
    // Test case 2: User login, Subscribe announcement, receive announcement
    public void Observer_announcementTest_2() {
        TrainTicketSystem ticketSystem = new TrainTicketSystem();
        UserDAO userDAO = new UserDAO();
        User user = userDAO.login("q", "q"); // user: q
        
        // Subscribe announcement
        ticketSystem.subscribeUser(user.getId());
        
        ticketSystem.updateAnnouncement("Hello World");
        ticketSystem.notifyAllUsers();
        boolean isReceovedMsg = user.isReceivedAnnouncement();
        String msg = user.getLatestAnnouncement();

        assertEquals(true, isReceovedMsg);
        assertEquals("Hello World", msg);
    }
   
}
