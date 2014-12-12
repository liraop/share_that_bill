/**
 *
 * ShareThatBill
 *
 * CSE444 - Mobile Application Programming
 * Prof. Robert J. Irwin
 *
 * Team:
 * Jose E. Almas de Jesus Junior - jeajjr@gmail.com
 * Pedro de Oliveira Lira - pedulira@gmail.com
 *
 */

package com.mobapp.almaslira.sharethatbill;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Picture;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TextView;

/**
 * Group activity:
 * - interface to interact in a group.
 */

public class GroupActivity extends TabActivity implements View.OnClickListener {
	static final String TAG = "GroupActivity";

    String thisUserName;
	String thisGroupName;
	TabHost tabHost;

    boolean logOut;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_group);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
            thisUserName = extras.getString("user_name");
			thisGroupName = extras.getString("group_name");
		}

		TextView title = (TextView) findViewById(R.id.textViewGroupTitle);

		title.setText(thisGroupName);

		tabHost = (TabHost) findViewById(android.R.id.tabhost);

        TabHost.TabSpec notificationsTabSpec = tabHost.newTabSpec("First");
        notificationsTabSpec.setIndicator("Updates", null);
        Intent notificationsIntent = new Intent(this, NotificationsTab.class);
        notificationsIntent.putExtra("group_name", thisGroupName);
        notificationsTabSpec.setContent(notificationsIntent);

		TabHost.TabSpec billsTabSpec = tabHost.newTabSpec("Second");
		billsTabSpec.setIndicator("Bills", null);
		Intent billsIntent = new Intent(this, BillsTab.class);
        billsIntent.putExtra("user_name", thisUserName);
        billsIntent.putExtra("group_name", thisGroupName);
		billsTabSpec.setContent(billsIntent);

		TabHost.TabSpec membersTabSpec = tabHost.newTabSpec("Third");
		membersTabSpec.setIndicator("Members", null);
		Intent membersIntent = new Intent(this, MembersTab.class);
        membersIntent.putExtra("user_name", thisUserName);
        membersIntent.putExtra("group_name", thisGroupName);
		membersTabSpec.setContent(membersIntent);

        tabHost.addTab(notificationsTabSpec);
        tabHost.addTab(billsTabSpec);
		tabHost.addTab(membersTabSpec);

        ImageButton logoutButton = (ImageButton) findViewById(R.id.imageButtonGroupLogout);
        logoutButton.setOnClickListener(this);
	}

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick");

        Intent intent;

        switch (view.getId()) {
            case R.id.imageButtonGroupLogout:
                logOut = true;

                intent = new Intent(GroupActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();

        if (logOut)
            finish();
    }
}
