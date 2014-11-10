package com.mobapp.almaslira.sharethatbill;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class ProfileActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
	static final String TAG = "ProfileActivity";

	private ListView groupsList;
	private TextView noGroupsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_profile);

		this.groupsList = (ListView) findViewById(R.id.listViewProfileGroups);
		noGroupsText = (TextView) findViewById(R.id.textViewProfileNoGroups);

		List<String> groupNamesList = new ArrayList<String>();

		groupNamesList.add("Create a new group...");
		for (int i=1; i<=20; i++) {
			groupNamesList.add("Group " + i);
		}
		
		if (groupNamesList.size() == 0) {
			noGroupsText.setVisibility(View.VISIBLE);
		}
		else {
			noGroupsText.setVisibility(View.GONE);
		}

		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
				this,
				android.R.layout.simple_list_item_1,
				groupNamesList );

		groupsList.setAdapter(arrayAdapter);

		groupsList.setOnItemClickListener(this);
    }

	@Override
	public void onClick(View view) {
		Log.d(TAG, "onClick");
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		Log.d(TAG, "onItemClick: " + i);

		if (i == 0)
			startActivity(new Intent(ProfileActivity.this, CreateGroupActivity.class));
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile, menu);
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
