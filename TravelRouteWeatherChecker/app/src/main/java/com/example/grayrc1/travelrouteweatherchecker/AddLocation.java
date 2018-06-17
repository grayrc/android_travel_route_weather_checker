package com.example.grayrc1.travelrouteweatherchecker;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static java.lang.Double.parseDouble;

public class AddLocation extends AppCompatActivity {

    String FinalLocation; //the location to be passed back to NewRoute
    String[] Coordinates; //the coordinates to be passed back to NewRoute

    static final int ADDTIME_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        FinalLocation = "";

        final TextView txtCoordinates = findViewById(R.id.txtCoordinates);
        final TextView txtLatitude = findViewById(R.id.txtLatitude);
        final TextView txtLongitude = findViewById(R.id.txtLongitude);
        final TextView txtLocation = findViewById(R.id.txtLocation);
        final EditText enterLocation = findViewById(R.id.edtLocation);
        final EditText rawLatitude = findViewById(R.id.edtLatitude);
        final EditText rawLongitude = findViewById(R.id.edtLongitude);
        final Button submitLocation = findViewById(R.id.btnSubmitLocation);
        final Button submitTime = findViewById(R.id.btnConfirmTime);
        final Button btnContinue = findViewById(R.id.btnContinue);
        final Activity activity = this;

        final Data data = new Data(); //data class allows us to access data retrieved in async tasks

        submitTime.setVisibility(View.GONE); //this is the (poorly named) confirm location button

        Intent getRouteName = getIntent();
        //retrieves the route name from NewRoute
        final String routeName = getRouteName.getStringExtra("routeName");

        //when submit location button is selected, data is retrieved from web apis based on either the
        //entered name or coordinates
        submitLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            String[] coordinates = new String[2];
            coordinates[0] = rawLatitude.getText().toString();
            coordinates[1] = rawLongitude.getText().toString();
            String location = enterLocation.getText().toString();

            if(!coordinates[0].matches("") && !coordinates[1].matches("")){
                Coordinates = coordinates;
                //checks that entered coordinates are in valid range
                if(parseDouble(Coordinates[0]) <= 90  &&  parseDouble(Coordinates[0]) > -90
                        && parseDouble(Coordinates[1]) <= 180 && parseDouble(Coordinates[1]) > -180)
                {
                    GetLocationFromCoordinates getLocationFromCoordinates =
                            new GetLocationFromCoordinates(activity, submitLocation,
                                    submitTime, coordinates, data);
                    getLocationFromCoordinates.execute();
                }
                else
                {
                    Toast.makeText(getBaseContext(), "Coordinates out of range.", Toast.LENGTH_LONG).show();
                }

            }
            else if(!location.matches("")) {
                GetLocationFromName getLocationFromName = new GetLocationFromName(activity,
                        submitLocation, submitTime, location, data);
                getLocationFromName.execute();
                //GetCoordinates getCoordinates = new GetCoordinates
            }
            else Toast.makeText(getBaseContext(), R.string.enterLocationMessage, Toast.LENGTH_LONG).show();
            }
        });

        //show the selected location for confirmation
        submitTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //gets the data from the Data object
                if(data.jsonData != null) data.location = getLocationFromJson(data.jsonData);
                if(data.location != null) FinalLocation = data.location;

                data.location = FinalLocation;
                txtCoordinates.setVisibility(View.GONE);
                txtLatitude.setVisibility(View.GONE);
                txtLongitude.setVisibility(View.GONE);
                txtLocation.setVisibility(View.GONE);
                enterLocation.setVisibility(View.GONE);
                rawLatitude.setVisibility(View.GONE);
                rawLongitude.setVisibility(View.GONE);
                TextView txtFinalLocation = findViewById(R.id.txtFinalLocation);
                txtFinalLocation.setVisibility(View.VISIBLE);
                submitTime.setVisibility(View.GONE);

                if(data.location.matches(""))
                {
                    data.location = "No named locations nearby";
                }
                txtFinalLocation.setText(String.format("Coordinates: %s, %s, \nClosest Location:\n%s", Coordinates[0], Coordinates[1], data.location));
                btnContinue.setVisibility(View.VISIBLE);


            }
        });

        //go to AddTime and send chosen location data
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] dataToPut = {data.location, Coordinates[0], Coordinates[1], routeName};
                Intent startAddTime = new Intent(getBaseContext(), AddTime.class);
                startAddTime.putExtra("data", dataToPut);
                startActivityForResult(startAddTime, ADDTIME_REQUEST_CODE);
            }
        });
    }

    //extracts the location name from the json from api
    private String getLocationFromJson(String jsonData) {
        String foundLocation = null;
        try{
            JSONObject data = new JSONObject(jsonData);
            JSONArray results = data.getJSONArray("results");
            JSONObject locationData = results.getJSONObject(0);
            foundLocation = locationData.getString("formatted_address");
        }catch(JSONException e){
            e.printStackTrace();
        }
        return foundLocation;
    }

    //When AddTime finishes, data is retrieved and goes straight back to NewRoute with the data
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == ADDTIME_REQUEST_CODE)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                String[] AllData = data.getStringArrayExtra("data");
                Intent returnIntent = new Intent();
                returnIntent.putExtra("data", AllData);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        }
    }
}

//data class holds data retrieved in async tasks
class Data
{
    String location;
    String jsonData;
    String[] forecast;
}