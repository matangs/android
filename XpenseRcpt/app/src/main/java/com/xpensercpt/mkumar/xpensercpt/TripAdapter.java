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
public class TripAdapter extends ArrayAdapter<Trip> {

    private Context m_context;
    private int m_layoutResourceId;
    private List<Trip> m_trips;

    public TripAdapter(Context context, int resource, List<Trip> objects) {
        super(context, resource, objects);

        m_context = context;
        m_layoutResourceId = resource;
        m_trips = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            // inflate the layout
            LayoutInflater inflater = ((Activity) m_context).getLayoutInflater();
            convertView = inflater.inflate(m_layoutResourceId, parent, false);
        }

        TextView section = (TextView) convertView.findViewById(R.id.separator);
        if (position == 0) {
            section.setVisibility(View.VISIBLE);
            section.setText(R.string.added_trips);
        }
        else
            section.setVisibility(View.GONE);

        Trip trip = m_trips.get(position);
        // get the TextView and then set the text (item name) and tag (item ID) values
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView subtitle = (TextView) convertView.findViewById(R.id.subtitle);

        title.setText(trip.getName());
        subtitle.setText(trip.getDate());

        return convertView;


    }
}
