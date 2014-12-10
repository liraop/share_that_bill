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
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
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

/**
 * CreateBill activity:
 * - interface to create a new bill OR
 * - interface to edit a bill
 *
 * To change its behave accordingly to which task above it is performing, this Activity uses
 * the boolean editingBill throughout its execution.
 */
public class CreateBillActivity extends Activity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener, LocationListener {
    static final String TAG = "CreateBillActivity";

    String groupName;
    ArrayList<TwoStringsClass> whoPaidTwoStringsList;
    ArrayList<TwoStringsClass> whoOwesTwoStringsList;

    CustomTwoItemAdapter whoPaidArrayAdapter;
    CustomTwoItemAdapter whoOwesArrayAdapter;

    boolean dividingEquallyFlag;
    boolean[] dividingEquallyMembers;

    Bill thisBill;

    boolean getLocation;

    boolean hasPicture;
    String picturePath;
    boolean downloadPicture; // in case the bill has a billPicture and it could not be downloaded by ViewBill
    boolean pictureReady;
    ProgressDialog progressDialog;

    boolean editingBill;
    String billOriginalName;
    String userName;

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

        pictureReady = false;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            groupName = extras.getString("group_name");
            editingBill = extras.getBoolean("editing");
            userName = extras.getString("user_name");

            hasPicture = extras.getBoolean("has_picture");

            if (editingBill) {
                thisBill.billName = extras.getString("bill_name");
                billOriginalName = thisBill.billName;

                if (hasPicture)
                    picturePath = extras.getString("picture_path");
                else
                    picturePath = null;

                if (picturePath != null) {
                    pictureReady = true;
                }
                else {
                    if (hasPicture)
                        downloadPicture = true;
                    else
                        downloadPicture = false;
                }
                Log.d(TAG, "editing bill " + billOriginalName + " from group " + groupName);
            }
        }
/*
        //TODO
        groupName ="House bills";
        editingBill = false;
        userName = "user1@test.com";
        hasPicture = true;
        picturePath = null;//"/storage/emulated/0/Pictures/JPEG__bill_20141210_021949-1430263900.jpg";
        downloadPicture = true;
        pictureReady = false;
        */

        progressDialog = new ProgressDialog(CreateBillActivity.this);
        progressDialog.setMessage(getResources().getString(R.string.warning_loading));
        progressDialog.setCancelable(false);

        if (!editingBill) {
            thisBill.billDate = Calendar.getInstance();
        }

        dividingEquallyFlag = true;

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.RadioGroupCreateBillSplit);
        if (editingBill) {
            radioGroup.setVisibility(View.GONE);
        }
        else {
            radioGroup.setOnCheckedChangeListener(this);
        }

        Button setDateButton = (Button) findViewById(R.id.buttonCreateBillDate);
        setDateButton.setOnClickListener(this);

        Button setTimeButton = (Button) findViewById(R.id.buttonCreateBillTime);
        setTimeButton.setOnClickListener(this);

        Button createBill = (Button) findViewById(R.id.buttonCreateBillCreate);
        createBill.setOnClickListener(this);
        if (editingBill)
            createBill.setText(getResources().getText(R.string.create_bill_save));

        Button takePicture = (Button) findViewById(R.id.buttonCreateBillPicture);
        takePicture.setOnClickListener(this);

        Button getLocationButton = (Button) findViewById(R.id.buttonCreateBillLocation);
        getLocationButton.setOnClickListener(this);

        ImageView mapImage = (ImageView) findViewById(R.id.imageViewCreateBillMap);
        mapImage.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.map_image_faded));
        mapImage.setOnClickListener(this);

        ImageView pictureThumbnail = (ImageView) findViewById(R.id.imageViewCreateBillThumbnail);
        pictureThumbnail.setOnClickListener(this);

        if (editingBill) {
            EditText billNameEditText = (EditText) findViewById(R.id.editTextCreateBillBillName);
            billNameEditText.setText(thisBill.billName);

            TextView whoPaidText = (TextView) findViewById(R.id.textViewCreateBillWhoPaid);
            whoPaidText.setText(getResources().getString(R.string.view_bill_who_paid));

            TextView whoOwesText = (TextView) findViewById(R.id.textViewCreateBillHowToSplit);
            whoOwesText.setText(getResources().getString(R.string.view_bill_who_owes));
        }

        if (downloadPicture) {
            ProgressBar imageProgressBar = (ProgressBar) findViewById(R.id.progressBarCreateBillPicture);
            imageProgressBar.setVisibility(View.VISIBLE);
        }

        ImageButton deleteLocation = (ImageButton) findViewById(R.id.imageButtonCreateBillDeleteLocation);
        deleteLocation.setOnClickListener(this);
        deleteLocation.setVisibility(View.INVISIBLE);

        ImageButton deletePicture = (ImageButton) findViewById(R.id.imageButtonCreateBillDeletePicture);
        deletePicture.setOnClickListener(this);
        deletePicture.setVisibility(View.INVISIBLE);

        getLocation = false;

        locationManagerGPS = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManagerGPS.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        locationManagerNetwork = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManagerNetwork.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

        fetchBillInformation();
    }

    /**
     * This method is called when the device location changes and is related to the locationManagers.
     */
    @Override
    public void onLocationChanged(Location location) {

        if (getLocation) {
            Log.d(TAG, "getting location");

            thisBill.billLocationLatitute = (float) location.getLatitude();
            thisBill.billLocationLongitude = (float) location.getLongitude();
            thisBill.locationIsSet = true;

            getLocation = false;

            ImageView mapImage = (ImageView) findViewById(R.id.imageViewCreateBillMap);
            mapImage.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.map_image));
            mapImage.setOnClickListener(this);

            ImageButton deleteLocation = (ImageButton) findViewById(R.id.imageButtonCreateBillDeleteLocation);
            deleteLocation.setVisibility(View.VISIBLE);

            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarCreateBillLocation);
            progressBar.setVisibility(View.INVISIBLE);
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
                Log.d(TAG, "take billPicture button");
                takePicture();
                break;

            case R.id.imageViewCreateBillThumbnail:
                Log.d(TAG, "touched image");

                if (pictureReady) {
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse("file://" + picturePath), "image/jpg");
                    startActivity(intent);
                }
                else
                    Toast.makeText(this, getResources().getString(R.string.create_bill_warning_picture_unavailable), Toast.LENGTH_SHORT).show();
                break;

            case R.id.buttonCreateBillLocation:
                Log.d(TAG, "getLocationButton");

                if (locationServicesEnabled()) {
                    Log.d(TAG, "location services are enabled");

                    ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarCreateBillLocation);
                    progressBar.setVisibility(View.VISIBLE);

                    Toast.makeText(this, getResources().getString(R.string.create_bill_getting_location), Toast.LENGTH_SHORT).show();

                    getLocation = true;
                }
                else {
                    Log.d(TAG, "location services are disabled");

                    createLocationWarningDialog();
                }
                break;

            case R.id.imageViewCreateBillMap:

                if (thisBill.locationIsSet) {
                    String geoUri = "http://maps.google.com/maps?q=loc:" + thisBill.billLocationLatitute + "," + thisBill.billLocationLongitude;

                    EditText name = (EditText) findViewById(R.id.editTextCreateBillBillName);
                    if ( name.getText().toString().length() > 0)
                        geoUri += " (" + name.getText().toString() + ")";

                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                    startActivity(intent);
                }
                break;

            case R.id.imageButtonCreateBillDeleteLocation:

                thisBill.billLocationLatitute = 0.0f;
                thisBill.billLocationLongitude = 0.0f;
                thisBill.locationIsSet = false;

                ImageButton mapImage = (ImageButton) findViewById(R.id.imageViewCreateBillMap);
                mapImage.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.map_image_faded));

                ImageButton deleteLocation = (ImageButton) findViewById(R.id.imageButtonCreateBillDeleteLocation);
                deleteLocation.setVisibility(View.GONE);
                break;

            case R.id.imageButtonCreateBillDeletePicture:

                thisBill.billPicture = null;
                picturePath = null;
                pictureReady = false;

                break;
        }
    }

    /**
     * Creates an alertDialog warning the user that the device location service is turned off
     * and offers the option to open the location settings menu.
     */
    private void createLocationWarningDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreateBillActivity.this);

        alertDialogBuilder.setTitle(getResources().getString(R.string.warning_error));
        alertDialogBuilder.setMessage(getResources().getString(R.string.create_bill_warning_location_disabled));

        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                Intent gpsOptionsIntent = new Intent(
                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(gpsOptionsIntent);
            }
        });

        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * Checks if any location service is enabled on the device.
     */
    private boolean locationServicesEnabled() {
        boolean gpsEnabled = false, networkEnabled = false;
        try {
            gpsEnabled = locationManagerGPS.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex){}
        try{
            networkEnabled = locationManagerNetwork.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex){}

        return (gpsEnabled || networkEnabled);
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    /**
     * Sends an intent to capture a billPicture.
     */
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

    /**
     * Creates a File for the billPicture to be stored.
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + "_bill_" + timeStamp;
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        picturePath =/* "file:" + */ image.getAbsolutePath();
        Log.d(TAG, "image save path created: " + picturePath);
        return image;

    }

    /**
     * Called when the camera activity finishes. If the task was successful, it sets the billPicture's
     * thumbnail in the interface's ImageView.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "returned from camera");
        File f = new File(picturePath);

        if(f.exists() && !f.isDirectory())
            Log.d(TAG, "image exists, size" + f.length());
        else
            Log.d(TAG, "image does not exist");

        Log.d(TAG, "result ok: " + (resultCode == RESULT_OK));

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            thisBill.billPicture = BitmapFactory.decodeFile(picturePath);

            if (thisBill.billPicture == null)
                Log.d(TAG, "bill billPicture null");
            else
                Log.d(TAG, "bill billPicture not null");

            ImageButton deleteLocation = (ImageButton) findViewById(R.id.imageButtonCreateBillDeletePicture);
            deleteLocation.setVisibility(View.VISIBLE);

            pictureReady = true;
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, thisBill.billName, null);
        return Uri.parse(path);
    }

    /**
     * Returns if the total value paid in the bill matches the total value owed.
     */
    public boolean billValuesMatch() {
        float totalOwes = 0;
        for (TwoStringsClass m : whoOwesTwoStringsList)
            totalOwes += Float.parseFloat(m.second);

        Log.d(TAG, "totalOwes: " + totalOwes);
        Log.d(TAG, "totalBillValue: " + totalBillValue());

        return ( Math.abs(totalOwes - totalBillValue()) < 0.02f);
    }

    /**
     * Create a generic alertDialog with an OK button.
     * @param title: title of the dialog
     * @param warning: message of the dialog
     */
    private void createWarningAlert (String title, String warning) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreateBillActivity.this);

        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(warning);

        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * Send request to save the bill on the database.
     */
    public void sendCreateBillRequest() {
        Log.d(TAG, "sendCreateBillRequest");
        progressDialog.show();

        new Thread() {
            public void run() {

                if (editingBill)
                    ((ShareThatBillApp) getApplication()).dBhandler.editBill(thisBill, billOriginalName, userName);
                else
                    ((ShareThatBillApp) getApplication()).dBhandler.createBill(thisBill, userName);

                ((ShareThatBillApp) getApplication()).dBhandler.addPictureToBill(thisBill);
                Log.d(TAG, "creating bill");

                for (int i = 0; i < whoPaidTwoStringsList.size(); i++) {
                    if (Float.parseFloat(whoPaidTwoStringsList.get(i).second) > 0 || Float.parseFloat(whoOwesTwoStringsList.get(i).second) > 0) {
                        Log.d(TAG, "user " + whoPaidTwoStringsList.get(i).first +
                                " owes " + Float.parseFloat(whoOwesTwoStringsList.get(i).second) +
                                " and paid " + Float.parseFloat(whoPaidTwoStringsList.get(i).second));

                        ((ShareThatBillApp) getApplication()).dBhandler.createUserBillRelation(whoPaidTwoStringsList.get(i).first, thisBill.billName,
                                Float.parseFloat(whoOwesTwoStringsList.get(i).second),
                                Float.parseFloat(whoPaidTwoStringsList.get(i).second));

                    }
                }

                Log.d(TAG, "saving picture");
                ((ShareThatBillApp) getApplication()).dBhandler.addPictureToBill(thisBill);

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

    /**
     * Creates a dialog with a custom datePicker dialog to get the bill's date.
     */
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
                .setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d(TAG,  "month:" + month.getValue() +
                                    " day: " + day.getValue() +
                                    " year: " + year.getValue());

                        thisBill.billDate.set(year.getValue(), month.getValue(), day.getValue());

                        updateDateTime();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).show();
    }

    /**
     * Create a dialog with a timePicker to get the bill's time.
     */
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
                .setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
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
                .setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).show();
    }

    /**
     * Downloads bill information.
     */
    public void fetchBillInformation() {
        Log.d(TAG, "fetchBillInformation");

        progressDialog.show();

        new Thread() {
            public void run() {
                ArrayList<String> members = ((ShareThatBillApp) getApplication()).dBhandler.getGroupMembers(groupName);

                whoPaidTwoStringsList = new ArrayList<TwoStringsClass>();
                whoOwesTwoStringsList = new ArrayList<TwoStringsClass>();

                for (String m : members) {
                    whoPaidTwoStringsList.add(new TwoStringsClass(m, "0.00"));
                    whoOwesTwoStringsList.add(new TwoStringsClass(m, "0.00"));
                    Log.d(TAG, "Adding member to lists: " + m);
                }

                if (editingBill) {
                    thisBill = ((ShareThatBillApp) getApplication()).dBhandler.getBill(thisBill.billName);

                    Log.d(TAG, "get who paid");
                    ArrayList<TwoStringsClass> whoPaidTemp = ((ShareThatBillApp) getApplication()).dBhandler.getWhoPaidBill(thisBill.billName);

                    for (int i = 0; i < members.size(); ++i) {
                        for (int j = 0; j < whoPaidTemp.size(); ++j) {
                            if (members.get(i).compareTo(whoPaidTemp.get(j).first) == 0)
                                whoPaidTwoStringsList.get(i).second = whoPaidTemp.get(j).second;
                        }
                    }

                    Log.d(TAG, "get who owes");
                    ArrayList<TwoStringsClass> whoOweTemp = ((ShareThatBillApp) getApplication()).dBhandler.getWhoOwesBill(thisBill.billName);

                    for (int i = 0; i < members.size(); ++i) {
                        for (int j = 0; j < whoOweTemp.size(); ++j) {
                            if (members.get(i).compareTo(whoOweTemp.get(j).first) == 0)
                                whoOwesTwoStringsList.get(i).second = whoOweTemp.get(j).second;
                        }
                    }

                    if (thisBill.billLocationLatitute != 0 && thisBill.billLocationLongitude != 0) {
                        Log.d(TAG, "bill location is valid");
                        thisBill.locationIsSet = true;



                        CreateBillActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                ImageView mapImage = (ImageView) findViewById(R.id.imageViewCreateBillMap);
                                mapImage.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.map_image));

                                ImageButton deleteLocation = (ImageButton) findViewById(R.id.imageButtonCreateBillDeleteLocation);
                                deleteLocation.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                    else {
                        Log.d(TAG, "bill location is invalid");
                        thisBill.locationIsSet = false;

                        ImageButton deleteLocation = (ImageButton) findViewById(R.id.imageButtonCreateBillDeleteLocation);
                        deleteLocation.setVisibility(View.GONE);
                    }
                }

                if (!editingBill) {
                    dividingEquallyMembers = new boolean[members.size()];
                    for (int i = 0; i < members.size(); ++i)
                        dividingEquallyMembers[i] = false;
                }

                CreateBillActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        setUpTables();

                        progressDialog.dismiss();
                    }
                });
            }
        }.start();

        // Download billPicture in another thread
        if (downloadPicture) {
            new Thread() {
                public void run() {
                    Log.d(TAG, "fetching picture");
                    //TODO
                    thisBill.billPicture = ((ShareThatBillApp) getApplication()).dBhandler.getBillPicture(thisBill);
                    try {
                        Thread.sleep(10000);
                    } catch(Exception e) {}

                    CreateBillActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            //TODO change image
                            ImageView pictureThumbnail = (ImageView) findViewById(R.id.imageViewCreateBillThumbnail);

                            ProgressBar imageProgressBar = (ProgressBar) findViewById(R.id.progressBarCreateBillPicture);
                            imageProgressBar.setVisibility(View.INVISIBLE);
                        }
                    });

                    pictureReady = true;
                    Log.d(TAG, "picture ready");
                }
            }.start();
        }
    }

    /**
     * Sets up the listViews to get who paid and who owes to the bill.
     */
    public void setUpTables() {
        updateDateTime();

        // ** Who paid list **

        ListView whoPaidListView = (ListView) findViewById(R.id.listViewCreateBillWhoPaid);

        whoPaidArrayAdapter = new CustomTwoItemAdapter(this, whoPaidTwoStringsList);

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

        // ** Who owes list **

        ListView whoOwesListView = (ListView) findViewById(R.id.listViewCreateBillWhoOwes);

        whoOwesArrayAdapter = new CustomTwoItemAdapter(this, whoOwesTwoStringsList);

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
        whoOwesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "tapped whoOwesList on " + position);

                WhoOwesListViewOnItemClickListener(position);
            }
        });
    }

    /**
     * Updates the date and time text fields according to user input.
     */
    private void updateDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        TextView date = (TextView) findViewById(R.id.textViewCreateBillDate);
        date.setText(getResources().getText(R.string.create_bill_date) + " " + dateFormat.format(thisBill.billDate.getTime()));

        dateFormat = new SimpleDateFormat("hh:mm a");

        TextView time = (TextView) findViewById(R.id.textViewCreateBillTime);
        time.setText(getResources().getText(R.string.create_bill_time) + "  " + dateFormat.format(thisBill.billDate.getTime()));
    }

    /**
     * Method to handle onItemClick of the WhoOwes listView. If the "divide bill equally" option is
     * selected, it will take the bill's total amount and split between the selected users. If not,
     * it will prompt the user for the amount the clicked user owes.
     */
    void WhoOwesListViewOnItemClickListener(int position) {

        if (dividingEquallyFlag && !editingBill) {
            dividingEquallyMembers[position] = !dividingEquallyMembers[position];
            updateWhoOwesValues();
        }
        else {
            createUpdateWhoOwesDialog(position);
        }
    }

    /**
     * Calculates how much each selected members owes. This method is only called when the bill is
     * being split equally.
     */
    void updateWhoOwesValues() {
        int totalPaying = 0;
        for (int i=0; i < whoOwesTwoStringsList.size(); ++i)
            if (dividingEquallyMembers[i])
                totalPaying++;

        if (totalPaying != 0) {
            float billTotal = totalBillValue();
            float totalDivided = billTotal/totalPaying;

            for (int i=0; i < whoOwesTwoStringsList.size(); ++i)
                if (dividingEquallyMembers[i])
                    whoOwesTwoStringsList.get(i).second = String.format("%.2f", totalDivided);
                else
                    whoOwesTwoStringsList.get(i).second = new String("0.00");
        }
        else {
            for (TwoStringsClass m : whoOwesTwoStringsList)
                m.second = new String("0.00");
        }
        whoOwesArrayAdapter.notifyDataSetChanged();
    }

    /**
     * @return: the total paid value of the bill
     */
    public float totalBillValue() {
        float total = 0;
        for (TwoStringsClass m : whoPaidTwoStringsList)
            total += Float.parseFloat(m.second);

        return total;
    }

    /**
     * Creates a dialog to get the amount paid by the clicked user.
     */
    void createUpdateWhoPaidDialog (final int index) {
        Log.d(TAG, "createUpdateWhoPaidDialog");

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Edit");

        final EditText newValueInput = new EditText(this);
        newValueInput.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        newValueInput.setText(this.whoPaidTwoStringsList.get(index).second);
        alert.setView(newValueInput);

        alert.setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String newValue = newValueInput.getText().toString();

                if (newValue.length() == 0)
                    newValue = "0.00";

                Log.d(TAG, "Ok to edit to " + newValue);

                whoPaidTwoStringsList.get(index).second = String.format("%.2f", Float.parseFloat(newValue));
                whoPaidArrayAdapter.notifyDataSetChanged();
            }
        });

        alert.setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Log.d(TAG, "alert cancelled");
            }
        });

        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }

    /**
     * Creates a dialog to get the amount owed by the clicked user.
     */
    void createUpdateWhoOwesDialog (final int index) {
        Log.d(TAG, "createUpdateWhoOwesDialog");

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Edit");

        final EditText newValueInput = new EditText(this);
        newValueInput.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        newValueInput.setText(this.whoOwesTwoStringsList.get(index).second);
        alert.setView(newValueInput);

        alert.setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String newValue = newValueInput.getText().toString();

                Log.d(TAG, "Ok to edit to " + newValue);

                whoOwesTwoStringsList.get(index).second = String.format("%.2f", Float.parseFloat(newValue));
                whoOwesArrayAdapter.notifyDataSetChanged();
            }
        });

        alert.setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Log.d(TAG, "alert cancelled");
            }
        });


        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }
/*
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
*/
    /**
     * Handles change of selection on the RadioGroup.
     */
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

        for (TwoStringsClass m : whoOwesTwoStringsList)
            m.second = new String("0.00");

        whoOwesArrayAdapter.notifyDataSetChanged();
    }
}
