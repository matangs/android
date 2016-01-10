package com.xpensercpt.mkumar.xpensercpt;

import android.annotation.TargetApi;
import android.content.Intent;import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.view.Window;
import android.view.WindowManager;

import com.xpensercpt.mkumar.xpensercpt.swipe.SwipeToDismissTouchListener;
import com.xpensercpt.mkumar.xpensercpt.swipe.adapter.ListViewAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TripDataSource m_tripDataSource;
    private TripAdapter m_adapter;
    private ArrayList<Trip> m_trips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this, AddTripActivity.class);
                MainActivity.this.startActivity(myIntent);

                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
            }
        });

        setStatusBarColor();
        addedTripListView();

    }

    @Override
    protected void onStart() {
        super.onStart();
        int prevCount = m_trips.size();

        m_tripDataSource.open();
        ArrayList<Trip> newTrips = m_tripDataSource.getAllTrips();
        m_tripDataSource.close();

        int newCount = newTrips.size();
        if (newCount > prevCount) {
            m_trips.clear();
            m_trips.addAll(newTrips);
            m_adapter.notifyDataSetChanged();
        }
    }

    private void addedTripListView(){
        m_tripDataSource = new TripDataSource(this);
        m_trips = new ArrayList<>();//m_tripDataSource.getAllTrips();
        m_adapter = new TripAdapter(this,R.layout.trip_view_row_item,m_trips);

        ListView listView = (ListView)findViewById(R.id.added_trip_list);//getListView();
        listView.setAdapter(m_adapter);


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
                                Trip trip = m_trips.get(position);
                                m_adapter.remove(trip);

                                m_tripDataSource.open();
                                m_tripDataSource.deleteTrip(trip);
                                m_tripDataSource.close();

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
                Trip trip = m_trips.get(position);
                // launch intent tripsactivity
                Intent myIntent = new Intent(MainActivity.this, TripsActivity.class);
                myIntent.putExtra("TripID", trip.getPrimaryKey()); //Optional parameters
                myIntent.putExtra("TripName", trip.getName());
                MainActivity.this.startActivity(myIntent);
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
        //getMenuInflater().inflate(R.menu.menu_main, menu);
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
