package com.example.grayrc1.travelrouteweatherchecker;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class AddTime extends AppCompatActivity {

    String day = "";
    String hour = "";
    TextView txtTemp;
    TextView txtWeather;
    String[] AllData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_time);

        AllData = new String[7]; //string array which will hold location data to be sent back to NewRoute
        final Activity activity = this;
        final Button enterTime = findViewById(R.id.btnEnterTime);
        final Button submitTime = findViewById(R.id.btnConfirmTime);
        Intent getData = getIntent();
        final String[] data = getData.getStringArrayExtra("data");

        //This Adds the data from addLocation to the new array, name and coordinates at the start
        //and route name at the end
        for(int i = 0; i < data.length - 1; i++)
        {
            AllData[i] = data[i];
        }
        AllData[5] = data[3];

        final Date currentDate = new Date();
        txtTemp = findViewById(R.id.txtTemp);
        txtWeather = findViewById(R.id.txtWeather);

        final Spinner spnDays = findViewById(R.id.spnDay);
        final Spinner spnHours = findViewById(R.id.spnHour);
        final Data jsonData = new Data(); //Data Class takes data retrieved in async task so it can be used in this class.

        ArrayAdapter<String> daysAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getDays(currentDate));
        spnDays.setAdapter(daysAdapter);

        ArrayAdapter<String> hoursAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getHours(currentDate));
        spnHours.setAdapter(hoursAdapter);

        //reads the spinner selection and gets the forecast for that time from openweathermap
        enterTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //date and time saved as a string
                AllData[6] = spnDays.getSelectedItem().toString() + " " + spnHours.getSelectedItem().toString();
                day = spnDays.getSelectedItem().toString();
                hour = spnHours.getSelectedItem().toString();
                String[] timeAndPlace = {data[1], data[2], day, hour};
                GetForecast getForecast = new GetForecast(activity, enterTime, submitTime, timeAndPlace, jsonData, txtWeather);
                getForecast.execute();
            }
        });

        //saves the retrieved weather data and finishes the activity, passing data back to AddLocation
        submitTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AllData[3] = jsonData.forecast[0];
                AllData[4] = jsonData.forecast[1];

                Intent returnIntent = new Intent();
                returnIntent.putExtra("data", AllData);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }

    //return list of every 3 hours starting from the next one (this is as fine grained as the api
    //will allow.)
    private String[] getHours(Date date)
    {
        int hour = date.getHours();

        while(!(hour % 3 == 0))
        {
            hour++;
        }

        String[] hours = new String[8];
        for(int i = 0; i < 8; i++)
        {
            hours[i] = String.format("%02d:00", ((i*3 + hour) % 24));
        }
        return hours;
    }

    //return list of the next 5 days (which is the length of the forecast)
    private String[] getDays(Date date)
    {
        //int day = date.getDay();
        Calendar c = Calendar.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd");

        c.setTime(date);

        String[] nextDays = new String[5];
        for (int i = 0; i < 5; i++)
        {
            nextDays[i] = dateFormat.format(c.getTime()); //days[(i + day)%days.length];
            c.add(Calendar.DAY_OF_MONTH, 1);
        }
        return nextDays;
    }
}
