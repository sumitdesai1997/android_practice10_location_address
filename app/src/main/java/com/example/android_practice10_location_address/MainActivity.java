package com.example.android_practice10_location_address;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // 2nd step - declaring location Manager and location listener
    LocationManager locationManager;
    LocationListener locationListener;

    public static final String TAG = "MainActivity";
    public static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 3rd step - instantiate locationManager and locationListener
        locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Log.i(TAG,"onLocationChanged: " + location);
                updateLocationInfo(location);
            }
        };

        // 4th step - request for the permission in onCreate method if permission in not already granted, if permission is granted request for updated location and show those details on screen
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        } else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, locationListener);

            Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation != null){
                updateLocationInfo(lastLocation);
            }
        }
    }

    // 5th step - after granting or denying permissions, the following method will be executed
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            startListening();
        }
    }

    private void startListening() {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    // showing the details of location on screen
    private void updateLocationInfo(Location location) {
        TextView latText = findViewById(R.id.lat_text);
        TextView lngText = findViewById(R.id.lng_text);
        TextView accText = findViewById(R.id.acc_text);
        TextView altText = findViewById(R.id.alt_text);
        TextView addressText = findViewById(R.id.address_text);

        latText.setText("Latitude: " + location.getLatitude());
        lngText.setText("Longitude: " + location.getLongitude());
        accText.setText("Accuracy: " + location.getAccuracy());
        altText.setText("Altitude: " + location.getAltitude());

        String address = "Could not find the address";

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try{
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if(addressList != null  && addressList.size() > 0){
                address = "\n";

                // street name
                if (addressList.get(0).getThoroughfare() != null)
                    address += addressList.get(0).getThoroughfare() + "\n";
                if (addressList.get(0).getLocality() != null)
                    address += addressList.get(0).getLocality() + " ";
                if (addressList.get(0).getPostalCode() != null)
                    address += addressList.get(0).getPostalCode() + " ";
                if (addressList.get(0).getAdminArea() != null)
                    address += addressList.get(0).getAdminArea();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        addressText.setText("Address: " + address);
    }
}