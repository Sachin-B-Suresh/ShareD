package com.example.sharedcfc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.qrcodescanner.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class GetUserLocation extends AppCompatActivity {
    String name,email,lat,lng;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userRef = database.getReference("Users");
    DatabaseReference newUserRef = userRef.push();
    DatabaseHelper databaseHelper;
    private FusedLocationProviderClient fusedLocationProviderClient;
    LatLng userLocation;
    String[] userDetails = new String[4];



    private GoogleMap mMap;
    Button btn;
    private final static int PLACE_PICKER_REQUEST = 999;
    private final static int LOCATION_REQUEST_CODE = 23;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_user_location);
        databaseHelper = new DatabaseHelper(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if(!databaseHelper.isDatabaseTableEmpty())
            userDetails = databaseHelper.fetchLocalInstance();

        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful()){
                    Location location=task.getResult();
                    userLocation= new LatLng(location.getLatitude(),location.getLongitude());
                    HashMap<String, Object> userEntry = new HashMap<>();
                    userEntry.put("email", userDetails[0]);
                    userEntry.put("name", userDetails[2]);
                    userEntry.put("location",userLocation);
                    newUserRef.setValue(userEntry);
                    Toast.makeText(GetUserLocation.this, "Location "+ location.getLatitude()+location.getLongitude(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(GetUserLocation.this, NavBar.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}