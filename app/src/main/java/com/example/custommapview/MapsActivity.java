package com.example.custommapview;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.security.Permission;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks
    ,GoogleApiClient.OnConnectionFailedListener,LocationListener
    {

    private GoogleMap mMap;
    private  GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastLoaction;
    private Marker currentLocationMarker;
    public  static final int REQUEST_CODE=99;
    EditText search_bar;
    ImageButton search_button;
    Switch dark;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.M)
        {
            checkLocationPermission();
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        dark=findViewById(R.id.switch1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            switch (requestCode) {
                case REQUEST_CODE:
                    if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {

                        //permission granted
                        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
                        {
                            if(client==null)
                            {
                                buildGoogleApiClient();
                            }
                            mMap.setMyLocationEnabled(true);
                        }
                    }
                    else//permission denied
                    {
                        Toast.makeText(this, "Give Permissions", Toast.LENGTH_SHORT).show();
                    }
                    return;

            }

        }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        mMap = googleMap;
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.setMyLocationEnabled(true);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION )!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();

        }
        search_bar=findViewById(R.id.search_bar);
        search_button=findViewById(R.id.search_button);
        final String locationAdd=search_bar.getText().toString();
        final List<Address>[] addressList = new List[]{null};
        final Geocoder geocoder=new Geocoder(this);
        final MarkerOptions mo=new MarkerOptions();
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!locationAdd.equals(""))
                {
                    try {
                        addressList[0] =geocoder.getFromLocationName(locationAdd,5);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    for(int i = 0; i< addressList[0].size(); i++)
                    {
                        Address myAddress= addressList[0].get(i);
                        LatLng latLng=new LatLng(myAddress.getLatitude(),myAddress.getLongitude());
                        mo.position(latLng);
                        mo.title("search Location");
                        mMap.addMarker(mo);
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    }
                }
            }
        });

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
          LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
          MarkerOptions markerOptions=new MarkerOptions();
          markerOptions.position(latLng);
          markerOptions.title("Current Location");
          markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
          currentLocationMarker=mMap.addMarker(markerOptions);
          mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
          mMap.animateCamera(CameraUpdateFactory.zoomBy(10));
          if(client!=null)
          {
              LocationServices.FusedLocationApi.removeLocationUpdates(client,this);
          }
        }
        public boolean checkLocationPermission()
        {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION )!= PackageManager.PERMISSION_GRANTED)
            {
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
                {
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
                }
                else
                {
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
                }
                return false;
            }
            else {
                return true;
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
