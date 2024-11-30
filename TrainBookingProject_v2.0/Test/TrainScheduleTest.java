package Test;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.io.*;

import Main.*;
import DAO.*;
import DataModel.*;


class TrainScheduleTest {

    @Test
    // Test case 1: Add a new train schedule successfully
    public void TrainScheduleTest_1() {
        TrainTicketSystem ticketSystem = TrainTicketSystem.getInstance();
        ArrayList<String> result = ticketSystem.addTrain("HK", "TW", "2024-10-06", "17:00", 1000);
        assertEquals("[true, Train added successfully: 17:00]", result.toString());
    }

    @Test
    // Test case 2: there is a conflict with the existing train schedule and the new train schedule cannot be added, is full
    public void TrainScheduleTest_2() {
        TrainTicketSystem ticketSystem = TrainTicketSystem.getInstance();
        ArrayList<String> result = ticketSystem.addTrain("HK", "TW", "2024-10-06", "17:00", 1000);
        assertEquals("[false, No available time slot]", result.toString());
    }
    
    @Test
    // Test case 3: there is a conflict with the existing train schedule, reassign the most suitable time
    // 10:00 & 11:00 are occupied, so the new time slot is 12:00
    public void TrainScheduleTest_3() {
        TrainTicketSystem ticketSystem = TrainTicketSystem.getInstance();
        ArrayList<String> result = ticketSystem.addTrain("Houston", "Dallas", "2024-10-06", "10:00", 200);
        System.out.println(result);
        assertEquals("[true, Overlap time slot detected, new time slot: 12:00]", result.toString());
    }
    
    @Test
    // Test case 4: remove a train schedule successfully
    public void TrainScheduleTest_4() {
        TrainTicketSystem ticketSystem = TrainTicketSystem.getInstance();
        int numOfAvaTrain = ticketSystem.displayTrains_available();
        ArrayList<String> result = ticketSystem.removeTrain("trainId_14");
        assertEquals(14, numOfAvaTrain);
        System.out.println(result);
        assertEquals("[true, Train deleted successfully.]", result.toString());
        numOfAvaTrain = ticketSystem.displayTrains_available();
        assertEquals(13, numOfAvaTrain);
    }
    
    @Test
    // Test case 5: remove a train schedule incorrectly, trainId not found
    public void TrainScheduleTest_5() {
        TrainTicketSystem ticketSystem = TrainTicketSystem.getInstance();
        int numOfAvaTrain = ticketSystem.displayTrains_available();
        ArrayList<String> result = ticketSystem.removeTrain("trainId_99");
        assertEquals(13, numOfAvaTrain);
        System.out.println(result);
        assertEquals("[false, Train ID not found. Deletion failed.]", result.toString());
        numOfAvaTrain = ticketSystem.displayTrains_available();
        assertEquals(13, numOfAvaTrain);
    }
    
    @Test
    // Test case 6: update a train schedule successfully
    public void TrainScheduleTest_6() {
        TrainTicketSystem ticketSystem = TrainTicketSystem.getInstance();
        int numOfAvaTrain = ticketSystem.displayTrains_available();
        
        ArrayList<String> result = ticketSystem.updateTrain("trainID_3", "Houston", "Dallas", "2024-10-06", "10:00", "200");
        System.out.println(result);
        assertEquals("[true, Train details updated successfully., trainID_3, From: Houston to Dallas on 2024-10-06 at 10:00, seats available: 21, Price: $200.00]",
        		result.toString());
    }
    
    @Test
    // Test case 7:update a train schedule incorrectly, trainId not found
    public void TrainScheduleTest_7() {
    	TrainTicketSystem ticketSystem = TrainTicketSystem.getInstance();
        int numOfAvaTrain = ticketSystem.displayTrains_available();
        ArrayList<String> result = ticketSystem.updateTrain("trainID_99", "Houston", "Dallas", "2024-10-06", "10:00", "200");
        System.out.println(result);
        assertEquals("[false, Train ID not found.]", result.toString());
    }
    
    @Test
    // Test case 8:update a train schedule incorrectly, price cannot be negative
    public void TrainScheduleTest_8() {
    	TrainTicketSystem ticketSystem = TrainTicketSystem.getInstance();
        int numOfAvaTrain = ticketSystem.displayTrains_available();
        ArrayList<String> result = ticketSystem.updateTrain("trainID_3", "Houston", "Dallas", "2024-10-06", "10:00", "-200");
        System.out.println(result);
        assertEquals("[false, Price cannot be negative.]", result.toString());
    }

}
