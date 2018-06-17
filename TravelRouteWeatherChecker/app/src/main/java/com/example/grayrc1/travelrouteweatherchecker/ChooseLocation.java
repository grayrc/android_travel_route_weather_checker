package com.example.grayrc1.travelrouteweatherchecker;


import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
    This class shows a dialog box with matches to the users location input and allows the user to
    choose the correct one.
 */
public class ChooseLocation extends DialogFragment
{
    String location; //option the user selects

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance)
    {
        View dialogView = inflater.inflate(R.layout.fragment_choose_location, container);
        Bundle bundle = getArguments();

        //the json data from the openmap.org query on the user entered location
        final String response = bundle.getString("response");
        String[] locations = getPossibleLocations(response); // list of locations from the json data
        ListView locationsList = dialogView.findViewById(R.id.lstPossibleLocations);
        ArrayAdapter<String> locationsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, locations);
        locationsList.setAdapter(locationsAdapter);

        //when the user selects an item, coordinates are found, name and coordinates are sent back to AddLocation,
        //and the dialog box is closed
        locationsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                location = (String)parent.getItemAtPosition(position);
                AddLocation addLocation = (AddLocation)getActivity();
                addLocation.FinalLocation = location;
                addLocation.Coordinates = getCoordinates(position, response);
                dismiss();
            }
        });
        return dialogView;
    }

    //pulls coordinates from the json data for the given position in the json array
    private String[] getCoordinates(int position, String response)
    {
        String[] coordinates = new String[2];
        try{
            JSONArray places = new JSONArray(response);
            JSONObject place = places.getJSONObject(position);
            coordinates[0] = place.getString("lat");
            coordinates[1] = place.getString("lon");

        }catch (JSONException e){
            e.printStackTrace();
        }
        return coordinates;
    }

    //gets an array of all the locations in the json data
    private String[] getPossibleLocations(String jsonData) {
        String[] locations = null;
        try {
            JSONArray places = new JSONArray(jsonData);
            locations = new String[places.length()];
            for(int i = 0; i < places.length(); i++)
            {
                JSONObject place = places.getJSONObject(i);
                locations[i] = place.getString("display_name");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return locations;
    }
}
