package DAO;

import java.util.*;

import DB_init.Database;
import DataModel.*;

public class UserDAO {
	// private User nowUser;

	public UserDAO() {
	}

	public User login(String userName, String pwd) {
		User currentUser = null;
		for (User user : Database.getInstance().getTable_user()) {
			if (user.getUsername().equals(userName) && user.getPassword().equals(pwd)) {
				// this.nowUser = user;
				currentUser = user;
			}
		}

		return currentUser;
	}

	public User getUserByName(String userName) {
		for (User user : getUserList()) {
			if (user.getUsername().equals(userName)) {
				return user;
			}
		}
		return null;
	}

	// Not needed here
	// public double getDiscount() {
	// return nowUser.getMember().getDiscount();
	// }

	// public double getDiscount(String Id) {
	// for (User user : Database.getInstance().getTable_user()) {
	// if (user.getUsername().equals(Id)) {
	// return user.getMember().getDiscount();
	// }
	// }
	// return 0;
	// }

	// Changed from return String to return boolean
	public boolean register(String role, String userName, String pwd) {
		if (usernameExists(userName)) {
			return false;
		}
		addUser_fromUserTable(new User("normal", "userID_" + (Database.getInstance().getTable_user().size() + 1), userName, pwd));
		return true;
	}

	public ArrayList<User> getUserList() {
		return Database.getInstance().getTable_user();
	}

	public void printUserList() {
		for (int i = 0; i < Database.getInstance().getTable_user().size(); i++) {
			System.out.println("index: " + i + " " + Database.getInstance().getTable_user().get(i).toString());
		}
	}

	public boolean addUser_fromUserTable(User user) {
		Database.getInstance().getTable_user().add(user);
		return true;
	}

	public User getUser_fromUserTable(String Id) {
		for (User user : Database.getInstance().getTable_user()) {
			if (user.getId().equals(Id)) {
				return user;
			}
		}
		return null;
	}

	public boolean updateUser_fromUserTable(User user) {
		boolean result = false;
		User foundUser = getUser_fromUserTable(user.getId());

		if (foundUser != null) {
			foundUser.setUsername(user.getUsername());
			foundUser.setPassword(user.getPassword());
			foundUser.setRole(user.getRole());
			result = true;
		}
		return result;
	}

	public boolean deleteUser_fromUserTable(String userId) {
		boolean result = false;
		User foundUser = getUser_fromUserTable(userId);

		if (foundUser != null) {
			Database.getInstance().getTable_user().remove(foundUser);
			result = true;
		}
		return result;
	}

	public boolean usernameExists(String username) {
		for (User user : Database.getInstance().getTable_user()) {
			if (user.getUsername().equalsIgnoreCase(username)) {
				return true;
			}
		}
		return false;
	}

	public User getUserByUsername(String username) {
		for (User user : Database.getInstance().getTable_user()) {
			if (user.getUsername().equals(username)) {
				return user;
			}
		}

		return null;

	}

	public double useCoupon(double totalAmount, User currentUser) {

		ArrayList<Coupon> coupons = currentUser.getCouponList();
		double maxDiscount = 0.0;
		Coupon coupon1= null;
		for (Coupon coupon : coupons) {
			if (coupon.getDiscount(totalAmount) > maxDiscount) {
				maxDiscount = coupon.getDiscount(totalAmount);
				coupon1 = coupon;
			}
		}
		currentUser.removeCoupon(coupon1);
		return maxDiscount;
	}



}
