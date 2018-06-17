package com.example.grayrc1.travelrouteweatherchecker;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/*
 * Displays saved routes and allows user to select one.
 */
public class RouteList extends AppCompatActivity {

    ListView lstSavedRoutes;
    SQLiteDatabase routeDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_list);

        lstSavedRoutes = findViewById(R.id.lstSavedRoutes);
        routeDb = openOrCreateDatabase("routeDb", MODE_PRIVATE, null);

        setList();

        lstSavedRoutes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent goToRoute = new Intent(getBaseContext(), Route.class);
                goToRoute.putExtra("routeName", (String)parent.getItemAtPosition(position));
                startActivity(goToRoute);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("DELETING", "are we here?");
        setList();
    }

    private void setList()
    {
        String[] routes = getListFromDb(routeDb);

        ArrayAdapter<String> routesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, routes);

        lstSavedRoutes.setAdapter(routesAdapter);
    }

    private String[] getListFromDb(SQLiteDatabase routeDb)
    {
        String selectQuery = "SELECT route_name from route";
        Cursor recordSet = routeDb.rawQuery(selectQuery, null);
        int recordCount = recordSet.getCount();
        String[] routes = new String[recordCount];
        int routeNameIndex = recordSet.getColumnIndex("route_name");
        recordSet.moveToFirst();
        for (int i = 0; i < recordCount; i++)
        {
            routes[i] = recordSet.getString(routeNameIndex);
            recordSet.moveToNext();
        }
        return routes;
    }
}
