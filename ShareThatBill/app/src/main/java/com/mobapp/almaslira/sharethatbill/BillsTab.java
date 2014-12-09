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
 * BillsTab activity:
 * - shows all group's bills
 * - create bill option
 */
public class BillsTab extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
    static final String TAG = "BillsTab";
    private static DBhandler dbhandler = new DBhandler();

    String thisUserName;
    String thisGroupName;
    ProgressDialog progressDialog;

    private ListView billsList;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> billsNamesList;

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

        progressDialog = new ProgressDialog(BillsTab.this);
        progressDialog.setMessage(getResources().getString(R.string.warning_loading));
        progressDialog.setCancelable(false);

        ImageButton addButton = (ImageButton) findViewById(R.id.imageButtonTabsAdd);
        addButton.setOnClickListener(this);

        TextView addText = (TextView) findViewById(R.id.textViewGroupTabAdd);
        addText.setText(getResources().getString(R.string.tabs_bills_add));

        billsList = (ListView) findViewById(R.id.listViewTabsList);

        billsNamesList = new ArrayList<String>();

        arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                billsNamesList);

        billsList.setAdapter(arrayAdapter);

        arrayAdapter.notifyDataSetChanged();
        billsList.setOnItemClickListener(this);

        updateBills();

    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick");

        switch (v.getId()) {
            case R.id.imageButtonTabsAdd:
                Log.d(TAG, "add button");

                Intent intent = new Intent(BillsTab.this, CreateBillActivity.class);
                intent.putExtra("group_name", thisGroupName);
                intent.putExtra("user_name", thisUserName);
                startActivity(intent);

                break;
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemClick");

        Intent intent = new Intent(BillsTab.this, ViewBillActivity.class);
        intent.putExtra("bill_name", billsNamesList.get(position));
        intent.putExtra("group_name", thisGroupName);
        intent.putExtra("user_name", thisUserName);
        startActivity(intent);
    }

    /**
     * Download all bills of the group.
     */
    void updateBills() {
        progressDialog.show();

        new Thread() {
            public void run() {
                Log.d(TAG, "in thread updateBills");

                billsNamesList = dbhandler.getGroupBills(thisGroupName);

                progressDialog.dismiss();

                runOnUiThread(new Runnable(){
                    public void run() {
                        updateList();

                        progressDialog.dismiss();
                    }
                });
            }
        }.start();
    }

    /**
     * Updates bills listView.
     */
    void updateList() {
        arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                billsNamesList);

        billsList.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume");
        updateBills();
    }
}
