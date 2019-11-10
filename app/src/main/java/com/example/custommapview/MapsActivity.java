package com.example.custommapview;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks
    ,GoogleApiClient.OnConnectionFailedListener,LocationListener
    {

    private GoogleMap mMap;
    private  GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastLoaction;
    private Marker currentLocationMarker;
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
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION )!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

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


    }
        protected synchronized void buildGoogleApiClient()
        {
            client=new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
            client.connect();
        }

        @Override
        public void onLocationChanged(Location location) {
          lastLoaction=location;
          if(currentLocationMarker!= null)
          {
              currentLocationMarker.remove();
          }
        }

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            locationRequest=new LocationRequest();
            locationRequest.setInterval(1000);
            locationRequest.setFastestInterval(1000);
            locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(client,locationRequest,this);
        }

        @Override
        public void onConnectionSuspended(int i) {

        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        }
    }
