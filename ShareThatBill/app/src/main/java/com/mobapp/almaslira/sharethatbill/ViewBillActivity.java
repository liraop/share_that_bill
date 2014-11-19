package com.mobapp.almaslira.sharethatbill;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Picture;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ViewBillActivity extends Activity {
    static final String TAG = "ViewBillActivity";

    String billName;
    String groupName;

    Bill thisBill;

    ArrayList<TwoStringsClass> whoPaidTwoStringsList;
    ArrayList<TwoStringsClass> whoOwnsStringList;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_view_bill);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            billName = extras.getString("bill_name");
            groupName = extras.getString("group_name");
        }

        progressDialog = new ProgressDialog(ViewBillActivity.this);
        progressDialog.setMessage(getResources().getString(R.string.warning_loading));
        progressDialog.setCancelable(false);

        fetchBillData();
    }

    public void fetchBillData() {
        progressDialog.show();

        new Thread() {
            public void run() {
                Log.d(TAG, "in thread fetchBillData");

                thisBill = ((ShareThatBillApp) getApplication()).dataBase.getBill(billName);
                whoPaidTwoStringsList = ((ShareThatBillApp) getApplication()).dataBase.getWhoPaidBill(billName);
                whoOwnsStringList = ((ShareThatBillApp) getApplication()).dataBase.getWhoOwnsBill(billName);

                runOnUiThread(new Runnable(){
                    public void run() {
                        setUpTables();
                    }
                });

                progressDialog.dismiss();
            }
        }.start();
    }

    public void setUpTables() {
        Log.d(TAG, "setUpTables");

        TextView title = (TextView) findViewById(R.id.textViewViewBillTitle);
        title.setText(getResources().getString(R.string.view_bill_bill) + " " + thisBill.billName);

        TextView total = (TextView) findViewById(R.id.textViewViewBillTotal);
        total.setText(getResources().getString(R.string.view_bill_total) + String.format("%2.2f", thisBill.billValue));

        TextView date = (TextView) findViewById(R.id.textViewViewBillDate);
        date.setText(getResources().getString(R.string.view_bill_date) + " " + (new SimpleDateFormat("MM-dd-yyyy @ hh:mm a").format(thisBill.billDate.getTime())));

        TextView location = (TextView) findViewById(R.id.textViewViewBillLocation);
        location.setText(getResources().getString(R.string.view_bill_location));


        // ** Who paid list **

        ListView whoPaidListView = (ListView) findViewById(R.id.listViewViewBillWhoPaid);

        CustomTwoItemAdapter whoPaidArrayAdapter = new CustomTwoItemAdapter(this, whoPaidTwoStringsList);

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


        // ** Who owns list **

        ListView whoOwnsListView = (ListView) findViewById(R.id.listViewViewBillWhoOwns);

        CustomTwoItemAdapter whoOwnsArrayAdapter = new CustomTwoItemAdapter(this, whoOwnsStringList);

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

        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollViewViewBill);
        scrollView.scrollTo(0, scrollView.getTop());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_bill, menu);
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
}
