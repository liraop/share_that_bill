/**
 *
 * ShareThatBill
 *
 * CSE444 - Mobile Application Development
 * Prof. Robert J. Irwin
 *
 * Team:
 * Jose E. Almas de Jesus Junior - jeajjr@gmail.com
 * Pedro de Oliveira Lira - pedulira@gmail.com
 *
 */

package com.mobapp.almaslira.sharethatbill;

import android.app.Application;
import android.util.Log;

/**
 * Created by José Ernesto on 11/12/2014.
 */
public class ShareThatBillApp extends Application {
	static final String TAG = "ShareThatBillApp";

	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");

		super.onCreate();
	}

    @Override
    public void onTerminate() {
        Log.d(TAG, "onTerminate");
        super.onTerminate();
    }
}
