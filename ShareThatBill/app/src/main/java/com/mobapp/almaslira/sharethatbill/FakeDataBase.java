package com.mobapp.almaslira.sharethatbill;



import android.location.Location;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by José Ernesto on 11/10/2014.
 */
public class FakeDataBase {
	int delayMin = 1000;
	int delayDelta = 500;

	private class User {
		String userEmail;
		String userPassword;
	}

	private class Group {
		String groupName;
	}

	private class UserBillRelation {
		String userEmail;
		String billName;
		float value;
	}

	private class UserGroupRelation {
		String userEmail;
		String groupName;
	}

	private ArrayList<User> users;
	private ArrayList<Group> groups;
	private ArrayList<Bill> bills;
	private ArrayList<UserBillRelation> userBillRelations;
	private ArrayList<UserGroupRelation> userGroupRelations;

	public void initialize() {
		/* USERS */
		users = new ArrayList<User>();
		User user;

		user = new User();
		user.userEmail = new String("user1@us.er");
		user.userPassword = new String("1234");
		users.add(user);

		user = new User();
		user.userEmail = new String("user2@us.er");
		user.userPassword = new String("1234");
		users.add(user);


		/* GROUPS */
		groups = new ArrayList<Group>();
		Group group;

		group = new Group();
		group.groupName = new String("group1");
		groups.add(group);


		/* BILLS */
		bills = new ArrayList<Bill>();
		Bill bill;

		bill = new Bill();
		bill.billName = new String("bgroup11");
		bill.groupName = new String("group1");
		bill.billValue = 50.0f;
        bill.billDate = new Date();
        bill.billLocation = new Location("android");
        bill.billLocation.setAltitude(-22.017280d);
        bill.billLocation.setLongitude(-47.886883d);
        bills.add(bill);


		/* USERS AND BILLS */
		userBillRelations = new ArrayList<UserBillRelation>();
		UserBillRelation userBillRelation;

		userBillRelation = new UserBillRelation();
		userBillRelation.billName = new String("bgroup11");
		userBillRelation.userEmail = new String("user1");
		userBillRelation.value = 50.0f;
		userBillRelations.add(userBillRelation);

		userBillRelation = new UserBillRelation();
		userBillRelation.billName = new String("bgroup11");
		userBillRelation.userEmail = new String("user2");
		userBillRelation.value = -50.0f;
		userBillRelations.add(userBillRelation);


		/* USERS AND GROUPS */
		this.userGroupRelations = new ArrayList<UserGroupRelation>();
		UserGroupRelation userGroupRelation;

		userGroupRelation = new UserGroupRelation();
		userGroupRelation.userEmail = new String("user1@us.er");
		userGroupRelation.groupName = new String("group1");
		this.userGroupRelations.add(userGroupRelation);

		userGroupRelation = new UserGroupRelation();
		userGroupRelation.userEmail = new String("user2@us.er");
		userGroupRelation.groupName = new String("group1");
		this.userGroupRelations.add(userGroupRelation);
	}

	/**
	 * Returns if userEmail and userPassword match an existing profile.
	 */
	public boolean checkLogin (String email, String password) {
		try {
			Thread.sleep((int) (Math.random() * delayDelta + delayMin));
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

		return false;
	}

	public ArrayList<String> getUserGroups (String email) {
		try {
			Thread.sleep((int) (Math.random() * delayDelta + delayMin));
		} catch (InterruptedException ex) {
		}
		ArrayList<String> result = new ArrayList<String>();

		for (UserGroupRelation ugr : this.userGroupRelations) {
			if (ugr.userEmail.compareTo(email) == 0)
				result.add(ugr.groupName);
		}

		return result;
	}

    public ArrayList<String> getGroupUsers (String groupName) {
        try {
            Thread.sleep((int) (Math.random() * delayDelta + delayMin));
        } catch (InterruptedException ex) {
        }
        ArrayList<String> result = new ArrayList<String>();

        for (UserGroupRelation ugr : this.userGroupRelations) {
            if (ugr.groupName.compareTo(groupName) == 0)
                result.add(ugr.userEmail);
        }

        return result;
    }

    public ArrayList<String> getGroupBills (String groupName) {
        try {
            Thread.sleep((int) (Math.random() * delayDelta + delayMin));
        } catch (InterruptedException ex) {
        }
        ArrayList<String> result = new ArrayList<String>();

        for (Bill bill : this.bills) {
            if (bill.groupName.compareTo(groupName) == 0)
                result.add(bill.billName);
        }

        return result;
    }

    public ArrayList<TwoStringsClass> getWhoPaidBill (String billName) {
        try {
            Thread.sleep((int) (Math.random() * delayDelta + delayMin));
        } catch (InterruptedException ex) {
        }

        ArrayList<TwoStringsClass> result = new ArrayList<TwoStringsClass>();

        for (UserBillRelation ubr : userBillRelations) {
            if (ubr.billName.compareTo(billName) == 0)
                if (ubr.value > 0)
                    result.add(new TwoStringsClass(ubr.userEmail, String.format("%2.2f", ubr.value)));
        }

        return result;
    }

    public ArrayList<TwoStringsClass> getWhoOwnsBill (String billName) {
        try {
            Thread.sleep((int) (Math.random() * delayDelta + delayMin));
        } catch (InterruptedException ex) {
        }

        ArrayList<TwoStringsClass> result = new ArrayList<TwoStringsClass>();

        for (UserBillRelation ubr : userBillRelations) {
            if (ubr.billName.compareTo(billName) == 0)
                if (ubr.value < 0)
                    result.add(new TwoStringsClass(ubr.userEmail, String.format("%2.2f", -ubr.value)));
        }

        return result;
    }

    public Bill getBill (String billName) {
        try {
            Thread.sleep((int) (Math.random() * delayDelta + delayMin));
        } catch (InterruptedException ex) {
        }

        Bill result;

        for (Bill b : bills) {
            if (b.billName.compareTo(billName) == 0)
                return b;
        }
        return null;
    }

    public void addUserToGroup (String user, String group) {
        try {
            Thread.sleep((int) (Math.random() * delayDelta + delayMin));
        } catch (InterruptedException ex) {
        }

        UserGroupRelation userGroupRelation = new UserGroupRelation();
        userGroupRelation.userEmail = new String(user);
        userGroupRelation.groupName = new String(group);
        this.userGroupRelations.add(userGroupRelation);
    }

    public boolean createGroup (String groupName) {
        try {
            Thread.sleep((int) (Math.random() * delayDelta + delayMin));
        } catch (InterruptedException ex) {
        }

        for(Group g : groups)
            if (g.groupName.compareTo(groupName) == 0)
                return false;

        Group group = new Group();
        group.groupName = new String(groupName);
        groups.add(group);

        return true;
    }

	public boolean createAccount (String email, String password) {
		try {
			Thread.sleep((int) (Math.random() * delayDelta + delayMin));
		} catch (InterruptedException ex) {
		}

		for(User u : users)
			if (u.userEmail.compareTo(email) == 0)
				return false;

		User user = new User();
		user.userEmail = new String(email);
		user.userPassword = new String(password);
		users.add(user);

		return true;
	}
}
