package com.mobapp.almaslira.sharethatbill;

import android.graphics.Picture;
import android.location.Location;

import java.util.Calendar;
import java.util.Date;

/**
 * This class is used to pack together information about a Bill.
 */
public class Bill {
    String billName;
    String groupName;
    Float billValue;
    Calendar billDate;
    Float billLocationLatitute = 0.0f;
    Float billLocationLongitude = 0.0f;
    boolean locationIsSet = false;
    String billPicturePath;

}
