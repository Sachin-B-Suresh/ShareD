package com.example.sharedcfc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.qrcodescanner.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;


public class PostSignUpActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG ="GetUserLocationActivity" ;
    private String firebaseUserToken;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference userRef = database.getReference("Users");
    private DatabaseReference newUserRef = userRef.push();
    private DatabaseHelper databaseHelper;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LatLng userLocation;
    private String[] userDetails = new String[4];
    private GoogleMap mMap;
    private EditText editTextLocality;
    private Button buttonSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_user_location);
        editTextLocality = (EditText) findViewById(R.id.locality);
        buttonSubmit = (Button) findViewById(R.id.submit);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAction();
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //New Users are required to provide location details, which will be stored in firebase
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful()){
                    Location location=task.getResult();
                    userLocation= new LatLng(location.getLatitude(),location.getLongitude());
                    // Add a marker in Sydney, Australia, and move the camera.
                    mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location").draggable(true));
                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(userLocation,14);
                    mMap.moveCamera(update);
                }
            }
        });
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setScrollGesturesEnabled(true);

        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker arg0) {
                // TODO Auto-generated method stub
                Log.d("System out", "onMarkerDragStart..."+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude);
            }

            @SuppressWarnings("unchecked")
            @Override
            public void onMarkerDragEnd(Marker arg0) {
                // TODO Auto-generated method stub
                userLocation= new LatLng(arg0.getPosition().latitude, arg0.getPosition().longitude);

                Log.d("System out", "onMarkerDragEnd..."+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(arg0.getPosition()));
            }

            @Override
            public void onMarkerDrag(Marker arg0) {
                // TODO Auto-generated method stub
                Log.i("System out", "onMarkerDrag...");
            }
        });
    }

    public void submitAction(){
        //Get Subscription Channel
        String channelName = editTextLocality.getText().toString().toLowerCase();
        if(channelName.isEmpty()){
            editTextLocality.setError("Please enter locality");
        }
//            Toast.makeText(GetUserLocation.this, "Please enter locality!", Toast.LENGTH_SHORT).show();
        else{
            channelName= channelName.replaceAll("\\s+","_");
            Log.d("User Input", channelName);
            //Subscribe to  a channel
            firebaseSetup(channelName);
        }
    }

    public void firebaseSetup(final String initialChannelName){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = "fcm_"+initialChannelName;
            String channelName = initialChannelName;
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_HIGH));
        }
        // If a notification message is tapped, any data accompanying the notification
        // message is available in the intent extras. In this sample the launcher
        // intent is fired when the notification is tapped, so any accompanying data would
        // be handled here. If you want a different intent fired, set the click_action
        // field of the notification message to the desired intent. The launcher intent
        // is used when no click_action is specified.
        //
        // Handle possible data accompanying notification message.
        // [START subscribe_topics]
        FirebaseMessaging.getInstance().subscribeToTopic(initialChannelName)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed to " + initialChannelName;
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_subscribe_failed);
                        }
                        Log.d(TAG, msg);
                        Toast.makeText(PostSignUpActivity.this, msg, Toast.LENGTH_SHORT).show();
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
                        writeToFirebase(initialChannelName);
                    }
                });
        // [END retrieve_current_token]
    }

    public void writeToFirebase(String initialChannel){
        databaseHelper = new DatabaseHelper(this);
        if(!databaseHelper.isDatabaseTableEmpty()){
            userDetails = databaseHelper.fetchLocalInstance();
        }
        HashMap<String, Object> userEntry = new HashMap<>();
        userEntry.put("email", userDetails[0]);
        userEntry.put("name", userDetails[2]);
        userEntry.put("location",userLocation);
        userEntry.put("token",firebaseUserToken);
        userEntry.put("topic",initialChannel);
        newUserRef.setValue(userEntry);
        //Intent user to Navigation activity
        Intent intent = new Intent(PostSignUpActivity.this, NavBar.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
