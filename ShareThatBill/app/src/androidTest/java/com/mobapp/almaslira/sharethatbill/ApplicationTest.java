package com.mobapp.almaslira.sharethatbill;

import android.app.Application;
import android.test.ApplicationTestCase;

import java.sql.SQLException;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void testLogin(){
        DBhandler dbhandler = new DBhandler();
        boolean t = false;

        try{
            t = dbhandler.checkLogin("user1","1234");
        } catch (SQLException e){
            System.out.print(e.getMessage());
        }

        assertTrue(t);
    }

}