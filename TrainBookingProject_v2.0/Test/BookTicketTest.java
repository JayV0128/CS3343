package Test;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import DB_init.Database;

import DataModel.AmountCoupon;
import DataModel.NormalMember;
import DataModel.OrderRecord;
import DataModel.Ticket;
import DataModel.User;
import Main.TrainTicketSystem;

public class BookTicketTest {
	@BeforeEach
	public void setUp() {
		Database.getInstance().resetDB();
	}
	
	@Test
	public void testHasOrders_01() {
		TrainTicketSystem tts = TrainTicketSystem.getInstance();
		tts.login("q", "q");
		assertFalse(tts.hasOrders());
	}
	
	@Test
	public void testHasOrders_02() {
		TrainTicketSystem tts = TrainTicketSystem.getInstance();
		tts.login("q", "q");
		tts.createOrder("trainID_1", 100, null);
		assertTrue(tts.hasOrders());
	}

	@Test
	public void testSelectedTrain_01() {
		TrainTicketSystem tts = TrainTicketSystem.getInstance();
		assertNotNull(tts.selectTrain(3));
	}
	
	@Test
	public void testSelectedTrain_02() {
		TrainTicketSystem tts = TrainTicketSystem.getInstance();
		assertNull(tts.selectTrain(0));
	}
	
	@Test
	public void testSelectedTrain_03() {
		TrainTicketSystem tts = TrainTicketSystem.getInstance();
		assertNull(tts.selectTrain(14)); // Max 13 trains in Mock DB --> last train index = 12
	}
	
	@Test
	public void testTotalPriceWithDiscount_01() {
		TrainTicketSystem tts = TrainTicketSystem.getInstance();
		User user = new User("normal", "test", "user1", "password");
		user.setMember(new NormalMember());
		user.addCoupon(new AmountCoupon(90, "testCoupon", "couponCode1", null));
		assertEquals(0, tts.getTotalPriceWithDiscount(user, 100), 0.001); // 100 - 100 * 0.1 - 90 = 0
	}
	
	@Test
	public void testTotalPriceWithDiscount_02() {
		TrainTicketSystem tts = TrainTicketSystem.getInstance();
		User user = new User("normal", "test", "user1", "password");
		user.setMember(new NormalMember());
		assertEquals(90, tts.getTotalPriceWithDiscount(user, 100), 0.001); // 100 - 100 * 0.1 = 90
	}
}
