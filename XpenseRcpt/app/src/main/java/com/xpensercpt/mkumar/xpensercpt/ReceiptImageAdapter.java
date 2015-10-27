package com.xpensercpt.mkumar.xpensercpt;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by mkumar on 10/26/15.
 * Adding receipt image adapter for the list view
 */
public class ReceiptImageAdapter  extends ArrayAdapter<ReceiptImage.ReceiptImageData> {

    private Context m_context;
    private int m_layoutResourceId;
    private List<ReceiptImage.ReceiptImageData> m_imgs;

    public ReceiptImageAdapter(Context context, int resource, List<ReceiptImage.ReceiptImageData> objects) {
        super(context, resource, objects);

        m_context = context;
        m_layoutResourceId = resource;
        m_imgs = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            // inflate the layout
            LayoutInflater inflater = ((Activity) m_context).getLayoutInflater();
            convertView = inflater.inflate(m_layoutResourceId, parent, false);
        }

        ReceiptImage.ReceiptImageData data = m_imgs.get(position);
        // get the TextView and then set the text (item name) and tag (item ID) values
        ImageView imgView = (ImageView)convertView.findViewById(R.id.imageViewRcptImg);
        imgView.setImageBitmap(data.getImage());
        return convertView;


    }
}

