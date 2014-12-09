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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * ListView Adapter for the TwoStringsClass class.
 * Adapted based on the code found at https://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView
 */
public class CustomTwoItemAdapter extends ArrayAdapter<TwoStringsClass> {

    public CustomTwoItemAdapter(Context context, ArrayList<TwoStringsClass> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        TwoStringsClass item = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_two_items_list, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.textViewTwoItemVIew1);
        TextView tvHome = (TextView) convertView.findViewById(R.id.textViewTwoItemVIew2);

        // Populate the data into the template view using the data object
        tvName.setText(item.first);

        tvHome.setText(String.format("$%.2f", Float.parseFloat(item.second)));
        // Return the completed view to render on screen
        return convertView;
    }


}
