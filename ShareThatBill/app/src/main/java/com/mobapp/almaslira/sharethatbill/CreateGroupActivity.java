package com.mobapp.almaslira.sharethatbill;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class CreateGroupActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
	static final String TAG = "CreateGroupActivity";
    private static DBhandler dbhandler = new DBhandler();

	ListView membersListView;
	List<String> membersListStrings;
	ArrayAdapter<String> arrayAdapter;
    String userName;

    ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_create_group);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userName = extras.getString("user_name");
        }

		membersListView = (ListView) findViewById(R.id.listViewCreateGroupMembers);
		membersListView.setOnItemClickListener(this);

		membersListStrings = new ArrayList<String>();

		arrayAdapter = new ArrayAdapter<String>(
				this,
				android.R.layout.simple_list_item_1,
				membersListStrings );

		membersListView.setAdapter(arrayAdapter);
		membersListView.setOnItemClickListener(this);

		Button createButton = (Button) findViewById(R.id.buttonCreateGroupCreate);
		createButton.setOnClickListener(this);

		ImageButton addButton = (ImageButton) findViewById(R.id.imageButtonCreateGroupAdd);
		addButton.setOnClickListener(this);

        progressDialog = new ProgressDialog(CreateGroupActivity.this);
        progressDialog.setMessage(getResources().getString(R.string.warning_loading));
        progressDialog.setCancelable(false);
	}

	@Override
	public void onClick(View view) {
		Log.d(TAG, "onClick");

		switch (view.getId()) {
			case R.id.imageButtonCreateGroupAdd:
				Log.d(TAG, "add button");

				EditText email = (EditText) findViewById(R.id.editTextCreateGroupAddEmail);

				InputMethodManager imm = (InputMethodManager)getSystemService(
						Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(email.getWindowToken(), 0);

				if (isValidEmail(email.getText())) {
					if (membersListStrings.contains(email.getText().toString())) {
						createWarningAlert(getResources().getString(R.string.warning_error),
								getResources().getString(R.string.create_group_warning_email_already_added));
					}
					else {
						membersListStrings.add(email.getText().toString());
						arrayAdapter.notifyDataSetChanged();
						email.setText("");
					}
				}
				else {
					createWarningAlert(getResources().getString(R.string.warning_error),
							getResources().getString(R.string.warning_invalid_email));
				}

				break;

			case R.id.buttonCreateGroupCreate:
				Log.d(TAG, "create groupName");

				EditText groupName = (EditText) findViewById(R.id.editTextCreateGroupName);
				final String groupNameString = groupName.getText().toString();

				if (groupNameString .length() != 0) {
					Log.d(TAG, "groupName billName: " + groupNameString + ", groupName size: " + this.membersListStrings.size());

                    registerGroup(groupNameString);
				}
				else {
					createWarningAlert(getResources().getString(R.string.warning_error),
							getResources().getString(R.string.warning_group_name_invalid));
				}
				break;
		}
	}

    private void registerGroup(final String groupNameString) {
        progressDialog.show();

        new Thread() {
            public void run() {
                Log.d(TAG, "in thread");


                if (dbhandler.createGroup(groupNameString)) {

                    Log.d(TAG, "group created");

                    membersListStrings.add(userName);

                    for (String m : membersListStrings) {
                        dbhandler.addUserToGroup(m, groupNameString);
                    }

                    Log.d(TAG, "members added");

                    Intent intent = new Intent(getApplicationContext(), GroupActivity.class);
                    intent.putExtra("user_name", userName);
                    intent.putExtra("group_name", groupNameString);
                    startActivity(intent);

                    progressDialog.dismiss();
                    finish();
                }
                else {
                    progressDialog.dismiss();
                    CreateGroupActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Log.d("TAG", "on the UI thread");
                            createWarningAlert(getResources().getString(R.string.warning_error),
                                    getResources().getString(R.string.warning_creating_group_fail));
                        }
                    });
                }
            }
        }.start();
    }

	private void createWarningAlert (String title, String warning) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreateGroupActivity.this);

		alertDialogBuilder.setTitle(title);
		alertDialogBuilder.setMessage(warning);

		alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
			}
		});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	public final static boolean isValidEmail(CharSequence target) {
		return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		Log.d(TAG, "onItemClick: " + i);

		createUpdateEmailDialog(i);
	}

	/**
	 * Pops a dialog with the option to edit the clicked userEmail or remove it.
	 * @param index: item clicked
	 */
	void createUpdateEmailDialog (final int index) {

		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Edit userEmail");

		final EditText newEmail = new EditText(this);
		newEmail.setText(this.membersListStrings.get(index));
		alert.setView(newEmail);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String newValue = newEmail.getText().toString();

				Log.d(TAG, "Ok to edit to " + newValue);

				membersListStrings.remove(index);
				membersListStrings.add(index, newValue);
				arrayAdapter.notifyDataSetChanged();
			}
		});

		alert.setNeutralButton("Remove", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value1 = newEmail.getText().toString();

				Log.d(TAG, "remove userEmail: " + value1);

				membersListStrings.remove(index);
				arrayAdapter.notifyDataSetChanged();
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Log.d(TAG, "alert cancelled");
			}
		});


		AlertDialog alertDialog = alert.create();
		alertDialog.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_group, menu);
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
