package com.xpensercpt.mkumar.xpensercpt;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mkumar on 10/10/15.
 * ArrayAdapter for the Trip activity
 */
public class TripAdapter extends ArrayAdapter<SuperObjectItem> {

    private Context m_context;
    private int m_layoutResourceId;
    private List<SuperObjectItem> m_superObjects;

    public TripAdapter(Context context, int resource, List<SuperObjectItem> objects) {
        super(context, resource, objects);

        m_context = context;
        m_layoutResourceId = resource;
        m_superObjects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            // inflate the layout
            LayoutInflater inflater = ((Activity) m_context).getLayoutInflater();
            convertView = inflater.inflate(m_layoutResourceId, parent, false);
        }
        // object item based on the position
        SuperObjectItem objectItem = m_superObjects.get(position);
        if (!objectItem.isTrip()){
            TextView section = (TextView) convertView.findViewById(R.id.separator);
            section.setVisibility(View.VISIBLE);
            section.setText(R.string.add_trip);

            TextView textViewItem = (TextView) convertView.findViewById(R.id.title);
            textViewItem.setText(R.string.new_trip);
            TextView textViewItem2 = (TextView) convertView.findViewById(R.id.subtitle);
            textViewItem2.setText("");
            textViewItem2.setVisibility(View.GONE);
            return convertView;
        }
        TextView section = (TextView) convertView.findViewById(R.id.separator);
        if (position == 1) {
            section.setVisibility(View.VISIBLE);
            section.setText(R.string.added_trips);
        }
        else
            section.setVisibility(View.GONE);

        Trip trip = objectItem.getTrip();
        // get the TextView and then set the text (item name) and tag (item ID) values
        TextView textViewItem = (TextView) convertView.findViewById(R.id.title);
        TextView textViewItem2 = (TextView) convertView.findViewById(R.id.subtitle);
        textViewItem2.setVisibility(View.VISIBLE);

        textViewItem.setText(trip.getName());
        textViewItem2.setText(trip.getDate());

        return convertView;


    }
}
