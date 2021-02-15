package com.project.locateme;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ViewLocationActivity extends AppCompatActivity implements OnMapReadyCallback {


    SupportMapFragment mapFrag;
    double latitude;
    double longitude;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_location);

         latitude=getIntent().getDoubleExtra("latitude",0.0);
         longitude=getIntent().getDoubleExtra("longitude",0.0);

         name=getIntent().getStringExtra("name");

        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, ""+latitude, Toast.LENGTH_SHORT).show();
        MarkerOptions markerOptions=new MarkerOptions();
        markerOptions.position(new LatLng(latitude,longitude));
        markerOptions.title(name);
        googleMap.addMarker(markerOptions);
        CameraUpdate cameraUpdate= CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude),10);
        googleMap.animateCamera(cameraUpdate);


    }
}