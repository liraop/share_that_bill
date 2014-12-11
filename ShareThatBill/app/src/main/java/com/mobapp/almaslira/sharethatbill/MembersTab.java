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
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * MembersTab activity:
 * - shows all group's members
 * - add member option
 */
public class MembersTab extends Activity implements View.OnClickListener {
	static final String TAG = "MembersTab";

    String thisUserName;
    String thisGroupName;
    ProgressDialog progressDialog;

    ListView membersList;

    CustomTwoItemAdapter arrayAdapter;
    ArrayList<TwoItemsClass> membersBalanceList;

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

        updateMembers();

	}

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageButtonTabsAdd:
                Log.d(TAG, "add button");

                createAddUserDialog();
                break;
        }
    }

    public void createAddUserDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(getResources().getString(R.string.members_tab_add_member));

        final EditText newMemberInput = new EditText(this);
        newMemberInput.setHint(getResources().getString(R.string.create_group_email_hint));
        alert.setView(newMemberInput);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String newMember = newMemberInput.getText().toString();

                Log.d(TAG, "add " + newMember);

                membersBalanceList.add(new TwoItemsClass(newMember, 0.0f));

                addUser(newMember);
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

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private void addUser(final String newMember) {
        progressDialog.show();

        new Thread() {
            public void run() {

                if (isValidEmail(newMember)) {
                    if (((ShareThatBillApp) getApplication()).dBhandler.addUserToGroup(newMember, thisGroupName,thisUserName)) {
                        Log.d(TAG, "user " + newMember + " added");

                        runOnUiThread(new Runnable() {
                            public void run() {
                                arrayAdapter.notifyDataSetChanged();

                                progressDialog.dismiss();

                                Toast.makeText(MembersTab.this, getResources().getString(R.string.members_tab_warning_user_user_added), Toast.LENGTH_LONG).show();
                            }
                        });


                    } else {
                        Log.d(TAG, "user " + newMember + " not added");

                        runOnUiThread(new Runnable() {
                            public void run() {
                                progressDialog.dismiss();

                                createWarningAlert(getResources().getString(R.string.warning_error),
                                        getResources().getString(R.string.members_tab_warning_user_already_on_group));
                            }
                        });
                    }
                }
                else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();

                            createWarningAlert(getResources().getString(R.string.warning_error),
                                    getResources().getString(R.string.warning_invalid_email));
                        }
                    });
                }
            }
        }.start();
    }

    private void createWarningAlert (String title, String warning) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MembersTab.this);

        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(warning);

        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * Download member list of the group.
     */
    void updateMembers() {
        runOnUiThread(new Runnable() {
            public void run() {
                progressDialog.show();
            }
        });

        new Thread() {
            public void run() {
                Log.d(TAG, "in thread updateBills");

                membersBalanceList = ((ShareThatBillApp) getApplication()).dBhandler.getUserGroupBalance(thisGroupName);

                Log.d(TAG, "list size " + membersBalanceList.size());
                runOnUiThread(new Runnable(){
                    public void run() {

                        TwoItemsClass thisUserTemp = removeThisUserFromMembersList();
                        thisUserTemp.string += " " + getResources().getString(R.string.members_tab_you);
                        membersBalanceList.add(0, thisUserTemp);

                        arrayAdapter = new CustomTwoItemAdapter(MembersTab.this, membersBalanceList);
                        membersList.setAdapter(arrayAdapter);
                        arrayAdapter.notifyDataSetChanged();

                        progressDialog.dismiss();
                    }
                });
            }
        }.start();
    }

    TwoItemsClass removeThisUserFromMembersList() {
        Log.d(TAG, "removing user: " + thisUserName +  ",size: " + membersBalanceList.size());

        for (int i = 0; i < membersBalanceList.size(); ++i) {
            if (membersBalanceList.get(i).string.compareTo(thisUserName) == 0) {
                TwoItemsClass toReturn;
                toReturn = membersBalanceList.get(i);
                membersBalanceList.remove(i);
                return toReturn;
            }
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume");

        updateMembers();
    }
}
