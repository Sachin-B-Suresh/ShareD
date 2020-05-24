package com.example.sharedcfc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.example.qrcodescanner.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;


public class GetUserLocation extends AppCompatActivity {
    private static final String TAG ="GetUserLocationActivity" ;
    private String firebaseUserToken;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference userRef = database.getReference("Users");
    private DatabaseReference newUserRef = userRef.push();
    private DatabaseHelper databaseHelper;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LatLng userLocation;
    private String[] userDetails = new String[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_user_location);

        //New Users are required to provide location details, which will be stored in firebase
        firebaseSetup();
    }

    public void firebaseSetup(){
        // [START subscribe_topics]
        FirebaseMessaging.getInstance().subscribeToTopic("LocationBasedChannel")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = getString(R.string.msg_subscribed);
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_subscribe_failed);
                        }
                        Log.d(TAG, msg);
                        Toast.makeText(GetUserLocation.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
        // [END subscribe_topics]
        // [START retrieve_current_token]
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        firebaseUserToken = task.getResult().getToken();
                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, firebaseUserToken);
//                        Log.d(TAG, msg);
//                        Toast.makeText(GetUserLocation.this, msg, Toast.LENGTH_SHORT).show();
                        getLocation();
                    }
                });
        // [END retrieve_current_token]
    }

    public void getLocation(){
        databaseHelper = new DatabaseHelper(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if(!databaseHelper.isDatabaseTableEmpty()){
            userDetails = databaseHelper.fetchLocalInstance();
        }
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
                    userEntry.put("token",firebaseUserToken);
                    newUserRef.setValue(userEntry);
//                    Toast.makeText(GetUserLocation.this, "Location "+ location.getLatitude()+location.getLongitude(), Toast.LENGTH_SHORT).show();
                    //Intent user to Navigation activity
                    Intent intent = new Intent(GetUserLocation.this, NavBar.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
