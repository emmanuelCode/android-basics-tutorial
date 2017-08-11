package com.example.android.quakereport;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;



/**
 * the main thing on arrayAdapter: build a constructor that support arrayList and override the getView method CTRL O
 */

public class EarthquakeAdapter extends ArrayAdapter<Earthquake> {

    //this is a seperator for splitting our location String from the {@link earthquake}
    private static final String LOCATION_SEPARATOR = " of ";

    //the constructor takes an context and a arraylist of type earthquakesclass to pass in
    public EarthquakeAdapter(@NonNull Context context, ArrayList<Earthquake> earthquakes) {
        super(context,0,earthquakes);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listViewItem = convertView;
        //TODO:need to understand this part better
        if(listViewItem == null){

            listViewItem = LayoutInflater.from(getContext()).inflate(R.layout.earthquake_list_item, parent, false);

        }

        //get the current position instance of the earthquake class
        Earthquake currentEarthquake = getItem(position);

        //link textView to their id from the list item
        //then set the text from the  id and the earthquakeclass

        //magnitude textView
        TextView magnitudeText = (TextView) listViewItem.findViewById(R.id.magnitude);

        //a String conversion from a double primitive
        String formattedMagnitude = formatMagnitude(currentEarthquake.getMagnitude());

        //set the textView to the formatted String
        magnitudeText.setText(formattedMagnitude);


        //a String holding our current earthquake from our {@link ArrayList<Earthquake>}
        String originalLocation = currentEarthquake.getLocation();

        //the location off set (eg 90km Est...)
        String locationOffset;

        //the location of the earthquake (eg San Francisco, Ca...)
        String primaryLocation;

        if(originalLocation.contains(LOCATION_SEPARATOR)){

            String[] stringParts = originalLocation.split(LOCATION_SEPARATOR);
            locationOffset = stringParts[0] + LOCATION_SEPARATOR;
            primaryLocation = stringParts[1];

        }else{
            locationOffset = "Near the";
            primaryLocation = originalLocation;
        }


        TextView locationOffsetView = (TextView) listViewItem.findViewById(R.id.location_offset);
        TextView primaryLocationView = (TextView) listViewItem.findViewById(R.id.primary_location);

        locationOffsetView.setText(locationOffset);
        primaryLocationView.setText(primaryLocation);


        //the dateObject witch hold out Unix time (it's contains the date as well as the time)
        Date dateObject = new Date(currentEarthquake.getTimeInMilliseconds());

        //date textView
        TextView dateView = (TextView) listViewItem.findViewById(R.id.date);

        //a String conversion from Unix time to a date  {@link SimpleDateFormat}(i.e SimpleDateFormat("LLL dd, yyyy"))
        String formattedDate = formatDate(dateObject);

        //set dateView text
        dateView.setText(formattedDate);

        //the time textView
        TextView timeView = (TextView) listViewItem.findViewById(R.id.time);

        //a String conversion from Unix time to a clock  {@link SimpleDateFormat}(i.e SimpleDateFormat("h:mm a"))
        String formattedTime = formatTime(dateObject);

        timeView.setText(formattedTime);


        //the next code following, set up the magnitude TextView background to display the right color

        // Set the proper background color on the magnitude circle.
        // Fetch the background from the TextView, which is a GradientDrawable.
        GradientDrawable magnitudeCircle = (GradientDrawable) magnitudeText.getBackground();

        // Get the appropriate background color based on the current earthquake magnitude
        int magnitudeColor = getMagnitudeColor(currentEarthquake.getMagnitude());

        // Set the color on the magnitude circle
        magnitudeCircle.setColor(magnitudeColor);




        return listViewItem;
    }



    /**
     * Return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
     * @param dateObject takes the date object that hold the Unix time
     */
    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }

    /**
     * Return the formatted date string (i.e. "4:30 PM") from a Date object.
     * @param dateObject takes the date object that hold the Unix time
     */
    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }


    /**
     * Return the formatted magnitude string showing 1 decimal place (i.e. "3.2")
     * from a decimal magnitude value.
     * @param magnitude takes the magnitude from the earthquake object
     */
    private String formatMagnitude(double magnitude) {
        DecimalFormat magnitudeFormat = new DecimalFormat("0.0");
        return magnitudeFormat.format(magnitude);
    }

    /**
     *
     * @param magnitude takes the magnitude witch is a double
     * @return the resource id of the color
     */
    private int getMagnitudeColor(double magnitude) {
        int magnitudeColorResourceId;
        //case cannot accept double so we cast it
        int magnitudeFloor = (int) Math.floor(magnitude);
        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }
        return ContextCompat.getColor(getContext(), magnitudeColorResourceId);
    }



}
