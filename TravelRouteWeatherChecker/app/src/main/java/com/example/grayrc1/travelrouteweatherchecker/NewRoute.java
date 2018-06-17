package com.example.grayrc1.travelrouteweatherchecker;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

/*
 * Allows user to name a route and add locations to route
 *
 */
public class NewRoute extends AppCompatActivity {

    String routeName;
    SQLiteDatabase routeDb;
    Button btnAddLocation;
    Button btnSaveRoute;
    Button btnRouteName;
    EditText edtRouteName;
    TextView txtRouteName;
    ListView lstLocations;

    String[] AllData; //Contains location data passed back from location class

    static final int ADDLOCATION_REQUEST_CODE= 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_route);

        //Main app database
        routeDb = openOrCreateDatabase("routeDb",MODE_PRIVATE, null);
        createDb(); //creates the database tables if they don't already exist

        txtRouteName = findViewById(R.id.txtRouteName);
        btnAddLocation = findViewById(R.id.btnAddLocation);
        btnSaveRoute = findViewById(R.id.btnRouteComplete);
        btnRouteName = findViewById(R.id.btnRouteName);
        edtRouteName = findViewById(R.id.edtRouteName);
        lstLocations = findViewById(R.id.lstLocations);

        //creates routeDb.route' entry based on input and changes the buttons around
        btnRouteName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                routeName = edtRouteName.getText().toString();
                String insertStatement = "INSERT INTO route VALUES(null, '" + routeName + "');";
                routeDb.execSQL(insertStatement);
                txtRouteName.setText(routeName);
                enterLocationMode(); //hides the enter route name button and shows locations and location buttons
                showLocationsList(); //displays route locations from database
            }
        });

        //go to AddLocation
        btnAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startAddLocation = new Intent(getBaseContext(), AddLocation.class);
                startAddLocation.putExtra("routeName", routeName);
                //startActivity(startAddLocation);
                startActivityForResult(startAddLocation, ADDLOCATION_REQUEST_CODE);
            }
        });

        //goes to RouteList
        btnSaveRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToRouteList = new Intent(getBaseContext(), RouteList.class);
                startActivity(goToRouteList);
            }
        });
    }

    //When user goes back from Location
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == ADDLOCATION_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                AllData = data.getStringArrayExtra("data"); //chosen location and times retrieved
                enterLocationMode(); //route already named, so show location buttons
                routeName = AllData[5];
                txtRouteName.setText(routeName); //make the title the route name
                addDataToDb(AllData); //save the location data to the database
                showLocationsList();
            }
        }
    }

    //displays all locations in the route
    private void showLocationsList()
    {
        String[] locationNames = getLocations();
        ArrayAdapter<String> locationNameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, locationNames);
        lstLocations.setAdapter(locationNameAdapter);
    }

    //queries the database for locations in route
    private String[] getLocations()
    {
        String selectQuery = "SELECT name FROM location JOIN route ON " +
                "location.route_id = route.route_id WHERE " +
                "route.route_name = '" +
                routeName + "';";
        Cursor recordSet = routeDb.rawQuery(selectQuery, null);
        int recordCount = recordSet.getCount();
        String[] locationNames = new String[recordCount];
        int nameIndex = recordSet.getColumnIndex("name");
        recordSet.moveToFirst();
        for (int i = 0; i < recordCount; i++)
        {
            locationNames[i] = recordSet.getString(nameIndex);
            Log.d("SQL", locationNames[i]);
            recordSet.moveToNext();
        }
        return locationNames;
    }

    //creates the database if it doesn't already exist
    private void createDb()
    {
        //run these lines after making any changes to the database schema
        //routeDb.execSQL("DROP TABLE IF EXISTS route");
        //routeDb.execSQL("DROP TABLE IF EXISTS location");
        String createQuery = "CREATE TABLE IF NOT EXISTS route(" +
                "route_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "route_name TEXT NOT NULL);";
        routeDb.execSQL(createQuery);

        createQuery = "CREATE TABLE IF NOT EXISTS location(" +
                "location_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "route_id INTEGER NOT NULL, " +
                "name TEXT NOT NULL, " +
                "latitude TEXT NOT NULL, " +
                "longitude TEXT NOT NULL, " +
                "temperature TEXT NOT NULL, " +
                "forecast TEXT NOT NULL, " +
                "time TEXT NOT NULL, " +
                "FOREIGN KEY (route_id) REFERENCES route(route_id)" +
                "ON DELETE CASCADE ON UPDATE CASCADE);";
        routeDb.execSQL(createQuery);
    }

    //makes the add route name buttons invisible and the add location buttons visible
    private void enterLocationMode()
    {
        edtRouteName.setVisibility(View.GONE);
        btnRouteName.setVisibility(View.GONE);
        btnAddLocation.setVisibility(View.VISIBLE);
        btnSaveRoute.setVisibility(View.VISIBLE);
        lstLocations.setVisibility(View.VISIBLE);

    }

    //add the user chosen location and time data to routeDb.location
    private void addDataToDb(String[] data)
    {
        //get route_id
        String selectQuery = "SELECT * FROM route WHERE route_name = '" + routeName + "';";
        Log.d("SQL", selectQuery);
        Cursor recordSet = routeDb.rawQuery(selectQuery, null);
        Log.d("SQL", "Do we even get here");
        Log.d("SQL", recordSet.toString());
        int routeIdIndex = recordSet.getColumnIndex("route_id");
        Log.d("SQL", Integer.toString(routeIdIndex));
        recordSet.moveToFirst();
        int routeId = recordSet.getInt(routeIdIndex);

        String insertStatement = "INSERT INTO location VALUES(null, " +
                routeId + ", '" + data[0] +
                "', '" + data[1] + "', '" + data[2] + "', '" + data[3] + "', '" + data[4] + "', '" + data[6] + "');";
        routeDb.execSQL(insertStatement);
        recordSet.close();
    }

}
