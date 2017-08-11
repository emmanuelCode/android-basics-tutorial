package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;



/**
 * here we create a loader class  to use it with our main activity
 * the AsyncTaskLoader is similar to the AsyncTask
 */
public class EarthquakeLoader extends AsyncTaskLoader<List<Earthquake>> {

    private static final String LOG_TAG = EarthquakeLoader.class.getName();

    private String mUrl;


    /**
     * here we pass in the context and the request url inside this constructor
     * @param context take the app context(Activity)
     */
    public EarthquakeLoader(Context context, String url) {
        super(context);
        mUrl = url;
        
    }


    /**
     * This method trigger automatically when init the loader in the EarthquakeActivity
     */
    @Override
    protected void onStartLoading() {
        forceLoad();//trigger the loader to start doing the background work
    }


    /**
     * this method load in the background thread to do some work
     * @return the list of earthquake and notify the {#onLoadFinished} in the EarthquakeActivity
     */
    @Override
    public List<Earthquake> loadInBackground() {

        if(mUrl == null){
            return null;
        }

//        //slow down the background Thread
//        //To simulate a slow connection and test the loading indicator
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }



        return QueryUtils.fetchEarthquakeData(mUrl);
    }

}
