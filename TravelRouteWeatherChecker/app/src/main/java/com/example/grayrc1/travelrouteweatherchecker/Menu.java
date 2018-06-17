package com.example.grayrc1.travelrouteweatherchecker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/*
 * Start Screen and initial menu
 */
public class Menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        final Intent startNewRoute = new Intent(Menu.this, NewRoute.class);

        Button btnStartNew = findViewById(R.id.btnStartNew);
        Button btnSelectSaved = findViewById(R.id.btnDeleteRoute);
        btnSelectSaved.bringToFront();
        btnStartNew.bringToFront();

        btnStartNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(startNewRoute);
            }
        });

        btnSelectSaved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToSavedRoutes = new Intent(getBaseContext(), RouteList.class);
                startActivity(goToSavedRoutes);
            }
        });
    }
}
