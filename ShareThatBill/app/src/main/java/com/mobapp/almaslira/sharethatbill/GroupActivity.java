package com.mobapp.almaslira.sharethatbill;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;


public class GroupActivity extends TabActivity {
	static final String TAG = "GroupActivity";

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
			thisGroupName = extras.getString("group_name");
		}

		TextView title = (TextView) findViewById(R.id.textViewGroupTitle);

		if (title != null) {
			if (thisGroupName != null)
				title.setText(thisGroupName);
			else
				title.setText("sdds");
		}
		else
			Log.d(TAG, "title missing");

		tabHost = (TabHost) findViewById(android.R.id.tabhost);



		TabHost.TabSpec billsTabSpec = tabHost.newTabSpec("First");
		billsTabSpec.setIndicator("Bills", null);
		Intent billsIntent = new Intent(this, BillsTab.class);
		billsTabSpec.setContent(billsIntent);

		TabHost.TabSpec membersTabSpec = tabHost.newTabSpec("Second");
		membersTabSpec.setIndicator("Members", null);
		Intent membersIntent = new Intent(this, MembersTab.class);
		membersTabSpec.setContent(membersIntent);

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
