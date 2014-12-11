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
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * ViewBill activity:
 * - visualises a bill
 * - opens Maps if bill location is available
 * - opens image viewer if a bill billPicture is available
 * - option to edit the bill
 * - option to delete the bill
 */
public class ViewBillActivity extends Activity implements View.OnClickListener {
    static final String TAG = "ViewBillActivity";

    String billName;
    String groupName;
    String userName;

    Bill thisBill;

    ArrayList<TwoStringsClass> whoPaidTwoStringsList;
    ArrayList<TwoStringsClass> whoOwesTwoStringList;

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
            userName = extras.getString("user_name");
        }
        //TODO
        /*
        billName = "Bus tickets";
        groupName = "NYC trip";
        userName = "user1@test.com";
*/
        ImageButton mapButton = (ImageButton) findViewById(R.id.imageButtonViewBillMap);
        mapButton.setOnClickListener(this);

        ImageButton editButton = (ImageButton) findViewById(R.id.imageButtonViewBillEditBill);
        editButton.setOnClickListener(this);

        ImageButton deleteButton = (ImageButton) findViewById(R.id.imageButtonViewBillDelete);
        deleteButton.setOnClickListener(this);

        ImageView picture = (ImageView) findViewById(R.id.imageViewViewBillThumbnail);
        picture.setVisibility(View.INVISIBLE);

        progressDialog = new ProgressDialog(ViewBillActivity.this);
        progressDialog.setMessage(getResources().getString(R.string.warning_loading));
        progressDialog.setCancelable(false);

        fetchBillData();
    }

    /**
     * Downloads all the bill's data from the database.
     */
    public void fetchBillData() {
        progressDialog.show();

        new Thread() {
            public void run() {
                Log.d(TAG, "in thread fetchBillData");

                thisBill = ((ShareThatBillApp) getApplication()).dBhandler.getBill(billName);

                if (thisBill.billLocationLatitute != 0 && thisBill.billLocationLongitude != 0) {
                    Log.d(TAG, "bill location is valid");
                    thisBill.locationIsSet = true;

                    runOnUiThread(new Runnable() {
                        public void run() {
                            ImageView mapImage = (ImageView) findViewById(R.id.imageButtonViewBillMap);
                            mapImage.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.map_image));
                        }
                    });
                }
                else {
                    Log.d(TAG, "bill location is invalid");
                    thisBill.locationIsSet = false;
                }

                Log.d(TAG, "get who paid");
                whoPaidTwoStringsList = ((ShareThatBillApp) getApplication()).dBhandler.getWhoPaidBill(billName);

                Log.d(TAG, "get who owes");
                whoOwesTwoStringList = ((ShareThatBillApp) getApplication()).dBhandler.getWhoOwesBill(billName);

                runOnUiThread(new Runnable(){
                    public void run() {
                        setUpTables();
                    }
                });

                progressDialog.dismiss();
            }
        }.start();
/*
        // Download billPicture separately
        new Thread() {
            public void run() {
                Log.d(TAG, "in thread fetchBillData - billPicture");

                // get billPicture

                // remove progress bar
                runOnUiThread(new Runnable() {
                    public void run() {
                        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarViewBillPicture);
                        progressBar.setVisibility(View.INVISIBLE);

                        ImageView viewPicture = (ImageView) findViewById(R.id.imageViewViewBillThumbnail);
                        viewPicture.setOnClickListener(ViewBillActivity.this);
                        viewPicture.setVisibility(View.VISIBLE);

                        if (thisBill.billPicture != null) {
                            viewPicture.setImageBitmap(thisBill.billPicture);
                        }
                    }
                });

                // Save downloaded billPicture on card for visualization

            }
        }.start();
        */
    }

    /**
     * Sets up the listViews of who paid and who owes to the bill.
     */
    public void setUpTables() {
        Log.d(TAG, "setUpTables");

        TextView title = (TextView) findViewById(R.id.textViewViewBillTitle);
        title.setText(getResources().getString(R.string.view_bill_bill) + " " + thisBill.billName);

        TextView total = (TextView) findViewById(R.id.textViewViewBillTotal);
        total.setText(getResources().getString(R.string.view_bill_total) + String.format("%.2f", thisBill.billValue));

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


        // ** Who owes list **

        ListView whoOwesListView = (ListView) findViewById(R.id.listViewViewBillWhoOwes);

        CustomTwoItemAdapter whoOwesArrayAdapter = new CustomTwoItemAdapter(this, whoOwesTwoStringList);

        whoOwesListView.setAdapter(whoOwesArrayAdapter);
        whoOwesArrayAdapter.notifyDataSetChanged();

        totalHeight = 0;
        for (int i = 0; i < whoOwesArrayAdapter.getCount(); i++) {
            View listItem = whoOwesArrayAdapter.getView(i, null, whoOwesListView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        params = whoOwesListView.getLayoutParams();
        params.height = totalHeight + (whoOwesListView.getDividerHeight() * (whoOwesArrayAdapter.getCount() - 1));
        whoOwesListView.setLayoutParams(params);
        whoOwesListView.requestLayout();

        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollViewViewBill);
        scrollView.scrollTo(0, scrollView.getTop());
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick");

        Intent intent;

        switch (view.getId()) {
            case R.id.imageViewViewBillThumbnail:
/*
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("file://" + thisBill.billPicturePath),"image/jpg");
                startActivity(intent);
*/
                break;

            case R.id.imageButtonViewBillMap:

                if (thisBill.locationIsSet) {
                    String geoUri = "http://maps.google.com/maps?q=loc:" + thisBill.billLocationLatitute + "," + thisBill.billLocationLongitude +
                            " (" + thisBill.billName + ")";

                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                    startActivity(intent);
                }
                else
                    Toast.makeText(this, getResources().getText(R.string.view_bill_warning_no_location).toString(), Toast.LENGTH_LONG).show();
                break;

            case R.id.imageButtonViewBillEditBill:

                intent = new Intent(ViewBillActivity.this, CreateBillActivity.class);
                intent.putExtra("group_name", groupName);
                intent.putExtra("bill_name", billName);
                intent.putExtra("user_name", userName);
                Log.d(TAG, "sending userName " + userName);
                intent.putExtra("editing", true);
                startActivity(intent);

                finish();

                break;

            case R.id.imageButtonViewBillDelete:
                createDeleteBillAlert();

                break;
        }
    }

    /**
     * Creates an alertDialog to confirm the deletion of a bill.
     */
    private void createDeleteBillAlert () {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ViewBillActivity.this);

        alertDialogBuilder.setTitle(getResources().getString(R.string.view_bill_delete_bill));
        alertDialogBuilder.setMessage(getResources().getString(R.string.view_bill_delete_bill_question));

        alertDialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {

                progressDialog.show();
                new Thread() {
                    public void run() {
                        Log.d(TAG, "in thread deleteBill");

                        ((ShareThatBillApp) getApplication()).dBhandler.deleteBill(thisBill.billName, userName, groupName);

                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(ViewBillActivity.this, getResources().getString(R.string.view_bill_bill_delete), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });

                        finish();

                    }
                }.start();

            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
