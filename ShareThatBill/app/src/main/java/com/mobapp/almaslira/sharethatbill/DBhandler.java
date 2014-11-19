package com.mobapp.almaslira.sharethatbill;



import java.sql.*;
import java.util.ArrayList;

public class DBhandler {
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
         *
         * Method for check the user credentials on the database.
         * If there are no match, returns false.
         * If matches, return true.
         *
         * @param userEmail
         * @param password
         * @return
         * @throws SQLException
         */
        public boolean checkLogin(String userEmail, String password) throws SQLException {
            boolean isValid = false;

            connect = DriverManager.getConnection(HOST, DB_USER, DB_PW);

            this.statement = connect.createStatement();
            String query = "SELECT * FROM users";
            this.resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int i = 1;
                while (i <= 2) {
                    if(resultSet.getString(i++).equals(userEmail) && resultSet.getString(i).equals(password)){
                        isValid = true;
                        break;
                    }
                }
            }
            return isValid;
        }

    /*
         public ArrayList<String> getUserGroups (String email) {

            ArrayList<String> result = new ArrayList<String>();

            for (UserGroupRelation ugr : this.userGroupRelations) {
                if (ugr.userEmail.compareTo(email) == 0)
                    result.add(ugr.groupName);
            }

            return result;
        }

    */

}
