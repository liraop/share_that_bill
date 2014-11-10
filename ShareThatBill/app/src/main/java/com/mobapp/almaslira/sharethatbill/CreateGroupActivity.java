package com.mobapp.almaslira.sharethatbill;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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

import java.util.ArrayList;
import java.util.List;


public class CreateGroupActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
	static final String TAG = "CreateGroupActivity";

	ListView membersListView;
	List<String> membersListStrings;
	ArrayAdapter<String> arrayAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_create_group);

		ImageButton addButton = (ImageButton) findViewById(R.id.imageButtonCreateGroupAdd);
		addButton.setOnClickListener(this);

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
	}

	@Override
	public void onClick(View view) {
		Log.d(TAG, "onClick");

		switch (view.getId()) {
			case R.id.imageButtonCreateGroupAdd:
				Log.d(TAG, "add button");

				EditText email = (EditText) findViewById(R.id.editTextCreateGroupAddEmail);

				//TODO: test in real device
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
				Log.d(TAG, "create group");

				EditText groupName = (EditText) findViewById(R.id.editTextCreateGroupName);
				final String groupNameString = groupName.getText().toString();

				if (groupNameString .length() != 0) {
					Log.d(TAG, "group name: " + groupNameString + ", group size: " + this.membersListStrings.size());
				}
				else {
					createWarningAlert(getResources().getString(R.string.warning_error),
							getResources().getString(R.string.warning_group_name_invalid));
				}
				break;
		}
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
	 * Pops a dialog with the option to edit the clicked email or remove it.
	 * @param index: item clicked
	 */
	void createUpdateEmailDialog (int index) {
		final int indexf = index;

		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Edit email");

		final EditText newEmail = new EditText(this);
		newEmail.setText(this.membersListStrings.get(index));
		alert.setView(newEmail);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String newValue = newEmail.getText().toString();

				Log.d(TAG, "Ok to edit to " + newValue);

				membersListStrings.remove(indexf);
				membersListStrings.add(indexf, newValue);
				arrayAdapter.notifyDataSetChanged();
			}
		});

		alert.setNeutralButton("Remove", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value1 = newEmail.getText().toString();

				Log.d(TAG, "remove email: " + value1);

				membersListStrings.remove(indexf);
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
