package com.mobapp.almaslira.sharethatbill;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Adapted from https://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView
 *
 */
public class NotificationAdapter extends ArrayAdapter<Notification> {

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



        TextView secondaryText = (TextView) convertView.findViewById(R.id.textViewNotificationLayoutSecondaryText);
        secondaryText.setText(item.description);

        return convertView;
    }
}
