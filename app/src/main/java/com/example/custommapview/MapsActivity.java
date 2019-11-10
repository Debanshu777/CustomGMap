package com.example.custommapview;

import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Geocoder geocoder;
    Switch dark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        dark=findViewById(R.id.switch1);


    }
    @Override
    public void onMapReady(final GoogleMap googleMap) {

        mMap = googleMap;
        geocoder = new Geocoder(this, Locale.getDefault());
        final Marker[] marker = new Marker[1];

        dark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                        try {
                            // Customise the styling of the base map using a JSON object defined
                            // in a raw resource file.
                            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.mapstyle));

                            if (!success) {
                                Log.e("MapActivity", "Style parsing failed.");
                            }
                        } catch (Resources.NotFoundException e) {
                            Log.e("MapActivity", "Can't find style. Error: ", e);
                        }

                }
                else {
                    try {
                        boolean success = googleMap.setMapStyle(
                                MapStyleOptions.loadRawResourceStyle(
                                        getApplicationContext(), R.raw.mystyle2));

                        if (!success) {
                            Log.e("MapActivity", "Style parsing failed.");
                        }
                    } catch (Resources.NotFoundException e) {
                        Log.e("MapActivity", "Can't find style. Error: ", e);
                    }
                }
            }
        });
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng arg0) {
                // TODO Auto-generated method stub
                Log.d("arg0", arg0.latitude + "-" + arg0.longitude);
                marker[0].remove();
                LatLng newLoc = new LatLng(arg0.latitude, arg0.longitude);
                marker[0] =mMap.addMarker(new MarkerOptions().position(newLoc).title(arg0.toString()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(newLoc));
            }
        });
    }
}
