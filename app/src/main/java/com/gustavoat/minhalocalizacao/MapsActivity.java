package com.gustavoat.minhalocalizacao;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker myPosition;
    private static final int ACCESS_FINE_LOCATION_REQUEST = 1;
    TextView txtLatLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        txtLatLon = findViewById(R.id.textViewLatitude);
        txtLatLon.setText(R.string.latitude_label);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        requestPermissions();

        myPosition = null;
    }

    public void requestPermissions(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == ACCESS_FINE_LOCATION_REQUEST) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startPositionService();
            } else {
                Toast.makeText(this, getText(R.string.error_permission),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public void startPositionService(){
        try {
            LocationManager locationmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            LocationListener locationlistener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                     refreshPosition(location);
                }
            };
            locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    0, 0, locationlistener);
        } catch (SecurityException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void refreshPosition(Location location){
        LatLng newPosition = new LatLng(location.getLatitude(), location.getLongitude());
        if(myPosition == null){
            myPosition = mMap.addMarker(new MarkerOptions()
                    .position(newPosition)
                    .title(getString(R.string.you_are_here)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newPosition, 16));
        }else {
            myPosition.setPosition(newPosition);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(newPosition));
        }
        String latitude = String.format("%1$s %2$.4f\n%3$s %4$.4f",
                getText(R.string.latitude_label),
                newPosition.latitude,
                getText(R.string.longitude_label),
                newPosition.longitude);
        txtLatLon.setText(latitude);
    }
}