package com.xpensercpt.mkumar.xpensercpt;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
//import android.content.DialogInterface;
//import android.app.AlertDialog;

public class MainActivity extends ListActivity {

    private TripDataSource m_tripDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        setStatusBarColor();

        m_tripDataSource = new TripDataSource(this);
        m_tripDataSource.open();
        ArrayList<Trip> trips = m_tripDataSource.getAllTrips();
        ArrayList<SuperObjectItem> objects = new ArrayList<>();
        objects.add(new SuperObjectItem(0,null, null, null));
        int index = 1;
        for (Trip trip :
                trips) {
            objects.add(new SuperObjectItem(index,trip, null, null));
            index++;
        }
        ListAdapter adapter = new TripAdapter(this,R.layout.trip_view_row_item,objects);

        ListView listView = getListView();
        listView.setAdapter(adapter);

        /*
        ArrayAdapter<Trip> adapter = new ArrayAdapter<Trip>(
                this,
                android.R.layout.simple_list_item_1,
                trips
        );
        listView.setAdapter(adapter);
        */


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

    public void onButtonSelect(View view) {
        Intent myIntent = new Intent(this, AddTripActivity.class);
        //myIntent.putExtra("key", value); //Optional parameters
        this.startActivity(myIntent);
        /*
        new AlertDialog.Builder(this)
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
                */
    }
}
