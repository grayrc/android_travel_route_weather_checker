package com.example.grayrc1.travelrouteweatherchecker;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

/*
 *Location activity displays details about location, time and weather for given saved location
 *
 */
public class Location extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        Intent getLocation = getIntent();
        String locationName = getLocation.getStringExtra("locationName");

        String[] locationData = getLocationData(locationName);
        TextView name = findViewById(R.id.txtLocationName);
        name.setText(locationData[0]);
        TextView time = findViewById(R.id.txtLTime);
        time.setText(locationData[5]);
        TextView latitude = findViewById(R.id.txtLLat);
        latitude.setText(locationData[1]);
        TextView longitude = findViewById(R.id.txtLLon);
        longitude.setText(locationData[2]);
        TextView temp = findViewById(R.id.txtLTemp);
        temp.setText(locationData[3] + " C");
        TextView forecast = findViewById(R.id.txtLForecast);
        forecast.setText(locationData[4]);
    }

    //retrieves location data from the database
    private String[] getLocationData(String locationName)
    {
        SQLiteDatabase routeDb = openOrCreateDatabase("routeDb", MODE_PRIVATE, null);
        String[] columns = {"name", "latitude", "longitude", "temperature", "forecast", "time"};
        String[] locationData = new String[columns.length];
        String selectQuery = "SELECT * FROM location WHERE location.name = '" + locationName + "';";
        Cursor recordSet = routeDb.rawQuery(selectQuery, null);
        recordSet.moveToFirst();

        for(int i = 0; i < columns.length; i++)
        {
            locationData[i] = recordSet.getString(recordSet.getColumnIndex(columns[i]));
        }

        return locationData;
    }
}
