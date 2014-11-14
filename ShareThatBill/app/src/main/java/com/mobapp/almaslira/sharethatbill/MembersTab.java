package com.mobapp.almaslira.sharethatbill;

import android.app.Activity;
import android.app.ProgressDialog;
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
 * Created by jalmasde on 11/11/14.
 */
public class MembersTab extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
	static final String TAG = "MembersTab";

    String thisUserName;
    String thisGroupName;
    ProgressDialog progressDialog;

    private ListView membersList;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> memberNamesList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.layout_group_tabs);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            thisUserName = extras.getString("user_name");
            thisGroupName = extras.getString("group_name");
        }

        progressDialog = new ProgressDialog(MembersTab.this);
		progressDialog.setMessage(getResources().getString(R.string.warning_loading));
        progressDialog.setCancelable(false);

        ImageButton addButton = (ImageButton) findViewById(R.id.imageButtonTabsAdd);
        addButton.setOnClickListener(this);

        TextView addText = (TextView) findViewById(R.id.textViewGroupTabAdd);
        addText.setText(getResources().getString(R.string.tabs_members_add));

        membersList = (ListView) findViewById(R.id.listViewTabsList);

        memberNamesList = new ArrayList<String>();

        arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                memberNamesList);

        membersList.setAdapter(arrayAdapter);

        arrayAdapter.notifyDataSetChanged();
        membersList.setOnItemClickListener(this);

        updateMembers();

	}

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageButtonTabsAdd:
                Log.d(TAG, "add button");
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    void updateMembers() {
        runOnUiThread(new Runnable(){
            public void run() {
                progressDialog.show();
            }
        });

        new Thread() {
            public void run() {
                Log.d(TAG, "in thread updateMembers");

                memberNamesList = ((ShareThatBillApp) getApplication()).dataBase.getGroupUsers(thisGroupName);

                if (memberNamesList != null) {

                    removeThisUserFromMembersList();
                    memberNamesList.add(0, thisUserName + " " + getResources().getString(R.string.members_tab_you));

                    Log.d(TAG, "group members (" + memberNamesList.size() + ":");
                    for (String s : memberNamesList)
                        Log.d(TAG, "user: " + s);
                }
                else
                    Log.d(TAG, "list null");

                runOnUiThread(new Runnable(){
                    public void run() {
                        updateList();

                        progressDialog.dismiss();
                    }
                });
            }
        }.start();
    }

    void removeThisUserFromMembersList() {
        Log.d(TAG, "removing user: " + thisUserName +  ",size: " + memberNamesList.size());

        for (int i = 0; i < memberNamesList.size(); ++i) {
            if (memberNamesList.get(i).compareTo(thisUserName) == 0) {
                memberNamesList.remove(i);
                return;
            }
        }
    }

    void updateList() {
        arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                memberNamesList);

        membersList.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume");
    }
}
