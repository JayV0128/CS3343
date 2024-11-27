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


class SeatArrangementTest {

    @Test
    public void seatArrange_1() { // Test case 1: Test seat arrangement with one passenger
        TrainTicketSystem ticketSystem = new TrainTicketSystem();
        ArrayList<String> result = ticketSystem.arrangeSeat("trainID_6");
        assertEquals("[D6]", result.toString());
    }
    
    @Test
    public void seatArrange_2() { // Test case 1: Test seat arrangement with one passenger when seat is full
        TrainTicketSystem ticketSystem = new TrainTicketSystem();
        ArrayList<String> result = ticketSystem.arrangeSeat("trainID_6");
        assertEquals("[]", result.toString());
    }
    
    @Test
    public void seatArrange_3() { // Test case 2: Test seat arrangement with passenger > 1 && < 5
        TrainTicketSystem ticketSystem = new TrainTicketSystem();
        ArrayList<String> result = ticketSystem.arrangeSeat("trainID_2", 2);
        assertEquals("[A1, A2]", result.toString());
    }
    
    @Test
    public void seatArrange_4() { // Test case 3: Test seat arrangement with passenger > 1 && < 5 and seat is full
        TrainTicketSystem ticketSystem = new TrainTicketSystem();
        ArrayList<String> result = ticketSystem.arrangeSeat("trainID_3", 4);
        assertEquals("[B1, B2, B3, B4]", result.toString());
    }
    
    @Test
    public void seatArrange_5() { 
    	// Test case 4: Test seat arrangement with passenger > maximum seat for a single line > 6
    	// it will automatically assign the seat 
        TrainTicketSystem ticketSystem = new TrainTicketSystem();
        ArrayList<String> result = ticketSystem.arrangeSeat("trainID_3", 7);
        assertEquals("[A4, A5, A6, B5, B6, C1, C2]", result.toString());
    }

    
    @Test
    public void seatArrange_6() { // Test case 5: Test seat arrangement with passenger > available seat
        TrainTicketSystem ticketSystem = new TrainTicketSystem();
        ArrayList<String> result = ticketSystem.arrangeSeat("trainID_3", 20);
        assertEquals("[]", result.toString());
    }
}
