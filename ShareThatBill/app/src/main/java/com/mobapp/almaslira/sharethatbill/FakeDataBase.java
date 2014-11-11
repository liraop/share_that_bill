package com.mobapp.almaslira.sharethatbill;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by José Ernesto on 11/10/2014.
 */
public class FakeDataBase {
	/**
	 * Returns if email and password match an existing profile.
	 */
	public static boolean checkLogin (String email, String password) {
		try {
			Thread.sleep((int) (Math.random() * 3000.0 + 1000));
		} catch (InterruptedException ex) {
		}

		if (email.compareTo("user1@us.er") == 0 && password.compareTo("1234") == 0)
			return true;
		else if (email.compareTo("user2@us.er") == 0 && password.compareTo("1234") == 0)
			return true;

		return false;
	}

	public static ArrayList<String> getUserGroups (String email) {
		try {
			Thread.sleep((int) (Math.random() * 3000.0 + 1000));
		} catch (InterruptedException ex) {
		}

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
	}
}
