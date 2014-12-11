package com.mobapp.almaslira.sharethatbill;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DBhandler {
    private static final String TAG = "DBHANDLER debug";
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String HOST = "jdbc:mysql://sql4.freesqldatabase.com/sql457251";
    private static final String DB_USER = "sql457251";
    private static final String DB_PW = "wX2*aK7%";
    private Connection connect;
    private Statement statement;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    public DBhandler() {

        try {
            Class.forName(JDBC_DRIVER).newInstance();

        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
    }

    /**
     * Method for check the user credentials on the database.
     * If there are no match, returns false.
     * If matches, return true.
     *
     * @param userEmail
     * @param password
     * @return login valid (true) or not (false)
     * @throws SQLException
     */
    public boolean checkLogin(String userEmail, String password) {
        boolean isValid = false;


        try {
            connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

            this.statement = connect.createStatement();
            String query = "SELECT * FROM users";
            this.resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int i = 1;
                while (i <= 2) {
                    if (resultSet.getString(i++).equals(userEmail) && resultSet.getString(i).equals(password)) {
                        isValid = true;
                        break;
                    }
                }
            }

            connect.close();

        } catch (SQLException e) {
            //do something with exception
        }


        return isValid;
    }

    /**
     * Method to get the groups of an user.
     *
     * @param userEmail
     * @return ArrayList<String> users
     */
    public ArrayList<String> getUserGroups(String userEmail) {

        ArrayList<String> result = new ArrayList<String>();

        try {

            connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

            this.statement = connect.createStatement();
            String query = "SELECT gid FROM usersAndGroups WHERE uid = '" + userEmail + "'";
            this.resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int i = 1;
                result.add(resultSet.getString(i++));
            }

            connect.close();

        } catch (SQLException e) {
            //do something with sql exception
        }

        return result;
    }

    /**
     * Method to get the group's members
     *
     * @param groupName
     * @return ArrayList<String> members
     */
    public ArrayList<String> getGroupMembers(String groupName) {

        ArrayList<String> result = new ArrayList<String>();

        try {

            connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

            this.statement = connect.createStatement();
            String query = "SELECT uid FROM usersAndGroups WHERE gid = '" + groupName + "'";
            this.resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int i = 1;
                result.add(resultSet.getString(i++));
            }

            connect.close();

        } catch (SQLException e) {
            //do something with sql exception
        }

        return result;

    }

   /** Method to create a group. It uses groupExists method
    * then creates the group or not. Adds the group on the groups table
    *
    * @param
    * @param groupName
    * @return group created (true) or not (false)
    */
    public boolean createGroup(String groupName) {

        if (!groupExists(groupName)) {

            try {
                connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

                this.statement = connect.createStatement();
                String query = "INSERT INTO groups VALUES ('" + groupName + "')";
                statement.executeUpdate(query);


                connect.close();

                return true;

            } catch (SQLException e) {
                //do something with exception
            }
        }

        return false;
    }

    /**
     * Method to add an user to a group.
     * User and group are added on usersAndGroups table.
     *
     * @param
     * @param groupName
     * @return
     */
    public boolean addUserToGroup(String addedUserName, String groupName, String sessionUserName) {
        if (!isUserMember(addedUserName, groupName)) {
            try {
                connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

                this.statement = connect.createStatement();
                String query = "INSERT INTO `usersAndGroups`(`uid`, `gid`) VALUES ('" + addedUserName + "','" + groupName + "')";
                statement.executeUpdate(query);

                connect.close();

                this.postNotification(new Notification(sessionUserName,Notification.USER_ADDED,addedUserName), groupName);
                return true;
            } catch (SQLException e) {
                //do something with exception
            }

        }

        return false;
    }

    /**
     * Method to check if a user is member of a group
     *
     * @param userName
     * @param groupName
     * @return true if it is or false if is not
     */
    private boolean isUserMember(String userName, String groupName) {
        try {

            connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);
            this.statement = connect.createStatement();
            String query = "SELECT EXISTS(SELECT 1 FROM `usersAndGroups` WHERE uid = '" + userName + "' and gid = '" + groupName + "')";
            this.resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int i = 1;
                if (Integer.parseInt(resultSet.getString(i++)) == 1) {
                    return true;
                }
            }

            connect.close();

        } catch (SQLException e) {
            //do something with sql exception
        }

        return false;
    }

    /**
     * Method to check if the group already exists on the db
     *
     * @param groupName
     * @return group exists (true) or not (false)
     */
    private boolean groupExists(String groupName) {
        try {

            connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);
            this.statement = connect.createStatement();
            String query = "SELECT EXISTS(SELECT 1 FROM `groups` WHERE name = '" + groupName + "')";
            this.resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int i = 1;
                if (Integer.parseInt(resultSet.getString(i++)) == 1) {
                    return true;
                }
            }

            connect.close();

        } catch (SQLException e) {
            //do something with sql exception
        }

        return false;
    }

    /**
     * Method to create an user account.
     * Adds the username to users table on db
     *
     * @param userName
     * @param userPassword
     * @return if creates (true) if not (false)
     */
    public boolean createUserAccount(String userName, String userPassword) {

        if (!userExists(userName)) {

            try {
                connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

                this.statement = connect.createStatement();
                String query = "INSERT INTO `users`(`email`,`password`) VALUES ('" + userName + "','" + userPassword + "')";
                statement.executeUpdate(query);


                connect.close();

                return true;

            } catch (SQLException e) {
                //do something with exception
            }
        }

        return false;

    }

   /**
    * Method to get all the bills for a group.
    *
    * @param groupName
    * @return ArrayList<String> bills ID
    */
    public ArrayList<String> getGroupBills (String groupName){

        ArrayList<String> result = new ArrayList<String>();

        try {

            connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

            this.statement = connect.createStatement();
            String query = "SELECT id FROM bills WHERE gid = '" + groupName + "'";
            this.resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int i = 1;
                result.add(resultSet.getString(i++));
            }

            connect.close();

        } catch (SQLException e) {
            //do something with sql exception
        }

        return result;
    }

    /**
     * Method to get the users from a group and their respective balance.
     *
     * @param groupName
     * @return ArrayList<TwoStringsClass> string uses floatValue balance
     */
    public ArrayList<TwoItemsClass> getUserGroupBalance(String groupName){
        ArrayList<TwoItemsClass> result = new ArrayList<TwoItemsClass>();
        ArrayList<String> parcialMem = new ArrayList<String>();
        String query = "";

        try {
            connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

            this.statement = connect.createStatement();
            query = "SELECT uid,SUM(valuePaid)-SUM(valueOwn) FROM bills INNER JOIN groups AS billsAndGroups ON bills.gid = billsAndGroups.name AND bills.gid = '"+groupName+"' INNER JOIN usersAndBills ON usersAndBills.bid = bills.id GROUP BY usersAndBills.uid";
            this.resultSet = statement.executeQuery(query);

            while(resultSet.next()){
                result.add(new TwoItemsClass(resultSet.getString(1),Float.parseFloat(resultSet.getString(2))));
                parcialMem.add(resultSet.getString(1));
            }
            connect.close();
        }
        catch (SQLException e) {
            //do something with sql exception
        }

        ArrayList<String> members = this.getGroupMembers(groupName);

        for (int i = 0; i < members.size(); i++){
            if (!parcialMem.contains(members.get(i))){
                result.add(new TwoItemsClass(members.get(i),0.0f));
            }
        }

        return result;
    }

    /**
     * Method to check if a user already exists in db
     *
     * @param userName
     * @return true if exists, false if not
     */
    private boolean userExists(String userName) {
        try {

            connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);
            this.statement = connect.createStatement();
            String query = "SELECT EXISTS(SELECT 1 FROM `users` WHERE email = '" + userName + "')";
            this.resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int i = 1;
                if (Integer.parseInt(resultSet.getString(i++)) == 1) {
                    return true;
                }
            }

            connect.close();

        } catch (SQLException e) {
            //do something with sql exception
        }

        return false;
    }


    /**
     * Method to get a bill from db.
     * TODO: implement billPicture request
     *
     * @param billID
     * @return the Bill
     */
    public Bill getBill(String billID){

        Bill b = new Bill();
        b.billName = billID;
        String groupName = null;
        Float billValue = null;
        b.billDate = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {

            connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);
            this.statement = connect.createStatement();
            String query = "SELECT * FROM bills WHERE id = '" + billID + "'";
            this.resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                    b.billValue = Float.parseFloat(this.resultSet.getString(2));
                    try {
                        b.billDate.setTime(df.parse(this.resultSet.getString(3)));
                    } catch (Exception e){
                        //do something with the exception
                    }
                    b.billLocationLatitute = Float.parseFloat(this.resultSet.getString(6));
                    b.billLocationLongitude = Float.parseFloat(this.resultSet.getString(7));
            }
            connect.close();

        } catch (SQLException e) {
            //do something with sql exception
        }

        return b;
    }

    public Bitmap getBillPicture(Bill bill){
        byte[] picbytes = null;

        try {
            connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

            this.statement = connect.createStatement();
            String query = "SELECT billPicture FROM bills WHERE bid = '"+bill.billName+"'";
            this.resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
               picbytes = resultSet.getBytes(1);
            }

            connect.close();

        } catch (SQLException e) {
            //do something with sql exception
        }

        Bitmap bitmap = BitmapFactory.decodeByteArray(picbytes, 0, picbytes.length);

        return bitmap;
    }

    /**
     * TODO
     * @param bill
     * @param sessionUserName
     */
    public void createBill(Bill bill, String sessionUserName){
        this.createBill(bill,sessionUserName, true);
    }

    /**
     * TODO
     * @param bill
     * @param sessionUserName
     * @param post
     */
    private void createBill(Bill bill, String sessionUserName, boolean post){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

            this.statement = connect.createStatement();
            String query = "INSERT INTO `bills`(`id`, `value`, `date&time`, `gid`, `latitude`,`longitude`) " +
                    "VALUES ('"+bill.billName+"','"+bill.billValue+"','"+sdf.format(bill.billDate.getTime())
                    +"','"+bill.groupName+"','"+bill.billLocationLatitute+"','"+bill.billLocationLongitude+"')";

            statement.executeUpdate(query);
            connect.close();
            if (post) {this.postNotification(new Notification(sessionUserName,Notification.BILL_CREATED,bill.billName), bill.groupName);}
        } catch (SQLException e) {
            //do something with exception
        }
    }

    public void addPictureToBill(final Bill bill){
/*
        new Thread() {
            public void run() {
*/
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bill.billPicture.compress(Bitmap.CompressFormat.PNG, 13, stream);
                byte[] byteArray = stream.toByteArray();

                Log.d(TAG, "saving picture");
                try
                {
                    connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

                    String query = "INSERT INTO `bills`(`billPicture`) VALUES (?) WHERE '" + bill.billName + "' = bills.id";
                    PreparedStatement psmtm = connect.prepareStatement(query);
                    psmtm.setBytes(1, byteArray);
                    psmtm.execute();

                    connect.close();

                    Log.d(TAG, "picture added");
                } catch (SQLException e)
                {
                    Log.e(TAG, "error upload picture", e);
                    //do something with exception
                }
        /*
            }

        }.start();
        */
    }

    /**
     * Method to create a relation between an user and a bill and its value.
     * @param user
     * @param billName
     *
     */
    public void createUserBillRelation(String user, String billName, float valueOwn, float valuePaid){

        try {
            connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

            this.statement = connect.createStatement();
            String query = "INSERT INTO `usersAndBills`(`uid`, `bid`, `valueOwn`, `valuePaid`) VALUES ('"+user+"','"+billName+"','"+valueOwn+"','"+valuePaid+"')";
            statement.executeUpdate(query);
            connect.close();

        } catch (SQLException e) {
            //do something with exception
        }
    }

    /**
     *
     * @param sessionUserName
     * @param bill
     */
    public void editBill(Bill bill, String oldBillName, String sessionUserName){

        this.deleteBill(oldBillName, sessionUserName,bill.groupName, false);
        this.createBill(bill, sessionUserName, false);
        this.postNotification(new Notification(sessionUserName, Notification.BILL_EDITED, bill.billName), bill.groupName);

    }

    /**
     * Method to get who not paid the bill
     * @param billName
     * @return ArrayList<TwoStringsClass> paid bills
     */
    public ArrayList<TwoItemsClass> getWhoPaidBill(String billName){

        ArrayList<TwoItemsClass> result = new ArrayList<TwoItemsClass>();

        try {
            String user;

            connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

            this.statement = connect.createStatement();
            String query = "SELECT uid,valuePaid FROM usersAndBills WHERE bid = '" + billName + "'";
            this.resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                if(Float.parseFloat(resultSet.getString(2)) != 0) {
                    result.add(new TwoItemsClass(resultSet.getString(1), Float.parseFloat(resultSet.getString(2))));
                }
            }

            connect.close();

        } catch (SQLException e) {
            //do something with sql exception
        }

       return result;
    }

    /**
     * Method to get users that have not paid the bill
     * @param billName
     * @return ArrayList<TwoStringsClass> owns bill
     */
    public ArrayList<TwoItemsClass> getWhoOwesBill(String billName){

        ArrayList<TwoItemsClass> result = new ArrayList<TwoItemsClass>();

        try {
            connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

            this.statement = connect.createStatement();
            String query = "SELECT uid,valueOwn FROM usersAndBills WHERE bid = '"+billName+"'";
            this.resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                if(Float.parseFloat(resultSet.getString(2)) != 0) {
                    result.add(new TwoItemsClass(resultSet.getString(1), Float.parseFloat(resultSet.getString(2))));
                }
            }

            connect.close();

        } catch (SQLException e) {
            //do something with sql exception
        }
        return result;
    }

    /**
     * TODO
     * @param billName
     * @param sessionUserName
     * @param groupName
     */
    public void deleteBill(String billName, String sessionUserName, String groupName){
        this.deleteBill(billName, sessionUserName,groupName, true);
    }

    /**
     * TODO
     * @param billName
     * @param sessionUserName
     * @param groupName
     * @param post
     */
    private void deleteBill(String billName, String sessionUserName, String groupName, boolean post){
        try {
            connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);
            this.statement = connect.createStatement();
            String query = "DELETE FROM usersAndBills WHERE bid = '"+billName+"'";
            statement.executeUpdate(query);
            query = "DELETE FROM bills WHERE id = '"+billName+"'";
            statement.executeUpdate(query);
            connect.close();

            if (post) {this.postNotification(new Notification(sessionUserName,Notification.BILL_DELETED, billName), groupName);}
        } catch (SQLException e) {

            //do something with sql exception
        }
    }

   /**
    * Method to get a group notification. Its ordered by time.
    *
    * @param groupName
    * @return ArrayList<Notification>
    */
    public ArrayList<Notification> getGroupNotifications(String groupName){
        ArrayList<Notification> result = new ArrayList<Notification>();

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);
            this.statement = connect.createStatement();
            String query = "SELECT * FROM groupNotifications WHERE gid = '"+groupName+"' ORDER BY `time` DESC";
            this.resultSet = statement.executeQuery(query);

            while(resultSet.next()){
               Notification n = new Notification(resultSet.getString(3),(Integer.parseInt(resultSet.getString(4))),resultSet.getString(5));
               try {
                   n.date.setTime(df.parse(resultSet.getString(6)));
               } catch (Exception e){

               }
               result.add(n);
            }
            connect.close();

        } catch (SQLException e) {
            //do something with sql exception
        }
        return result;
    }

    /**
     * Method to post a notification on the db.
     *
     * @param notification
     * @param groupName
     * @return true if posted, false if not
     */
    private boolean postNotification(Notification notification, String groupName) {
        boolean result = false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Log.d(TAG, "deleting bill" + notification.description);

            connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

            this.statement = connect.createStatement();
            String query = "INSERT INTO `groupNotifications`(`nid`, `gid`, `uid`, `type`, `details`, `time`) VALUES " +
                    "('" + notification.hashCode() + "','" + groupName + "','" + notification.owner + "','" + notification.type +
                    "','" + notification.description + "','" + sdf.format(notification.date.getTime()) + "')";
            statement.executeUpdate(query);
            connect.close();
            result = true;

        } catch (SQLException e) {
            Log.e(TAG, "post delete bill " + notification.description, e);
            //do something with exception
        }
        return result;
    }

}
