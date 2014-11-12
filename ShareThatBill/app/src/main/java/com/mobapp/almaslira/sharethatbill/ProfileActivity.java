package com.mobapp.almaslira.sharethatbill;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class ProfileActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
	static final String TAG = "ProfileActivity";

	private ListView groupsList;
	private TextView noGroupsText;
	private ArrayAdapter<String> arrayAdapter;
	private List<String> groupsNamesList;
	private String userName;

	boolean readyToUpdate;

	ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_profile);

		this.groupsList = (ListView) findViewById(R.id.listViewProfileGroups);
		noGroupsText = (TextView) findViewById(R.id.textViewProfileNoGroups);

		ImageButton createGroupButton = (ImageButton) findViewById(R.id.imageButtonProfileCreateGroup);
		createGroupButton.setOnClickListener(this);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			userName = extras.getString("user_name");
		}

		groupsNamesList = new ArrayList<String>();

		arrayAdapter = new ArrayAdapter<String>(
				this,
				android.R.layout.simple_list_item_1,
				groupsNamesList);

		groupsList.setAdapter(arrayAdapter);

		arrayAdapter.notifyDataSetChanged();
		groupsList.setOnItemClickListener(this);

		readyToUpdate = false;

		progressDialog = new ProgressDialog(ProfileActivity.this);
		progressDialog.setMessage(getResources().getString(R.string.warning_loading));

		final Handler handler = new Handler();
		Timer timer = new Timer();
		TimerTask doAsynchronousTask = new TimerTask() {
			@Override
			public void run() {
				handler.post(new Runnable() {
					public void run() {
						try {
							updateNoGroupsText();
						} catch (Exception e) {
						}
					}
				});
			}
		};
		timer.schedule(doAsynchronousTask, 0, 200);

		progressDialog.show();
		updateGroups();
	}

	void updateGroups() {
		new Thread() {
			public void run() {
				Log.d(TAG, "in thread");

				groupsNamesList = FakeDataBase.getUserGroups(userName);
				if (groupsNamesList != null) {
					groupsNamesList.add(0, getResources().getString(R.string.profile_create_new_group));
					Log.d(TAG, "list not null");

					for (String s : groupsNamesList)
						Log.d(TAG, "group: " + s);
				}
				else
					Log.d(TAG, "list null");

				progressDialog.dismiss();
				readyToUpdate = true;
			}
		}.start();

	}

	void updateNoGroupsText() {
		if (groupsNamesList.size() == 0) {
			noGroupsText.setVisibility(View.VISIBLE);
		} else {
			noGroupsText.setVisibility(View.GONE);
		}

		if (readyToUpdate) {
			Log.d(TAG, "readyToUpdate");
			readyToUpdate = false;
			arrayAdapter.notifyDataSetChanged();
			progressDialog.dismiss();
		}
	}
/*
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_profile);

        this.groupsList = (ListView) findViewById(R.id.listViewProfileGroups);
        noGroupsText = (TextView) findViewById(R.id.textViewProfileNoGroups);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userName = extras.getString("user_name");
            groupsNamesList = extras.getStringArrayList("user_groups");
        }

        groupsNamesList.add(0, getResources().getString(R.string.profile_create_new_group));

        arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                groupsNamesList);

        groupsList.setAdapter(arrayAdapter);

        if (groupsNamesList.size() == 0) {
            noGroupsText.setVisibility(View.VISIBLE);
        } else {
            noGroupsText.setVisibility(View.GONE);
        }

        arrayAdapter.notifyDataSetChanged();
        groupsList.setOnItemClickListener(this);
    }
*/

	@Override
	public void onClick(View view) {
		Log.d(TAG, "onClick");

		switch (view.getId()) {
			case R.id.imageButtonProfileCreateGroup:
				startActivity(new Intent(ProfileActivity.this, CreateGroupActivity.class));
				break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
		Log.d(TAG, "onItemClick: " + i);

		new Thread() {
			public void run() {
				Log.d(TAG, "in thread: fetch groupName information");

				Intent intent = new Intent(ProfileActivity.this, GroupActivity.class);
				intent.putExtra("group_name", groupsNamesList.get(i));
				startActivity(intent);
			}
		}.start();
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
