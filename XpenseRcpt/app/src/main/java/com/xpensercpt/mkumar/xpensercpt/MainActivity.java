package com.xpensercpt.mkumar.xpensercpt;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
//import android.content.DialogInterface;
//import android.app.AlertDialog;

public class MainActivity extends ListActivity {

    private TripDataSource m_tripDataSource;
    private ReceiptDataSource m_rcptDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        m_tripDataSource = new TripDataSource(this);
        m_tripDataSource.open();

        m_rcptDataSource = new ReceiptDataSource(this);
        m_rcptDataSource.open();
        ArrayList<Receipt> rcpts = m_rcptDataSource.getAllReceipts(2);

        ListView listView = getListView();

        ArrayList<Trip> trips = m_tripDataSource.getAllTrips();
        ArrayAdapter<Receipt> adapter = new ArrayAdapter<Receipt>(
                this,
                android.R.layout.simple_list_item_1,
                rcpts
        );

        /*
        ArrayList<String> array = new ArrayList<>();
        array.add("test1");
        array.add("test2");
        array.add("test3");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                array
        );
        */

        listView.setAdapter(adapter);

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
