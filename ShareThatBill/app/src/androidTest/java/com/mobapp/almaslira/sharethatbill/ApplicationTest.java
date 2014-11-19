package com.mobapp.almaslira.sharethatbill;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    static final String TAG = "LoginActivity";

    public ApplicationTest() {
        super(Application.class);
    }

    public void testLogin(){
        DBhandler dbhandler = new DBhandler();

        assertTrue(dbhandler.checkLogin("user1","1234"));

    }

    public void testGetUserGroups(){
        DBhandler dbhandler = new DBhandler();
        ArrayList<String> result = new ArrayList<String>();

        try{
            dbhandler.getUserGroups("user1");
        } catch (SQLException e){
            System.out.print(e.getMessage());
        }
    }

}