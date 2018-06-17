package com.example.grayrc1.travelrouteweatherchecker;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

/*
 * Displays loctions in a saved route and allows user to select one.
 */
public class Route extends AppCompatActivity {
    String routeName;
    SQLiteDatabase routeDb;
    ListView lstLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_route);
        Intent getRouteName = getIntent();
        routeName = getRouteName.getStringExtra("routeName");

        routeDb = openOrCreateDatabase("routeDb", MODE_PRIVATE, null);

        lstLocations = findViewById(R.id.lstRouteLocations);

        setList();

        Button btnDeleteRoute = findViewById(R.id.btnDeleteRoute);
        btnDeleteRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRoute(routeName, routeDb);
            }
        });

        lstLocations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent goToLocation = new Intent(getBaseContext(), Location.class);
                goToLocation.putExtra("locationName", (String)parent.getItemAtPosition(position));

                startActivity(goToLocation);
            }
        });
    }

    private void setList()
    {

        String[] locations = getLocations(routeDb, routeName);
        ArrayAdapter<String> locationsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_selectable_list_item, locations);
        lstLocations.setAdapter(locationsAdapter);
    }

    private void deleteRoute(String routeName, SQLiteDatabase routeDb)
    {
        String deleteStatement = "DELETE FROM route WHERE route_name = '" + routeName + "';";
        routeDb.execSQL(deleteStatement);
        finish();
    }

    private String[] getLocations(SQLiteDatabase routeDb, String routeName)
    {
        String selectQuery = "SELECT location.name FROM route JOIN location " +
                "ON route.route_id = location.route_id " +
                "WHERE route.route_name == '" + routeName + "';";
        Cursor recordSet = routeDb.rawQuery(selectQuery, null);
        int recordCount = recordSet.getCount();
        String[] locations = new String[recordCount];
        int columnIndex = recordSet.getColumnIndex("name");
        recordSet.moveToFirst();
        for(int i = 0; i < recordCount; i++)
        {
            locations[i] = recordSet.getString(columnIndex);
            recordSet.moveToNext();
        }
        return locations;
    }

}
