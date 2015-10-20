package com.xpensercpt.mkumar.xpensercpt;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.xpensercpt.mkumar.xpensercpt.swipe.SwipeToDismissTouchListener;
import com.xpensercpt.mkumar.xpensercpt.swipe.adapter.ListViewAdapter;

import java.util.ArrayList;

public class TripsActivity extends AppCompatActivity {

    private Trip m_trip;
    private String m_tripName;
    private ReceiptDataSource m_rcptDataSource;
    private ReceiptAdapter m_rcptAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        setStatusBarColor();
        addedRcptListView();
        onAddRcptBtnClick();
    }

    @Override
    protected void onStart() {
        super.onStart();
        int prevCount = m_trip.getReceipts().size();

        m_rcptDataSource.open();
        ArrayList<Receipt> newRcpts = m_rcptDataSource.getAllReceipts((int) m_trip.getPrimaryKey());
        m_rcptDataSource.close();

        int newCount = newRcpts.size();
        if (newCount > prevCount) {
            m_trip.getReceipts().clear();
            m_trip.getReceipts().addAll(newRcpts);
            m_rcptAdapter.notifyDataSetChanged();
        }
    }

    private void addedRcptListView(){
        m_rcptDataSource = new ReceiptDataSource(this);
        Intent intent = getIntent();
        long tripId = intent.getLongExtra("TripID",-1);
        m_trip = new Trip(tripId);
        m_tripName = intent.getStringExtra("TripName");
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(m_tripName);

        m_rcptAdapter = new ReceiptAdapter(this,R.layout.trip_view_row_item,m_trip.getReceipts());

        ListView listView = (ListView)findViewById(R.id.added_rcpt_list);
        listView.setAdapter(m_rcptAdapter);


        final SwipeToDismissTouchListener<ListViewAdapter> touchListener =
                new SwipeToDismissTouchListener<>(
                        new ListViewAdapter(listView),
                        new SwipeToDismissTouchListener.DismissCallbacks<ListViewAdapter>() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListViewAdapter view, int position) {
                                m_rcptAdapter.remove(m_trip.getReceipts().get(position));
                            }
                        });
        listView.setOnTouchListener(touchListener);
        listView.setOnScrollListener((AbsListView.OnScrollListener) touchListener.makeScrollListener());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                if (touchListener.existPendingDismisses()) {
                    touchListener.undoPendingDismiss();
                    return;
                }
                Receipt rcpt = m_trip.getReceipts().get(position);
                // launch intent tripsactivity
                Intent myIntent = new Intent(TripsActivity.this, ReceiptActivity.class);
                myIntent.putExtra("ReceiptID", rcpt.getPrimaryKey()); //Optional parameters
                TripsActivity.this.startActivity(myIntent);
            }
        });
    }

    private void onAddRcptBtnClick(){
        Button addButton = (Button) findViewById(R.id.addNewRcptButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(TripsActivity.this, ReceiptActivity.class);
                myIntent.putExtra("ReceiptID", -1);
                TripsActivity.this.startActivity(myIntent);
            }
        });
    }

    @TargetApi(21)
    void setStatusBarColor(){
        if (Build.VERSION.SDK_INT < 21)
            return;

        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.darkest_orange));

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
