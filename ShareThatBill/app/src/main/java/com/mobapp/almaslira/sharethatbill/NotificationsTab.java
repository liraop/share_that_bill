package com.mobapp.almaslira.sharethatbill;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class NotificationsTab extends Activity {
    static final String TAG = "NotificationsTab";
    private static DBhandler dbhandler = new DBhandler();

    String thisGroupName;
    ProgressDialog progressDialog;

    ListView notificationsList;

    CustomTwoItemAdapter arrayAdapter;
    ArrayList<TwoStringsClass> membersBalanceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_group_tabs);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            thisGroupName = extras.getString("group_name");
        }

        progressDialog = new ProgressDialog(NotificationsTab.this);
        progressDialog.setMessage(getResources().getString(R.string.warning_loading));
        progressDialog.setCancelable(false);

        fetchNotification();
    }

    void fetchNotification() {
        progressDialog.show();

        new Thread() {
            public void run() {

                //get notification

                runOnUiThread(new Runnable() {
                    public void run() {


                        progressDialog.dismiss();
                    }
                });

            }
        }.start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notifications_tab, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
