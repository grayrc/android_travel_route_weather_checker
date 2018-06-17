package com.example.grayrc1.travelrouteweatherchecker;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.text.DecimalFormat;

/**
 * Created by grayrc1 on 4/06/2018.
 * Asynchronously gets weather data from openweathermap.org
 */

class GetForecast extends GetLocation{

    String[] timeAndPlace;
    Data jsonData;
    TextView showLocation;
    String data;
    String[] forecast;
    Button submit;
    Button confirrm;


    public GetForecast(Activity activity, Button submit, Button confirm, String[] timeAndPlace,
                       Data jsonData, TextView showLocation){
        super(activity, confirm, submit);
        this.timeAndPlace = timeAndPlace;
        this.jsonData = jsonData;
        this.showLocation = showLocation;
        jsonData.forecast = new String[2];
        this.submit = submit;
        this.confirrm = confirm;
    }

    //builds the api url
    @Override
    String getMapsUrl() {
        String url = activity.getResources().getText(R.string.weatherPrefix) +
                timeAndPlace[0] +
                activity.getResources().getText(R.string.weatherMid) +
                timeAndPlace[1] +
                activity.getResources().getText(R.string.weatherSuffix);
        Log.d("WEATHER", url);
        return url;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String url = getMapsUrl();
        if(URLUtil.isValidUrl(url)){
            HttpURLConnection connection = openConnection(url);
            data = getResponse(connection);
            jsonData.jsonData = data;
        }
        else{
            Log.d("WEATHER", "Invalid url");
        }
        return data;
    }

    //data is extracted from the json, displayed to screen, and saved to the 'Data' class object
    @Override
    protected void onPostExecute(String forecastData)
    {
        forecast = getForecastFromJson(forecastData, timeAndPlace[2], timeAndPlace[3]);

        showLocation.setText(String.format("Time = %s %s\nTemperature = %s\nForecast = %s", timeAndPlace[2], timeAndPlace[3], forecast[0], forecast[1]));
        jsonData.forecast[0] = forecast[0];
        jsonData.forecast[1] = forecast[1];

        submit.setVisibility(View.GONE);
        confirrm.setVisibility(View.VISIBLE);
    }

    //extracts relevant data from the retrieved json
    private String[] getForecastFromJson(String jsonData, String date, String time)
    {
        String[] foreCast = new String[2];
        try {
            JSONObject data = new JSONObject(jsonData);
            JSONArray list = data.getJSONArray("list");
            JSONObject weather = null;
            String dtTxt = date + " " + time + ":00"; //formats the date and time as it is in json

            //finds part of forecast which corresponds to input time
            for(int i = 0; i < list.length(); i++)
            {
                JSONObject weatherInstance = list.getJSONObject(i);
                if (weatherInstance.getString("dt_txt").matches(dtTxt))
                {
                    weather = weatherInstance;
                    break;
                }
            }
            JSONObject main = weather.getJSONObject("main");
            String temp =  main.getString("temp");
            //converts temperature in Kelvin to Celsius to 2 decimal places
            double tempC = Double.parseDouble(temp) -273.15;
            DecimalFormat df = new DecimalFormat("##.##");
            String tem = df.format(tempC);
            foreCast[0] = tem;
            JSONArray w = weather.getJSONArray("weather");
            JSONObject jo = w.getJSONObject(0);
            foreCast[1] = jo.getString("description");
        } catch (JSONException e){
            e.printStackTrace();
        }
        return foreCast;
    }
}
