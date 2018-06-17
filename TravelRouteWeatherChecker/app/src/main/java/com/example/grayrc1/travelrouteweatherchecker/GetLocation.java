package com.example.grayrc1.travelrouteweatherchecker;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by grayrc1 on 4/06/2018.
 * base class for opening a connection to a restful api and getting a response
 *
 */

abstract class GetLocation extends AsyncTask<Void, Void, String>
{
    Activity activity;
    static String location;
    Button submitLocation; //takes submit and confirm buttons to switch them when data retrieved
    Button submitTime;

    public GetLocation(Activity activity, Button submitLocation, Button submitTime)
    {
        this.submitLocation = submitLocation;
        this.activity = activity;
        this.submitTime = submitTime;
    }
    abstract String getMapsUrl();



    //opens and returns a connection to the given url
    protected HttpURLConnection openConnection(String urlString)
    {
        URL urlObject = null;
        try {
            urlObject = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d("URL", "Url string " + urlString + " is invalid");
        }
        Log.d("URL", "url object: " + urlObject.toString());

        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection)urlObject.openConnection();

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK)
            {
                connection.connect();
            }
            else
            {
                Log.d("URL", "connection not ok, response code = " + responseCode);
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.d("URL", "unable to open http connection");
        } catch (NullPointerException e) {
            e.printStackTrace();
            Log.d("URL", "httpURLConnection connection is null");
        }
        return  connection;
    }

    //retrieves and returns response from url
    protected String getResponse(HttpURLConnection connection)
    {
        InputStream inputStream = null;
        try {
            inputStream = connection.getInputStream();

        } catch (IOException e){
            e.printStackTrace();
            Log.d("URL", "cannot get input stream");
        }
        InputStreamReader streamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(streamReader);
        StringBuilder stringBuilder = new StringBuilder();
        String responseString;
        try{
            while ((responseString = reader.readLine()) != null) {
                stringBuilder = stringBuilder.append(responseString);
            }
        } catch (IOException e)
        {
            e.printStackTrace();
            Log.d("URL", "cannot read response string");
        }

        return stringBuilder.toString();
    }
}