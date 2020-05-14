package com.example.qrcodescanner;

import android.Manifest;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.ibm.cloud.appid.android.api.AppID;
import com.ibm.cloud.appid.android.api.AppIDAuthorizationManager;
import com.ibm.cloud.appid.android.api.AuthorizationException;
import com.ibm.cloud.appid.android.api.AuthorizationListener;
import com.ibm.cloud.appid.android.api.LoginWidget;
import com.ibm.cloud.appid.android.api.tokens.AccessToken;
import com.ibm.cloud.appid.android.api.tokens.IdentityToken;
import com.ibm.cloud.appid.android.api.tokens.RefreshToken;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient;

public class SigninSignupActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1;
    private AppID appId;
    private BMSClient bmsClient;
    private AppIDAuthorizationManager appIDAuthorizationManager;
    Button btnSignIn,btnSignUp;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signinsignup);

        //Below code is to get users current location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if(verifyPermissions()){
           // getLastKnownLocation();
        }
        bmsClient = BMSClient.getInstance();
        appId = AppID.getInstance();
        appId.initialize(this,"d1e16955-f793-498f-a8b6-7a8193219904",AppID.REGION_UK);

        this.appIDAuthorizationManager = new AppIDAuthorizationManager(this.appId);
        bmsClient.setAuthorizationManager(appIDAuthorizationManager);
        btnSignIn=(Button) findViewById(R.id.ButtonSignin);
        btnSignUp=(Button) findViewById(R.id.ButtonSignup);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInAction();
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpAction();
            }
        });

    }
    private boolean verifyPermissions(){
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[1]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[2]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[3]) == PackageManager.PERMISSION_GRANTED){
            return  true;
        }else{
            ActivityCompat.requestPermissions(SigninSignupActivity.this,
                    permissions,
                    REQUEST_CODE);
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        verifyPermissions();
    }
    //Users Current Location
    private void getLastKnownLocation(){
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful()){
                    Location location=task.getResult();
                    LatLng userLocation= new LatLng(location.getLatitude(),location.getLongitude());
                    Log.d("Map latlng location",location.getLatitude() + " and " + location.getLongitude());
                }
            }
        });
    }

    public void signInAction(){
        databaseHelper = new DatabaseHelper(this);
        appId.initialize(this, "d1e16955-f793-498f-a8b6-7a8193219904", AppID.REGION_UK);
        this.appIDAuthorizationManager = new AppIDAuthorizationManager(this.appId);
        //If Cloud Directory is set as default, then you need not use widgets
        LoginWidget loginWidget = appId.getLoginWidget();
        loginWidget.launch(this, new AuthorizationListener() {
            @Override
            public void onAuthorizationCanceled() {
                Log.d(" Auth","Cancelled");
            }

            @Override
            public void onAuthorizationFailure(AuthorizationException exception) {
                Log.d(" Auth","Failed");
            }

            @Override
            public void onAuthorizationSuccess(AccessToken accessToken, IdentityToken identityToken, RefreshToken refreshToken) {
                databaseHelper.deleteInstance();
                String refresh_token;
                refresh_token = null;
// nullpointer exception (identityToken.getEmail() is null)
                try{
                    refresh_token = identityToken.getPicture();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                long returnValue = databaseHelper.insertRecord(identityToken.getEmail(),
                        refreshToken.getRaw(),
                        identityToken.getName(),
                        refresh_token);
                Log.d("Name",identityToken.getName());
                Intent intent = new Intent(SigninSignupActivity.this, NavBar.class);
                startActivity(intent);
                finish();
            }
        });
    }
    public void signUpAction(){
        databaseHelper = new DatabaseHelper(this);
        appId.initialize(this, "d1e16955-f793-498f-a8b6-7a8193219904", AppID.REGION_UK);
        this.appIDAuthorizationManager = new AppIDAuthorizationManager(this.appId);
        LoginWidget loginWidget = AppID.getInstance().getLoginWidget();
        loginWidget.launchSignUp(this, new AuthorizationListener() {

            @Override
            public void onAuthorizationFailure (AuthorizationException exception) {
                Log.d(" Sign Up","Failed");
            }

            @Override
            public void onAuthorizationCanceled () {
                Log.d(" Sign Up","Cancelled");
            }

            @Override
            public void onAuthorizationSuccess (AccessToken accessToken, IdentityToken identityToken, RefreshToken refreshToken) {
                if (accessToken != null && identityToken != null) {
                    databaseHelper.deleteInstance();
                    String refresh_token=null;
                    if(refreshToken!= null)
                        refresh_token=refreshToken.getRaw();
                    long returnValue = databaseHelper.insertRecord(identityToken.getEmail(),
                            refresh_token,
                            identityToken.getName(),
                            null);
                    Intent intent = new Intent(SigninSignupActivity.this, NavBar.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SigninSignupActivity.this, "Email Verification Required", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

