package com.mobapp.almaslira.sharethatbill;

import android.graphics.Picture;
import android.location.Location;
import android.util.Log;
import java.sql.*;
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


    /* Method to create a group. It uses groupExists method
     * then creates the group or not. Adds the group on the groups table
     *
     * @param userName
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


    /*
     * Method to add an user to a group.
     * User and group are added on usersAndGroups table.
     *
     * @param userName
     * @param groupName
     * @return
     */
    public boolean addUserToGroup(String userName, String groupName) {

        if (!isUserMember(userName, groupName)) {
            try {
                connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

                this.statement = connect.createStatement();
                String query = "INSERT INTO `usersAndGroups`(`uid`, `gid`) VALUES ('" + userName + "','" + groupName + "')";
                statement.executeUpdate(query);

                connect.close();

                return true;
            } catch (SQLException e) {
                //do something with exception
            }
        }

        return false;
    }

    /*
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


    /*
     * Method to get all the bills for a group.
     *
     * @param groupName
     * @return ArrayList<String> bills
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
     * Method to get a bill from db.
     * TODO: implement location,picture request
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
                    b.groupName = this.resultSet.getString(6);
            }
            connect.close();

        } catch (SQLException e) {
            //do something with sql exception
        }

        return b;
    }

    /*
     * Method to create a bill on db.
     * @param bill
     */
    public void createBill(Bill bill){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

            this.statement = connect.createStatement();
            String query = "INSERT INTO `bills`(`id`, `value`, `date&time`, `gid`) " +
                    "VALUES ('"+bill.billName+"','"+bill.billValue+"','"+sdf.format(bill.billDate.getTime())+"','"+bill.groupName+"')";

           statement.executeUpdate(query);


            connect.close();

        } catch (SQLException e) {
            //do something with exception
        }
    }

    /*
     * Method to create a relation between an user and a bill and its value.
     * @param user
     * @param billName
     * @param value
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

    /*
     * Method to get who not paid the bill
     * @param billName
     * @return ArrayList<TwoStringsClass> paid bills
     */
    public ArrayList<TwoStringsClass> getWhoPaidBill(String billName){

        ArrayList<TwoStringsClass> result = new ArrayList<TwoStringsClass>();

        try {
            String user;

            connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

            this.statement = connect.createStatement();
            String query = "SELECT uid,valuePaid FROM usersAndBills WHERE bid = '" + billName + "'";
            this.resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                if(Float.parseFloat(resultSet.getString(2)) > 0) {
                    result.add(new TwoStringsClass(resultSet.getString(1),resultSet.getString(2)));
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
    public ArrayList<TwoStringsClass> getWhoOwnsBill(String billName){

        ArrayList<TwoStringsClass> result = new ArrayList<TwoStringsClass>();

        try {
            connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

            this.statement = connect.createStatement();
            String query = "SELECT uid,valueOwn FROM usersAndBills WHERE bid = '"+billName+"'";
            this.resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                if(Float.parseFloat(resultSet.getString(2)) < 0) {
                    result.add(new TwoStringsClass(resultSet.getString(1),resultSet.getString(2)));
                }
            }

            connect.close();

        } catch (SQLException e) {
            //do something with sql exception
        }
        return result;
    }
}