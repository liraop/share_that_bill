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
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

/**
 * Profile activity:
 * - shows user's groups
 * - option to create new group
 */
public class ProfileActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
	static final String TAG = "ProfileActivity";

	private ListView groupsListView;
	private TextView noGroupsText;
	private ArrayAdapter<String> arrayAdapter;
	private List<String> groupsNamesList;
	private String userName;
    private static DBhandler dbhandler = new DBhandler();


    ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_profile);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userName = extras.getString("user_name");
        }

        Log.d(TAG, "user: " + userName);

		this.groupsListView = (ListView) findViewById(R.id.listViewProfileGroups);
		noGroupsText = (TextView) findViewById(R.id.textViewProfileNoGroups);

		ImageButton createGroupButton = (ImageButton) findViewById(R.id.imageButtonProfileCreateGroup);
		createGroupButton.setOnClickListener(this);

		groupsNamesList = new ArrayList<String>();

		arrayAdapter = new ArrayAdapter<String>(
				this,
				android.R.layout.simple_list_item_1,
				groupsNamesList);

		groupsListView.setAdapter(arrayAdapter);

		arrayAdapter.notifyDataSetChanged();
		groupsListView.setOnItemClickListener(this);

		progressDialog = new ProgressDialog(ProfileActivity.this);
		progressDialog.setMessage(getResources().getString(R.string.warning_loading));
        progressDialog.setCancelable(false);

		updateGroups();
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "onResume");

		super.onResume();

		progressDialog.show();
		updateGroups();
	}

	void updateGroups() {
        progressDialog.show();

		new Thread() {
			public void run() {
				Log.d(TAG, "in thread updateGroups");

				groupsNamesList = dbhandler.getUserGroups(userName);

				if (groupsNamesList != null) {

					Log.d(TAG, "user " + userName + "'s groups:");
					for (String s : groupsNamesList)
						Log.d(TAG, "group: " + s);
				}
				else
					Log.d(TAG, "list null");


				progressDialog.dismiss();

                runOnUiThread(new Runnable(){
                    public void run() {
                        updateNoGroupsText();
                    }
                });
			}
		}.start();
	}

	void updateNoGroupsText() {
        Log.d(TAG, "updating");

        arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                groupsNamesList);

        groupsListView.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();

        progressDialog.dismiss();

        if (groupsNamesList.size() == 0) {
            this.noGroupsText.setVisibility(View.VISIBLE);
        } else {
            this.noGroupsText.setVisibility(View.INVISIBLE);
        }
	}

	@Override
	public void onClick(View view) {
		Log.d(TAG, "onClick");

		switch (view.getId()) {
			case R.id.imageButtonProfileCreateGroup:
                Intent intent = new Intent(ProfileActivity.this, CreateGroupActivity.class);
                intent.putExtra("user_name", userName);
                startActivity(intent);
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
                intent.putExtra("user_name", userName);
                intent.putExtra("group_name", groupsNamesList.get(i));
                startActivity(intent);
			}
		}.start();
	}
}
