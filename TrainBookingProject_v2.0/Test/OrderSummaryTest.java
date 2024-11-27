package Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.junit.Test;
import static org.junit.Assert.*;

import DataModel.OrderRecord;
import DataModel.Ticket;
import Main.TrainTicketSystem;

public class OrderSummaryTest {
	@Test
    public void testAverageRating() {
		TrainTicketSystem tts = TrainTicketSystem.getInstance();
		ArrayList<OrderRecord> orders = new ArrayList<>();
        orders.add(new OrderRecord());
        orders.add(new OrderRecord());
        orders.add(new OrderRecord());
		
		orders.get(0).setRating(1);
		orders.get(1).setRating(2);
		orders.get(2).setRating(5);
		
		assertEquals(2.67, tts.calculateAverageRating(orders), 0.01);
    }

    @Test
    public void testMostVisitedDestinations_01() {
		TrainTicketSystem tts = TrainTicketSystem.getInstance();
		ArrayList<OrderRecord> orders = new ArrayList<>();
        orders.add(new OrderRecord());
        orders.add(new OrderRecord());
        orders.add(new OrderRecord());
        orders.add(new OrderRecord());
        orders.add(new OrderRecord());
        
		orders.get(0).setTrainId("trainID_2"); // Dest: Miami
		orders.get(1).setTrainId("trainID_2"); 
		orders.get(2).setTrainId("trainID_2");
		orders.get(3).setTrainId("trainID_4"); // Dest: Dallas
		orders.get(4).setTrainId("trainID_4");
		
		ArrayList<String> expectedResult = new ArrayList<>();
		expectedResult.add("Miami");
        
        assertEquals(expectedResult, tts.getMostVisitedDestination(orders));
    }
    
    @Test
    public void testMostVisitedDestinations_02() {
		TrainTicketSystem tts = TrainTicketSystem.getInstance();
		ArrayList<OrderRecord> orders = new ArrayList<>();
        orders.add(new OrderRecord());
        orders.add(new OrderRecord());
        orders.add(new OrderRecord());
        orders.add(new OrderRecord());
        orders.add(new OrderRecord());
        
		orders.get(0).setTrainId("trainID_1"); // Dest: Chicago
		orders.get(1).setTrainId("trainID_2"); // Dest: Miami
		orders.get(2).setTrainId("trainID_2");
		orders.get(3).setTrainId("trainID_4"); // Dest: Dallas
		orders.get(4).setTrainId("trainID_4");
		
		ArrayList<String> expectedResult = new ArrayList<>();
		expectedResult.add("Dallas");
		expectedResult.add("Miami");
        
        assertEquals(expectedResult, tts.getMostVisitedDestination(orders));
    }

    @Test
    public void testMaxRatingDestinations_01() {
		TrainTicketSystem tts = TrainTicketSystem.getInstance();
		ArrayList<OrderRecord> orders = new ArrayList<>();
        orders.add(new OrderRecord());
        orders.add(new OrderRecord());
        orders.add(new OrderRecord());
        orders.add(new OrderRecord());
        orders.add(new OrderRecord());
        
		orders.get(0).setTrainId("trainID_1"); // Dest: Chicago
		orders.get(0).setRating(5);
		orders.get(1).setTrainId("trainID_2"); // Dest: Miami
		orders.get(1).setRating(3);
		orders.get(2).setTrainId("trainID_2");
		orders.get(2).setRating(4);
		orders.get(3).setTrainId("trainID_4"); // Dest: Dallas
		orders.get(3).setRating(5);
		orders.get(4).setTrainId("trainID_4");
		orders.get(4).setRating(4);
		
		ArrayList<String> expectedResult = new ArrayList<>();
		expectedResult.add("Chicago");
        
        assertEquals(expectedResult, tts.getMaxAvgRatingDestination(orders));
    }

    @Test
    public void testMaxRatingDestinations_02() {
		TrainTicketSystem tts = TrainTicketSystem.getInstance();
		ArrayList<OrderRecord> orders = new ArrayList<>();
        orders.add(new OrderRecord());
        orders.add(new OrderRecord());
        orders.add(new OrderRecord());
        orders.add(new OrderRecord());
        orders.add(new OrderRecord());
        
		orders.get(0).setTrainId("trainID_1"); // Dest: Chicago
		orders.get(0).setRating(4);
		orders.get(1).setTrainId("trainID_2"); // Dest: Miami
		orders.get(1).setRating(4);
		orders.get(2).setTrainId("trainID_2");
		orders.get(2).setRating(4);
		orders.get(3).setTrainId("trainID_4"); // Dest: Dallas
		orders.get(3).setRating(2);
		orders.get(4).setTrainId("trainID_4");
		orders.get(4).setRating(4);
		
		ArrayList<String> expectedResult = new ArrayList<>();
		expectedResult.add("Chicago");
		expectedResult.add("Miami");
        assertEquals(expectedResult, tts.getMaxAvgRatingDestination(orders));
    }
}
