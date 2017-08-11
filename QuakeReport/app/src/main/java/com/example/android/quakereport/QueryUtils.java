package com.example.android.quakereport;

import android.util.Log;

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
import java.util.ArrayList;

import static android.R.id.input;
import static com.example.android.quakereport.EarthquakeActivity.LOG_TAG;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {


    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }



    public static ArrayList<Earthquake> fetchEarthquakeData(String requestUrl){
        //create the url with our own method
        URL url = createUrl(requestUrl);
        //perform the Http request our own method
        String jsonResponse = null;
        try{

            jsonResponse = makeHttpRequest(url);

        }catch (IOException e){
            Log.e(QueryUtils.class.getSimpleName(), "Error closing input stream", e);

        }
        //here we prepare a list of earthquake to return
        ArrayList<Earthquake> earthquakes = extractEarthquakes(jsonResponse);

        return earthquakes;
    }


    /**
     *
     * the method encapsulate the url inside a try/catch block
     * @param stringUrl the url of http String
     * @return a valid url
     */
    private static URL createUrl(String stringUrl){
        URL url = null;// outside the try/catch for the variable scope
        try{
            url = new URL(stringUrl);
        }catch (MalformedURLException e){
            Log.e(QueryUtils.class.getSimpleName(),"invalid url",e);
            return null;//here it ends the method with null not executing the rest of the code
        }

        return url ;
    }


    /**
     *
     * @param url the request url passed in by the {{@link #fetchEarthquakeData(String)}}
     * @return a string containing the JSON response
     * @throws IOException we need to handle the InputStream error if any, try/catch inside {{@link #fetchEarthquakeData(String)}}
     */
    private static String makeHttpRequest(URL url) throws IOException{
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }


        // REMEMBER TO NOT FORGET THE INTERNET PERMISSION FOR FETCHING DATA AND VERIFY THAT OUR INTERNET WORKS

        // here we consider variable scope to have access inside the try/catch block
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }


    /**
     *
     * @param inputStream takes the inputStream given by the server (request url)
     * @return build and return the String containing the server response
     * @throws IOException handle the InputStream error if any, try/catch inside {{@link #makeHttpRequest(URL)}}
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }





    /**
     * Return a list of {@link Earthquake} objects that has been built up from
     * parsing a JSON response.
     */
    private static ArrayList<Earthquake> extractEarthquakes(String earthquakeJSON) {

        // Create an empty ArrayList that we can start adding earthquakes from the JSON key values
        ArrayList<Earthquake> earthquakes = new ArrayList<>();

        // Try to parse the JSON response from the USGS_REQUEST_URL if there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // build up a list of Earthquake objects with the corresponding data.
            // the baseJsonResponse is actually a JSONObject
            JSONObject baseJsonResponse = new JSONObject(earthquakeJSON);

            // here we extract the array in the JSON key value "features"
            JSONArray earthquakeArray = baseJsonResponse.getJSONArray("features");

            // here we loop into the array getting the fields we need for the earthquake data
            for(int i = 0;i < earthquakeArray.length();i++){

                //since the array contains Object of the same type we get each one of them
                JSONObject currentEarthquake = earthquakeArray.getJSONObject(i);

                // Extract the value for the key called "properties" in a JSONObject
                JSONObject properties = currentEarthquake.getJSONObject("properties");

                // Extract the value for the key called "mag" in a double
                Double magnitude = properties.getDouble("mag");

                // Extract the value for the key called "place" in a String
                String place = properties.getString("place");

                // Extract the value for the key called "time" in a long data type
                long time = properties.getLong("time");

                // Extract the value for the key called "url" in a String
                String url = properties.getString("url");


                // add a new {@link Earthquake} object with the magnitude, location, time,
                // and url from the JSON response.
                earthquakes.add(new Earthquake(magnitude,place,time,url));
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return earthquakes;
    }

}