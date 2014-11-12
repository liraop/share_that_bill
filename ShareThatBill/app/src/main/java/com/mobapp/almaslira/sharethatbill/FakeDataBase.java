package com.mobapp.almaslira.sharethatbill;

import java.util.ArrayList;

/**
 * Created by José Ernesto on 11/10/2014.
 */
public class FakeDataBase {

	private static class User {
		public String userEmail;
		public String userPassword;
	}

	private static class Group {
		String groupName;
	}

	private static class Bill {
		String billName;
		float billValue;
		String groupName;
		//TODO complete
	}

	private static class UserBillRelation {
		String userEmail;
		String billName;
		float value;
	}

	private static class UserGroupRelation {
		String userEmail;
		String groupName;
	}

	private static ArrayList<User> users;
	private static ArrayList<Group> groups;
	private static ArrayList<Bill> bills;
	private static ArrayList<UserBillRelation> userBillRelations;
	private static ArrayList<UserGroupRelation> userGroupRelations;

	public static void initialize() {
		/* USERS */
		users = new ArrayList<User>();
		User user = new User();

		user.userEmail = new String("user1@us.er");
		user.userPassword = new String("1234");
		users.add(user);

		user.userEmail = new String("user2@us.er");
		user.userPassword = new String("1234");
		users.add(user);

		/* GROUPS */
		groups = new ArrayList<Group>();
		Group group = new Group();

		group.groupName = new String("group1");
		groups.add(group);

		/* BILLS */
		bills = new ArrayList<Bill>();
		Bill bill = new Bill();

		bill.billName = new String("bgroup11");
		bill.groupName = new String("group1");
		bill.billValue = 50.0f;
		bills.add(bill);

		/* USERS AND BILLS */
		userBillRelations = new ArrayList<UserBillRelation>();
		UserBillRelation userBillRelation = new UserBillRelation();

		userBillRelation.billName = new String("bgroup11");
		userBillRelation.userEmail = new String("user1");
		userBillRelation.value = 50.0f;
		userBillRelations.add(userBillRelation);

		userBillRelation.billName = new String("bgroup11");
		userBillRelation.userEmail = new String("user2");
		userBillRelation.value = -50.0f;
		userBillRelations.add(userBillRelation);

		/* USERS AND GROUPS */
		userGroupRelations = new ArrayList<UserGroupRelation>();
		UserGroupRelation userGroupRelation = new UserGroupRelation();

		userGroupRelation.userEmail = new String("user1@us.er");
		userGroupRelation.groupName = new String("group1");
		userGroupRelations.add(userGroupRelation);

		userGroupRelation.userEmail = new String("user2@us.er");
		userGroupRelation.groupName = new String("group1");
		userGroupRelations.add(userGroupRelation);
	}

	/**
	 * Returns if userEmail and userPassword match an existing profile.
	 */
	public static boolean checkLogin (String email, String password) {
		try {
			Thread.sleep((int) (Math.random() * 3000.0 + 1000));
		} catch (InterruptedException ex) {
		}

		for (User u : users) {
			if (u.userEmail.compareTo(email) == 0) {
				if (u.userPassword.compareTo(password) == 0)
					return true;
				else
					return false;
			}
		}

		/*
		if (email.compareTo("user1@us.er") == 0 && password.compareTo("1234") == 0)
			return true;
		else if (email.compareTo("user2@us.er") == 0 && password.compareTo("1234") == 0)
			return true;
*/
		return false;
	}

	public static ArrayList<String> getUserGroups (String email) {
		try {
			Thread.sleep((int) (Math.random() * 3000.0 + 1000));
		} catch (InterruptedException ex) {
		}

		ArrayList<String> result = new ArrayList<String>();

		for (UserGroupRelation ugr : userGroupRelations) {
			if (ugr.userEmail.compareTo(email) == 0)
				result.add(ugr.groupName);
		}

		return result;
		/*
		if (email.compareTo("user1@us.er") == 0) {
			ArrayList<String> list = new ArrayList<String>();
			list.add("group1");
			return list;
		}
		else if (email.compareTo("user2@us.er") == 0) {
			ArrayList<String> list = new ArrayList<String>();
			list.add("group1");
			return list;
		}
		else
			return null;
			*/
	}
}
