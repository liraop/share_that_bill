package com.mobapp.almaslira.sharethatbill;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class CreateBillActivity extends Activity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {
    static final String TAG = "CreateBillActivity";
    private static DBhandler dbhandler = new DBhandler();

    String groupName;
    ArrayList<TwoStringsClass> whoPaid;
    ArrayList<TwoStringsClass> whoOwns;

    CustomTwoItemAdapter whoPaidArrayAdapter;
    CustomTwoItemAdapter whoOwnsArrayAdapter;

    boolean dividingEquallyFlag;
    boolean[] dividingEquallyMembers;

    Bill thisBill;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_create_bill);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            groupName = extras.getString("group_name");
        }

        progressDialog = new ProgressDialog(CreateBillActivity.this);
        progressDialog.setMessage(getResources().getString(R.string.warning_loading));
        progressDialog.setCancelable(false);

        dividingEquallyFlag = true;

        thisBill = new Bill();
        thisBill.billDate = Calendar.getInstance();

        RadioGroup  radioGroup = (RadioGroup) findViewById(R.id.RadioGroupCreateBillSplit);
        radioGroup.setOnCheckedChangeListener(this);

        Button setDateButton = (Button) findViewById(R.id.buttonCreateBillDate);
        setDateButton.setOnClickListener(this);

        Button setTimeButton = (Button) findViewById(R.id.buttonCreateBillTime);
        setTimeButton.setOnClickListener(this);

        Button createBill = (Button) findViewById(R.id.buttonCreateBillCreate);
        createBill.setOnClickListener(this);

        fetchMembersList();
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick");

        switch (view.getId()) {
            case R.id.buttonCreateBillDate:
                createGetDateDialog();
                break;

            case R.id.buttonCreateBillTime:
                createGetTimeDialog();
                break;

            case R.id.buttonCreateBillCreate:
                EditText billName = (EditText) findViewById(R.id.editTextCreateBillBillName);
                thisBill.billName = new String(billName.getText().toString());

                thisBill.billValue = totalBillValue();

                thisBill.groupName = new String(groupName);

                if (totalBillValue() == 0)
                    createWarningAlert(getResources().getString(R.string.warning_error),
                                        getResources().getString(R.string.warning_creating_bill_fail_total_zero));
                else if (!billValuesMatch())
                    createWarningAlert(getResources().getString(R.string.warning_error),
                            getResources().getString(R.string.warning_creating_bill_fail_total_match));
                else if (thisBill.billName == null)
                    createWarningAlert(getResources().getString(R.string.warning_error),
                            getResources().getString(R.string.warning_creating_bill_fail_name));
                else
                    sendCreateBillRequest();
                break;
        }
    }

    public boolean billValuesMatch() {
        float totalOwns = 0;
        for (TwoStringsClass m : whoOwns)
            totalOwns += Float.parseFloat(m.second);

        return ((totalOwns - totalBillValue()) == 0);
    }

    private void createWarningAlert (String title, String warning) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreateBillActivity.this);

        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(warning);

        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void sendCreateBillRequest() {
        Log.d(TAG, "sendCreateBillRequest");
        progressDialog.show();

        new Thread() {
            public void run() {
                dbhandler.createBill(thisBill);

                for (TwoStringsClass mp : whoPaid) {
                    if (Float.parseFloat(mp.second) > 0)
                        dbhandler.createUserBillRelation(mp.first, thisBill.billName, Float.parseFloat(mp.second));
                }
                for (TwoStringsClass mo : whoOwns) {
                    if (Float.parseFloat(mo.second) > 0)
                        dbhandler.createUserBillRelation(mo.first, thisBill.billName, -Float.parseFloat(mo.second));
                }

                CreateBillActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(CreateBillActivity.this, getResources().getText(R.string.create_bill_created), Toast.LENGTH_LONG).show();

                        progressDialog.dismiss();

                        finish();
                    }
                });
            }
        }.start();
    }

    public void createGetDateDialog () {

        View dialogView = View.inflate(this, R.layout.layout_date_picker, null);

        final NumberPicker month = (NumberPicker) dialogView.findViewById(R.id.numberPickerDatePickerFirst);
        month.setMinValue(1);
        month.setMaxValue(12);
        DateFormat dateFormat = new SimpleDateFormat("MM");
        month.setValue(Integer.parseInt(dateFormat.format(thisBill.billDate.getTime())));

        final NumberPicker day = (NumberPicker) dialogView.findViewById(R.id.numberPickerDatePickerSecond);
        day.setMinValue(1);
        day.setMaxValue(31);
        dateFormat = new SimpleDateFormat("dd");
        day.setValue(Integer.parseInt(dateFormat.format(thisBill.billDate.getTime())));

        final NumberPicker year = (NumberPicker) dialogView.findViewById(R.id.numberPickerDatePickerThird);
        year.setMinValue(1970);
        year.setMaxValue(2025);
        dateFormat = new SimpleDateFormat("yyyy");
        year.setValue(Integer.parseInt(dateFormat.format(thisBill.billDate.getTime())));


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.create_bill_set_date));
        builder.setMessage(getResources().getString(R.string.create_bill_set_date_format))
                .setView(dialogView)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d(TAG,  "month:" + month.getValue() +
                                    " day: " + day.getValue() +
                                    " year: " + year.getValue());

                        thisBill.billDate.set(year.getValue(), month.getValue(), day.getValue());

                        updateDateTime();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).show();
    }

    public void createGetTimeDialog () {

        View dialogView = View.inflate(this, R.layout.layout_time_picker, null);

        final TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.timePicker);

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH");
        timePicker.setCurrentHour(Integer.parseInt(dateFormat.format(thisBill.billDate.getTime())));

        dateFormat = new SimpleDateFormat("mm");
        timePicker.setCurrentMinute(Integer.parseInt(dateFormat.format(thisBill.billDate.getTime())));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.create_bill_set_time))
                .setView(dialogView)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
                        int year = Integer.parseInt(dateFormat.format(thisBill.billDate.getTime()));
                        dateFormat = new SimpleDateFormat("MM");
                        int month = Integer.parseInt(dateFormat.format(thisBill.billDate.getTime()));
                        dateFormat = new SimpleDateFormat("dd");
                        int day = Integer.parseInt(dateFormat.format(thisBill.billDate.getTime()));

                        thisBill.billDate.set(year, month, day, timePicker.getCurrentHour(), timePicker.getCurrentMinute());

                        updateDateTime();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).show();
    }

    public void fetchMembersList() {
        Log.d(TAG, "fetchMembersList");

        progressDialog.show();

        new Thread() {
            public void run() {
                ArrayList<String> members = dbhandler.getGroupMembers(groupName);

                whoPaid = new ArrayList<TwoStringsClass>();
                whoOwns = new ArrayList<TwoStringsClass>();

                for (String m : members) {
                    whoPaid.add(new TwoStringsClass(m, "0.00", "$"));
                    whoOwns.add(new TwoStringsClass(m, "0.00", "$"));
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
        updateDateTime();

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
    }

    private void updateDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        TextView date = (TextView) findViewById(R.id.textViewCreateBillDate);
        date.setText(getResources().getText(R.string.create_bill_date) + " " + dateFormat.format(thisBill.billDate.getTime()));

        dateFormat = new SimpleDateFormat("hh:mm a");

        TextView time = (TextView) findViewById(R.id.textViewCreateBillTime);
        time.setText(getResources().getText(R.string.create_bill_time) + "  " + dateFormat.format(thisBill.billDate.getTime()));
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
