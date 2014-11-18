package com.mobapp.almaslira.sharethatbill;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;

import java.util.ArrayList;


public class CreateBillActivity extends Activity implements RadioGroup.OnCheckedChangeListener {
    static final String TAG = "CreateBillActivity";

    String groupName;
    ArrayList<TwoStringsClass> whoPaid;
    ArrayList<TwoStringsClass> whoOwns;

    CustomTwoItemAdapter whoPaidArrayAdapter;
    CustomTwoItemAdapter whoOwnsArrayAdapter;

    boolean dividingEquallyFlag;
    boolean[] dividingEquallyMembers;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_create_bill);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            groupName = extras.getString("group_name");
        }

        groupName = new String("group1");

        progressDialog = new ProgressDialog(CreateBillActivity.this);
        progressDialog.setMessage(getResources().getString(R.string.warning_loading));
        progressDialog.setCancelable(false);

        dividingEquallyFlag = true;

        RadioGroup  radioGroup = (RadioGroup) findViewById(R.id.RadioGroupCreateBillSplit);
        radioGroup.setOnCheckedChangeListener(this);

        fetchMembersList();
    }

    public void fetchMembersList() {
        Log.d(TAG, "fetchMembersList");

        progressDialog.show();

        new Thread() {
            public void run() {
                ArrayList<String> members = ((ShareThatBillApp) getApplication()).dataBase.getGroupUsers(groupName);

                whoPaid = new ArrayList<TwoStringsClass>();
                whoOwns = new ArrayList<TwoStringsClass>();

                for (String m : members) {
                    whoPaid.add(new TwoStringsClass(m, "0.00"));
                    whoOwns.add(new TwoStringsClass(m, "0.00"));
                    Log.d(TAG, "Adding member to lists: " + m);
                }

                dividingEquallyMembers = new boolean[members.size()];
                for (int i=0; i<members.size(); ++i)
                    dividingEquallyMembers[i] = false;

                CreateBillActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        setUpTables();

                        progressDialog.dismiss();
                    }
                });
            }
        }.start();
    }

    public void setUpTables() {
        // ** Who paid list **

        ListView whoPaidListView = (ListView) findViewById(R.id.listViewCreateBillWhoPaid);

        whoPaidArrayAdapter = new CustomTwoItemAdapter(this, whoPaid);

        whoPaidListView.setAdapter(whoPaidArrayAdapter);

        whoPaidArrayAdapter.notifyDataSetChanged();

        int totalHeight = 0;
        for (int i = 0; i < whoPaidArrayAdapter.getCount(); i++) {
            View listItem = whoPaidArrayAdapter.getView(i, null, whoPaidListView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = whoPaidListView.getLayoutParams();
        params.height = totalHeight + (whoPaidListView.getDividerHeight() * (whoPaidArrayAdapter.getCount() - 1));
        whoPaidListView.setLayoutParams(params);
        whoPaidListView.requestLayout();
        whoPaidListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "tapped whoPaidList on " + position);

                createUpdateWhoPaidDialog(position);
            }
        });

        // ** Who owns list **

        ListView whoOwnsListView = (ListView) findViewById(R.id.listViewCreateBillWhoOwns);

        whoOwnsArrayAdapter = new CustomTwoItemAdapter(this, whoOwns);

        whoOwnsListView.setAdapter(whoOwnsArrayAdapter);
        whoOwnsArrayAdapter.notifyDataSetChanged();

        totalHeight = 0;
        for (int i = 0; i < whoOwnsArrayAdapter.getCount(); i++) {
            View listItem = whoOwnsArrayAdapter.getView(i, null, whoOwnsListView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        params = whoOwnsListView.getLayoutParams();
        params.height = totalHeight + (whoOwnsListView.getDividerHeight() * (whoOwnsArrayAdapter.getCount() - 1));
        whoOwnsListView.setLayoutParams(params);
        whoOwnsListView.requestLayout();
        whoOwnsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "tapped whoOwnsList on " + position);

                WhoOwnsListViewOnItemClickListener(position);

            }
        });

        hideKeyboard();
    }

    void WhoOwnsListViewOnItemClickListener(int position) {
        if (dividingEquallyFlag) {
            dividingEquallyMembers[position] = !dividingEquallyMembers[position];
            updateWhoOwnsValues();
        }
        else {
            createUpdateWhoOwnsDialog(position);
        }
    }

    void updateWhoOwnsValues() {
        int totalPaying = 0;
        for (int i=0; i<whoOwns.size(); ++i)
            if (dividingEquallyMembers[i])
                totalPaying++;

        if (totalPaying != 0) {
            float billTotal = totalBillValue();
            float totalDivided = billTotal/totalPaying;

            for (int i=0; i<whoOwns.size(); ++i)
                if (dividingEquallyMembers[i])
                    whoOwns.get(i).second = String.format("%.2f", totalDivided);
                else
                    whoOwns.get(i).second = new String("0.00");
        }
        else {
            for (TwoStringsClass m : whoOwns)
                m.second = new String("0.00");
        }
        whoOwnsArrayAdapter.notifyDataSetChanged();
    }

    public float totalBillValue() {
        float total = 0;
        for (TwoStringsClass m : whoPaid)
            total += Float.parseFloat(m.second);

        Log.d(TAG, "Bill total: " + total);
        return total;
    }

    void createUpdateWhoPaidDialog (final int index) {
        Log.d(TAG, "createUpdateWhoPaidDialog");

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Edit");

        final EditText newValueInput = new EditText(this);
        newValueInput.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        newValueInput.setText(this.whoPaid.get(index).second);
        alert.setView(newValueInput);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String newValue = newValueInput.getText().toString();

                Log.d(TAG, "Ok to edit to " + newValue);

                whoPaid.get(index).second = String.format("%.2f", Float.parseFloat(newValue));
                whoPaidArrayAdapter.notifyDataSetChanged();
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

    void createUpdateWhoOwnsDialog (final int index) {
        Log.d(TAG, "createUpdateWhoOwnsDialog");

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Edit");

        final EditText newValueInput = new EditText(this);
        newValueInput.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        newValueInput.setText(this.whoOwns.get(index).second);
        alert.setView(newValueInput);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String newValue = newValueInput.getText().toString();

                Log.d(TAG, "Ok to edit to " + newValue);

                whoOwns.get(index).second = String.format("%.2f", Float.parseFloat(newValue));
                whoOwnsArrayAdapter.notifyDataSetChanged();
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

    public void hideKeyboard() {
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_bill, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        Log.d(TAG, "onCheckedChanged ");

        switch (i) {
            case R.id.radioButtonCreateBillEqually:
                dividingEquallyFlag = true;

                break;

            case R.id.radioButtonCreateBillCustom:
                dividingEquallyFlag = false;

                break;
        }

        for (TwoStringsClass m : whoOwns)
            m.second = new String("0.00");

        whoOwnsArrayAdapter.notifyDataSetChanged();
    }
}
