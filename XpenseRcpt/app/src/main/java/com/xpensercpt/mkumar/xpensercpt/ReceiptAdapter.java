package com.xpensercpt.mkumar.xpensercpt;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by mkumar on 10/17/15.
 * Adapter for TripView
 */
public class ReceiptAdapter  extends ArrayAdapter<Receipt> {

    private Context m_context;
    private int m_layoutResourceId;
    private List<Receipt> m_receipts;

    public ReceiptAdapter(Context context, int resource, List<Receipt> objects) {
        super(context, resource, objects);

        m_context = context;
        m_layoutResourceId = resource;
        m_receipts = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            // inflate the layout
            LayoutInflater inflater = ((Activity) m_context).getLayoutInflater();
            convertView = inflater.inflate(m_layoutResourceId, parent, false);
        }

        Receipt rcpt  = m_receipts.get(position);
        // get the TextView and then set the text (item name) and tag (item ID) values
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView subtitle = (TextView) convertView.findViewById(R.id.subtitle);

        title.setText(rcpt.getTitle());
        subtitle.setText(rcpt.getSubTitle());

        return convertView;


    }
}
