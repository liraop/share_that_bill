package com.mobapp.almaslira.sharethatbill;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;


public class CreateBillActivity extends Activity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener, LocationListener {
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

    boolean getLocation;

    ProgressDialog progressDialog;

    boolean editingBill;

    protected LocationManager locationManagerGPS;
    protected LocationManager locationManagerNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_create_bill);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        thisBill = new Bill();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            groupName = extras.getString("group_name");
            editingBill = extras.getBoolean("editing");

            if (editingBill) {
                thisBill.billName = extras.getString("bill_name");

                Log.d(TAG, "editing bill" + thisBill.billName + " from group " + groupName);
            }
        }

        progressDialog = new ProgressDialog(CreateBillActivity.this);
        progressDialog.setMessage(getResources().getString(R.string.warning_loading));
        progressDialog.setCancelable(false);

        if (!editingBill) {
            thisBill.billDate = Calendar.getInstance();
            dividingEquallyFlag = true;
        }

        RadioGroup  radioGroup = (RadioGroup) findViewById(R.id.RadioGroupCreateBillSplit);
        radioGroup.setOnCheckedChangeListener(this);

        Button setDateButton = (Button) findViewById(R.id.buttonCreateBillDate);
        setDateButton.setOnClickListener(this);

        Button setTimeButton = (Button) findViewById(R.id.buttonCreateBillTime);
        setTimeButton.setOnClickListener(this);

        Button createBill = (Button) findViewById(R.id.buttonCreateBillCreate);
        createBill.setOnClickListener(this);

        Button takePicture = (Button) findViewById(R.id.buttonCreateBillPicture);
        takePicture.setOnClickListener(this);

        Button getLocationButton = (Button) findViewById(R.id.buttonCreateBillLocation);
        getLocationButton.setOnClickListener(this);

        ImageView mapImage = (ImageView) findViewById(R.id.imageViewCreateBillMap);
        mapImage.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.map_image_faded));
        mapImage.setOnClickListener(this);

        getLocation = false;

        locationManagerGPS = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManagerGPS.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        locationManagerNetwork = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManagerNetwork.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

        fetchMembersList();
    }

    @Override
    public void onLocationChanged(Location location) {

        Log.d(TAG,"Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude() + ", Accuracy: "  + location.getAccuracy());

        if (getLocation == true) {
            Log.d(TAG, "getting location");

            thisBill.billLocation = location;
            getLocation = false;

            ImageView mapImage = (ImageView) findViewById(R.id.imageViewCreateBillMap);
            mapImage.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.map_image));
            mapImage.setOnClickListener(this);
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick");

        Intent intent;

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
                else if (thisBill.billName.length() == 0)
                    createWarningAlert(getResources().getString(R.string.warning_error),
                            getResources().getString(R.string.warning_creating_bill_fail_name));
                else
                    sendCreateBillRequest();
                break;

            case R.id.buttonCreateBillPicture:
                Log.d(TAG, "take picture button");
                takePicture();
                break;

            case R.id.imageViewCreateBillThumbnail:
                Log.d(TAG, "touched image");

                intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("file://" + thisBill.billPicturePath),"image/jpg");
                startActivity(intent);

                break;

            case R.id.buttonCreateBillLocation:
                Log.d(TAG, "getLocationButton");
                getLocation = true;
                break;

            case R.id.imageViewCreateBillMap:

                if (thisBill.billLocation != null) {
                    String geoUri = "http://maps.google.com/maps?q=loc:" + thisBill.billLocation.getLatitude() + "," + thisBill.billLocation.getLongitude();

                    EditText name = (EditText) findViewById(R.id.editTextCreateBillBillName);
                    if ( name.getText().toString().length() > 0)
                        geoUri += " (" + name.getText().toString() + ")";

                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                    startActivity(intent);
                }
                break;
        }
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void takePicture() {
        Log.d(TAG, "calling camera");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                Log.e(TAG, "exception on createImageFile", e);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                Log.d(TAG, "started camera activity");
            }
            else
                Log.d(TAG, "error creating image file");
        }
        else
            Log.d(TAG, "error checking camera activity");
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + groupName + "_bill_" + timeStamp;
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        thisBill.billPicturePath = /*"file:" + */image.getAbsolutePath();
        Log.d(TAG, "image save path created: " + thisBill.billPicturePath);
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "returned from camera");
        File f = new File(thisBill.billPicturePath);
        if(f.exists() && !f.isDirectory())
            Log.d(TAG, "image exists");
        else
            Log.d(TAG, "image does not exist");

        Log.d(TAG, "result ok: " + (resultCode == RESULT_OK));

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            ImageView mImageView = (ImageView) findViewById(R.id.imageViewCreateBillThumbnail);
            mImageView.setImageBitmap(BitmapFactory.decodeFile(thisBill.billPicturePath));
            mImageView.setOnClickListener(this);
        }
/*

            Log.d(TAG, "result ok");
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            Log.d(TAG, "passou");


        }
        */
    }

    /**
     * http://developer.android.com/training/camera/photobasics.html
     */
    public Bitmap compressBitmap (String photoPath, int targetW, int targetH) {

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(photoPath, bmOptions);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, thisBill.billName, null);
        return Uri.parse(path);
    }

    public boolean billValuesMatch() {
        float totalOwns = 0;
        for (TwoStringsClass m : whoOwns)
            totalOwns += Float.parseFloat(m.second);

        Log.d(TAG, "totalOwns: " + totalOwns);
        Log.d(TAG, "totalBillValue: " + totalBillValue());

        return ( Math.abs(totalOwns - totalBillValue()) < 0.02f);
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

                Log.d(TAG, "creating bill");

                for (int i = 0; i < whoPaid.size(); i++) {
                    if (Float.parseFloat(whoPaid.get(i).second) > 0 || Float.parseFloat(whoOwns.get(i).second) > 0) {
                        Log.d(TAG, "user " + whoPaid.get(i).first +
                                " owns " + Float.parseFloat(whoOwns.get(i).second) +
                                " and paid " + Float.parseFloat(whoPaid.get(i).second));

                        dbhandler.createUserBillRelation(whoPaid.get(i).first, thisBill.billName,
                                Float.parseFloat(whoOwns.get(i).second),
                                Float.parseFloat(whoPaid.get(i).second));

                    }
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
        for (int i=0; i < whoOwns.size(); ++i)
            if (dividingEquallyMembers[i])
                totalPaying++;

        if (totalPaying != 0) {
            float billTotal = totalBillValue();
            float totalDivided = billTotal/totalPaying;

            for (int i=0; i < whoOwns.size(); ++i)
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
