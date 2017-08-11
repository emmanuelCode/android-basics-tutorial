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
package com.example.android.soonami;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;

/**
 * Displays information about a single earthquake.
 */
public class MainActivity extends AppCompatActivity {

    /** Tag for the log messages */
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    /** URL to query the USGS dataset for earthquake information */
    private static final String USGS_REQUEST_URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2014-01-01&endtime=2014-12-01&minmagnitude=7";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Kick off an {@link AsyncTask} to perform the network request
        TsunamiAsyncTask task = new TsunamiAsyncTask();
        task.execute();
    }

    /**
     * Update the screen to display information from the given {@link Event}.
     */
    private void updateUi(Event earthquake) {
        // Display the earthquake title in the UI
        TextView titleTextView = (TextView) findViewById(R.id.title);
        titleTextView.setText(earthquake.title);

        // Display the earthquake date in the UI
        TextView dateTextView = (TextView) findViewById(R.id.date);
        dateTextView.setText(getDateString(earthquake.time));

        // Display whether or not there was a tsunami alert in the UI
        TextView tsunamiTextView = (TextView) findViewById(R.id.tsunami_alert);
        tsunamiTextView.setText(getTsunamiAlertString(earthquake.tsunamiAlert));
    }

    /**
     * Returns a formatted date and time string for when the earthquake happened.
     */
    private String getDateString(long timeInMilliseconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy 'at' HH:mm:ss z");
        return formatter.format(timeInMilliseconds);
    }

    /**
     * Return the display string for whether or not there was a tsunami alert for an earthquake.
     */
    private String getTsunamiAlertString(int tsunamiAlert) {
        switch (tsunamiAlert) {
            case 0:
                return getString(R.string.alert_no);
            case 1:
                return getString(R.string.alert_yes);
            default:
                return getString(R.string.alert_not_available);
        }
    }

    /**
     * {@link AsyncTask} to perform the network request on a background thread, and then
     * update the UI with the first earthquake in the response.
     */
    private class TsunamiAsyncTask extends AsyncTask<URL, Void, Event> {

        @Override
        protected Event doInBackground(URL... urls) {
            // Create URL object
            URL url = createUrl(USGS_REQUEST_URL);

            // Perform HTTP request to the URL and receive a JSON response back
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);//this method return a JSON String
            } catch (IOException e) {
                // Handle the IOException
                Log.e(LOG_TAG, "Problem making the HTTP request.", e);
            }

            // Extract relevant fields from the JSON response and create an {@link Event} object
            Event earthquake = extractFeatureFromJson(jsonResponse);

            // Return the {@link Event} object as the result fo the {@link TsunamiAsyncTask}
            return earthquake;
        }

        /**
         * Update the screen with the given earthquake (which was the result of the
         * {@link TsunamiAsyncTask}).
         */
        @Override
        protected void onPostExecute(Event earthquake) {
            if (earthquake == null) {
                return;
            }

            updateUi(earthquake);
        }

        /**
         * Returns new URL object from the given string URL.
         */
        private URL createUrl(String stringUrl) {// we could also set a throw so that the try/catch is outside the method
            //we initialize the variable to null here so we can access it outside the try/catch(variable scope)
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                // exception.printStackTrace(); will also print the error in detail format
                Log.e(LOG_TAG, "Error with creating URL", exception);
                return null;//here it ends the method with null not executing the rest of the code
            }
            return url;
        }

        /**
         * Make an HTTP request to the given URL and return a String as the response.
         */
        private String makeHttpRequest(URL url) throws IOException {
            // start setting the string to nothing
            String jsonResponse = "";

            // here we make our code robust by verifying if we do have an url
            if(url == null){

                return jsonResponse;//we return the method early if we don't have a url
            }

            //we set our HttpURLConnection and InputStream object null before doing anything with it

            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;

            try {
                // here we take our url parameter and cast it to a HttpURLConnection
                urlConnection = (HttpURLConnection) url.openConnection();

                //we set the request method -> we want to read from the server so we use "GET"
                urlConnection.setRequestMethod("GET");

                //specify the time when reading from an inputStream
                urlConnection.setReadTimeout(10000 /* milliseconds */);//if time reach ->java.net.SocketTimeoutException error
                //set timeout when opening a communications link
                urlConnection.setConnectTimeout(15000 /* milliseconds */);//if time reach ->java.net.SocketTimeoutException error

                //connect the with the given url
                urlConnection.connect();

                //verify the response code
                if (urlConnection.getResponseCode() == 200){
                    //get the inputStream witch is a Stream of bytes
                    inputStream = urlConnection.getInputStream();
                    //we read the bytes with a helper method
                    jsonResponse = readFromStream(inputStream);
                }else{
                    //if the response code is not 200 we Log.e the error
                    Log.e(LOG_TAG,"Error Response Code:" + urlConnection.getResponseCode());
                }

            } catch (IOException e) {
                // Handle the exception by passing a third parameter on Log -> "Log.e(LOG_TAG, msg, e )"
                Log.e(LOG_TAG,"Problem retrieving the earthquake JSON results.", e);
                //whether we were successful or not we close the connection if not empty(null)
                // for both InputStream and HttpUrlConnection(urlConnection)
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    // function must handle java.io.IOException here
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        /**
         * Convert the {@link InputStream} into a String which contains the
         * whole JSON response from the server.
         */
        private String readFromStream(InputStream inputStream) throws IOException {

            //we use a StringBuilder because it mutable can change overtime, you re able to use one variable and keep adding new String on it
            //were a String is unmutable
            StringBuilder output = new StringBuilder();

            //we verify if the inputStream is not null then we proceed on reading
            if (inputStream != null) {

                //the InputStreamReader help decode the bytes(InputStream) into character(UTF-8) one small chunk at time
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                //the BufferedReader accelerate that process by reading big chunk at time
                BufferedReader reader = new BufferedReader(inputStreamReader);

                //we pass in the lines read by the BufferedReader into a String
                String line = reader.readLine();

                // while the line is not empty(null)
                while (line != null) {//when the line will be empty(null) break out of the loop

                    //add line to the StringBuilder variable(output)
                    output.append(line);
                    //rewrote and Store again to the String variable(line)
                    line = reader.readLine();
                }
            }
            return output.toString();//return the StringBuilder witch all the lines has been added to it
        }

        /**
         * Return an {@link Event} object by parsing out information
         * about the first earthquake from the input earthquakeJSON string.
         */
        private Event extractFeatureFromJson(String earthquakeJSON) {

            if(TextUtils.isEmpty(earthquakeJSON)){
                return null;
            }

            try {
                JSONObject baseJsonResponse = new JSONObject(earthquakeJSON);
                JSONArray featureArray = baseJsonResponse.getJSONArray("features");

                // If there are results in the features array
                if (featureArray.length() > 0) {
                    // Extract out the first feature (which is an earthquake)
                    JSONObject firstFeature = featureArray.getJSONObject(0);
                    JSONObject properties = firstFeature.getJSONObject("properties");

                    // Extract out the title, time, and tsunami values
                    String title = properties.getString("title");
                    long time = properties.getLong("time");
                    int tsunamiAlert = properties.getInt("tsunami");

                    // Create a new {@link Event} object
                    return new Event(title, time, tsunamiAlert);
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Problem parsing the earthquake JSON results", e);
            }
            return null;
        }
    }
}
