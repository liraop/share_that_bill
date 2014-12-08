package com.mobapp.almaslira.sharethatbill;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Adapted from https://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView
 *
 */
public class NotificationAdapter extends ArrayAdapter<Notification> {
    static final String TAG = "NotificationAdapter";

    public NotificationAdapter(Context context, ArrayList<Notification> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Notification item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_notification, parent, false);
        }

        String middleString = "";
        String endString = "";

        switch (item.type) {
            case Notification.BILL_CREATED:
                middleString = convertView.getContext().getResources().getString(R.string.notification_adapter_bill_created);
                endString = "";
                break;

            case Notification.BILL_EDITED:
                middleString = convertView.getContext().getResources().getString(R.string.notification_adapter_bill_edited);
                endString = "";
                break;

            case Notification.BILL_DELETED:
                middleString = convertView.getContext().getResources().getString(R.string.notification_adapter_bill_deleted);
                endString = "";
                break;

            case Notification.USER_ADDED:
                middleString = convertView.getContext().getResources().getString(R.string.notification_adapter_added);
                endString = convertView.getContext().getResources().getString(R.string.notification_adapter_to_the_group);
                break;

        }
        TextView mainText = (TextView) convertView.findViewById(R.id.textViewNotificationLayoutMainText);
        mainText.setText(item.owner + " " + middleString + " "+ item.description + " " + endString);

        Calendar now = Calendar.getInstance();

        long difference =  now.getTimeInMillis() - item.date.getTimeInMillis();

        // TODO
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Log.d(TAG, "item date: " + df.format(item.date.getTime()));

        Log.d(TAG, "difference: " + difference + ", item date: " + item.date.getTimeInMillis() + ", now: " + now.getTimeInMillis());

        difference /= 1000; // difference is now in seconds

        Log.d(TAG, "difference in secs: " + difference);

        String timeStamp = "";

        if (difference < 60)
            timeStamp = convertView.getContext().getResources().getString(R.string.notification_adapter_less_than_one_minute_ago);
        else if ((difference /= 60) < 60) // difference is now in minutes
            timeStamp = difference + " " + convertView.getContext().getResources().getString(R.string.notification_adapter_minutes_ago);
        else if ((difference /= 60) < 24) // difference is now in hours
            timeStamp = difference +  " " + convertView.getContext().getResources().getString(R.string.notification_adapter_hours_ago);
        else if ((difference /= 24) < 7) // difference is now in days
            timeStamp = difference +  " " + convertView.getContext().getResources().getString(R.string.notification_adapter_days_ago);
        else {
            difference /= 7; // difference is now in weeks
            timeStamp = difference +  " " + convertView.getContext().getResources().getString(R.string.notification_adapter_weeks_ago);
        }

        TextView secondaryText = (TextView) convertView.findViewById(R.id.textViewNotificationLayoutSecondaryText);
        secondaryText.setText(timeStamp);

        return convertView;
    }
}
