package com.mobapp.almaslira.sharethatbill;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Adapted from https://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView
 *
 */
public class NotificationAdapter extends ArrayAdapter<NotificationsTab> {

    public NotificationAdapter(Context context, ArrayList<NotificationsTab> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        NotificationsTab item = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_two_items_list, parent, false);
        }
        // Lookup view for data population
        TextView mainText = (TextView) convertView.findViewById(R.id.textViewTwoItemVIew1);
        mainText.setText(item.first);

        TextView secondaryText = (TextView) convertView.findViewById(R.id.textViewTwoItemVIew2);
        secondaryText.setText(String.format("$%.2f", Float.parseFloat(item.second)));

        return convertView;
    }


}
