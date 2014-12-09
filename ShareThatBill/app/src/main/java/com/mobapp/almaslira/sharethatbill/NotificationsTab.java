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

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * NotificationsTab activity:
 * - shows all group's notifications
 */
public class NotificationsTab extends Activity {
    static final String TAG = "NotificationsTab";
    private static DBhandler dbhandler = new DBhandler();

    String thisGroupName;
    ProgressDialog progressDialog;

    ListView notificationsListView;

    NotificationAdapter arrayAdapter;
    ArrayList<Notification> notificationList;

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

        notificationsListView = (ListView) findViewById(R.id.listViewTabsList);

        ImageButton addButton = (ImageButton) findViewById(R.id.imageButtonTabsAdd);
        addButton.setVisibility(View.GONE);

        TextView addText = (TextView) findViewById(R.id.textViewGroupTabAdd);
        addText.setVisibility(View.GONE);

        fetchNotification();
    }

    void fetchNotification() {
        progressDialog.show();

        new Thread() {
            public void run() {

                notificationList = dbhandler.getGroupNotifications(thisGroupName);

                runOnUiThread(new Runnable() {
                    public void run() {
                        arrayAdapter = new NotificationAdapter(NotificationsTab.this, notificationList);
                        notificationsListView.setAdapter(arrayAdapter);
                        arrayAdapter.notifyDataSetChanged();

                        progressDialog.dismiss();
                    }
                });
            }
        }.start();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

        fetchNotification();
    }
}
