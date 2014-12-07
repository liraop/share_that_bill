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
    static final String TAG = "AAAAAAAAAAAAAAA";

    public ApplicationTest() {
        super(Application.class);
    }

    public void testGroupExists(){
        DBhandler dbhandler = new DBhandler();

        ArrayList<TwoStringsClass>membersBalanceList = dbhandler.getUserGroupBalance("group1");

        Log.d(TAG, ""+membersBalanceList.size());

    }

}