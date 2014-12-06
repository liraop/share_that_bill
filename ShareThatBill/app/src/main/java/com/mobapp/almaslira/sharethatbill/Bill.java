package com.mobapp.almaslira.sharethatbill;

import android.graphics.Picture;
import android.location.Location;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by jeajjr on 11/17/14.
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
