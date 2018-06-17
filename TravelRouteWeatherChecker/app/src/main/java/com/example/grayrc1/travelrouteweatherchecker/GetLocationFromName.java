package com.example.grayrc1.travelrouteweatherchecker;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

/**
 * Created by grayrc1 on 4/06/2018.
 * Gets location data from openstreetmaps.org based on name input
 *
 */

class GetLocationFromName extends GetLocation
{
    Data data;
    String rawLocation;
    public GetLocationFromName(Activity activity, Button submitLocation, Button submitTime, String rawLocation, Data data) {
        super(activity, submitLocation, submitTime);
        this.rawLocation = rawLocation;
        this.data = data;
    }

    @Override
    protected String doInBackground(Void... voids) {
        HttpURLConnection connection = openConnection(getMapsUrl());
        String response = getResponse(connection);
        openDialog(response);
        return location;
    }

    @Override
    protected void onPostExecute(String s)
    {
        Log.d("MAPS", "what");
        submitLocation.setVisibility(View.GONE);
        submitTime.setVisibility(View.VISIBLE);
    }

    //opens a dialog fragment with a list of the matches to the user's input
    private void openDialog(String response)
    {
        Bundle bundle = new Bundle();
        bundle.putString("response", response);
        FragmentManager fm = activity.getFragmentManager();
        ChooseLocation chooseLocation = new ChooseLocation();
        chooseLocation.setArguments(bundle);
        chooseLocation.show(fm, "Choose Location");
    }

    String getMapsUrl()
    {
        String url = activity.getResources().getText(R.string.openMapsRoot) +
                rawLocation +
                activity.getResources().getText(R.string.openMapsSuffix);
        Log.d("MAPS", url);
        return url;
    }




}

