package com.example.grayrc1.travelrouteweatherchecker;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import java.net.HttpURLConnection;


/**
 * Created by grayrc1 on 4/06/2018.
 * extends get location and retrieves data from googlemapsapi based on input coordinates
 *
 */

class GetLocationFromCoordinates extends GetLocation{

    String[] rawCoordinates;
    Data data;

    public GetLocationFromCoordinates(Activity activity, Button submitLocation, Button submitTime,
                                      String[] coordinates, Data data) {
        super(activity, submitLocation, submitTime);
        this.rawCoordinates = coordinates;
        this.data = data;
    }

    @Override
    protected String doInBackground(Void... voids) {

        String url = getMapsUrl();

        if(URLUtil.isValidUrl(url))
        {
            HttpURLConnection connection1 = openConnection(url);
            data.jsonData =  getResponse(connection1);
        }
        else{
            Log.d("MAPS", "location from coordinate url is invalid");
        }

        return null;
    }

    @Override
    protected void onPostExecute(String location)
    {
        submitLocation.setVisibility(View.GONE);
        submitTime.setVisibility(View.VISIBLE);
    }


    String getMapsUrl()
    {
        String url = activity.getResources().getText(R.string.coordinatesPrefix) +
                rawCoordinates[0] +
                activity.getResources().getText(R.string.coordinatesMid) +
                rawCoordinates[1] +
                activity.getResources().getText(R.string.coordinatesSuffix);
        return url;
    }
}
