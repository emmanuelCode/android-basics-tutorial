/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Earthquake>> {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();


    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&minmag=6&limit=10";
    //if not enough is showing is this link instead->"https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=time&minmag=5&limit=10"

    //create an id for the loader useful when you have multiple loader
    private static final int EARTHQUAKE_LOADER_ID = 1;

    //
    private EarthquakeAdapter mAdapter;

    //a text view to show whether we have no earthquake or  no internet connection
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        // Find a reference to the {@link TextView} in the layout
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);

        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);

        //set the empty view on the listView if there no data to be shown
        earthquakeListView.setEmptyView(mEmptyStateTextView);

        // here we populate the adapter with an empty list to start with //
        mAdapter = new EarthquakeAdapter(this, new ArrayList<Earthquake>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(mAdapter);

        //this set the listview item to show the webpage or the earthquake showing more detail
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                // Find the current earthquake that was clicked on
                Earthquake currentEarthquake = mAdapter.getItem(position);

                // alternative way to get the position: Earthquake currentEarthquake = earthquakes.get(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri earthquakeUri = Uri.parse(currentEarthquake.getUrl());

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);


            }
        });


        // REMEMBER TO NOT FORGET THE ACCESS NETWORK STATE PERMISSION TO CHECK INTERNET CONNECTIVITY
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        // if networkInfo is not empty and network is connected then perform step...
        if (networkInfo != null && networkInfo.isConnected()) {

            // Get a reference to the LoaderManager, in order to interact with loaders.
            // the loaderManager will help us with screen rotation
            LoaderManager loaderManager = getLoaderManager();//there also getSupportLoaderManager();
            // initialize the loader and specify an id for reuse
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);

        } else {
            //set the loading indicator to gone if we have no connection
            //so that it will not overlap the view when showing "no internet connection"
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            mEmptyStateTextView.setText("no internet connection");
        }


    }

    /**
     *
     * this method prepare and create the Loader by initializing our Loader class {@link EarthquakeLoader}
     * and passing in the data we need to be process in the background Thread
     * @param id the id given when we initialize it
     * @param args Any arguments supplied by the caller TODO ??
     * @return a loader with a generic type list of earthquake
     */
    @Override
    public Loader<List<Earthquake>> onCreateLoader(int id, Bundle args) {

        return new EarthquakeLoader(this, USGS_REQUEST_URL);
    }

    /**
     * this method is called when the Loader finished gathering eatrhquake data
     * @param loader loader takes in a loader of generic type list of earthquake
     * @param earthquakes the returned list of earthquakes from the JSON parse
     */
    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> earthquakes) {

        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);


        // Set empty state text to display "No earthquakes found."
        // this TextView is attached to the earthquakeListView.setEmptyView()
        // here we just preparing our text to show up if the view is really empty
        mEmptyStateTextView.setText("No earthquakes found");


        // Clear the adapter of previous earthquake data
        mAdapter.clear();// we prepare our adapter by clearing any information as the EarthquakeArray was an empty at first

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (earthquakes != null && !earthquakes.isEmpty()) {
            mAdapter.addAll(earthquakes);//here you can comment out the adapter to test if the empty view message will be shown or to see the progress indicator
        }
    }

    /**
     *
     * called when the activity or Fragment is destroy
     * I need to remove code that has any references to the loader's data such as the adapter
     * @param loader takes in a loader of generic type list of earthquake
     */
    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {

        mAdapter.clear();

    }


}
