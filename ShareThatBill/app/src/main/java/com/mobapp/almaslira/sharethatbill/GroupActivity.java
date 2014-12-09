package com.mobapp.almaslira.sharethatbill;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TabHost;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Group activity:
 * - interface to interact in a group.
 */

public class GroupActivity extends TabActivity {
	static final String TAG = "GroupActivity";

    String thisUserName;
	String thisGroupName;
	TabHost tabHost;

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

        thisUserName = "user1@test.com";
        thisGroupName = "House bills";

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
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.group, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
