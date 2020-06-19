package com.example.sharedcfc;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.sharedcfc.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.LatLng;
import com.ibm.cloud.appid.android.api.AppID;
import com.ibm.cloud.appid.android.api.AppIDAuthorizationManager;
import com.ibm.cloud.appid.android.api.AuthorizationException;
import com.ibm.cloud.appid.android.api.AuthorizationListener;
import com.ibm.cloud.appid.android.api.LoginWidget;
import com.ibm.cloud.appid.android.api.tokens.AccessToken;
import com.ibm.cloud.appid.android.api.tokens.IdentityToken;
import com.ibm.cloud.appid.android.api.tokens.RefreshToken;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient;


public class SignInSignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignInSignUpActivity";
    private LatLng userLocation;
    private static final int REQUEST_CODE = 1;
    private AppID appId;
    private BMSClient bmsClient;
    private AppIDAuthorizationManager appIDAuthorizationManager;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private DatabaseHelper databaseHelper;
    private Button btnSignIn,btnSignUp;
    private TextView txtForgotPassword;
    private boolean doubleBackToExitPressedOnce = false;
    private ProgressDialog progress ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signinsignup);
        //Verify for permissions during runtime
        verifyPermissions();
        progress = new ProgressDialog(this);

        //AppId service
        bmsClient = BMSClient.getInstance();
        appId = AppID.getInstance();
        appId.initialize(this,"d1e16955-f793-498f-a8b6-7a8193219904",AppID.REGION_UK);
        this.appIDAuthorizationManager = new AppIDAuthorizationManager(this.appId);
        bmsClient.setAuthorizationManager(appIDAuthorizationManager);
        btnSignIn=(Button) findViewById(R.id.ButtonSignin);
        btnSignUp=(Button) findViewById(R.id.ButtonSignup);
        txtForgotPassword=(TextView) findViewById(R.id.TextForgotPassword);

        //OnClick Listeners for sign in, forgot password and sign up actions
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
        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPasswordAction();
            }
        });

    }

    public void signInAction(){
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        databaseHelper = new DatabaseHelper(this);
        appId.initialize(this, "d1e16955-f793-498f-a8b6-7a8193219904", AppID.REGION_UK);
        this.appIDAuthorizationManager = new AppIDAuthorizationManager(this.appId);

        //AppId widget to login users
        LoginWidget loginWidget = appId.getLoginWidget();
        loginWidget.launch(this, new AuthorizationListener() {
            @Override
            public void onAuthorizationCanceled() {
                Log.d(" Auth","Cancelled");
                progress.dismiss();
            }

            @Override
            public void onAuthorizationFailure(AuthorizationException exception) {
                Log.d(" Auth","Failed");
                progress.dismiss();
            }

            @Override
            public void onAuthorizationSuccess(AccessToken accessToken, IdentityToken identityToken, RefreshToken refreshToken) {
                databaseHelper.deleteInstance();
                String refresh_token;
                refresh_token = null;

                // nullpointer exception (identityToken.getPicture is null)
                try{
                    refresh_token = identityToken.getPicture();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                long returnValue = databaseHelper.insertRecord(identityToken.getEmail(),
                        refreshToken.getRaw(),
                        identityToken.getName(),
                        refresh_token);

                //onAuthorizationSuccess intent user to the navigation activity
                Intent intent = new Intent(SignInSignUpActivity.this, NavBar.class);
                progress.dismiss();
                startActivity(intent);
                finish();
            }
        });
    }
    public void signUpAction(){
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        databaseHelper = new DatabaseHelper(this);
        appId.initialize(this, "d1e16955-f793-498f-a8b6-7a8193219904", AppID.REGION_UK);
        this.appIDAuthorizationManager = new AppIDAuthorizationManager(this.appId);
        LoginWidget loginWidget = AppID.getInstance().getLoginWidget();
        loginWidget.launchSignUp(this, new AuthorizationListener() {

            @Override
            public void onAuthorizationFailure (AuthorizationException exception) {
                Log.d(" Sign Up","Failed");
                progress.dismiss();
            }

            @Override
            public void onAuthorizationCanceled () {
                Log.d(" Sign Up","Cancelled");
                progress.dismiss();
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

                    //onAuthorizationSuccess intent user to GetUserLocation activity
                    Intent intent = new Intent(SignInSignUpActivity.this, PostSignUpActivity.class);
                    progress.dismiss();
                    startActivity(intent);
                    finish();
                } else {
                    progress.dismiss();
                    Toast.makeText(SignInSignUpActivity.this, "Email Verification Required", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void forgotPasswordAction(){
        appId.initialize(this, "d1e16955-f793-498f-a8b6-7a8193219904", AppID.REGION_UK);
        this.appIDAuthorizationManager = new AppIDAuthorizationManager(this.appId);
        LoginWidget loginWidget = AppID.getInstance().getLoginWidget();
        loginWidget.launchForgotPassword(this, new AuthorizationListener() {
            @Override
            public void onAuthorizationFailure (AuthorizationException exception) { Log.d(" Forgot Password ","Exception Occurred"); }

            @Override
            public void onAuthorizationCanceled () { Log.d(" Forgot Password ","Cancelled"); }

            @Override
            public void onAuthorizationSuccess (AccessToken accessToken, IdentityToken identityToken, RefreshToken refreshToken) { Log.d(" Forgot Password ","Successful"); }
        });
    }

    //Press back twice to exit app
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
    private void verifyPermissions(){
        Log.d(TAG, "verifyPermissions: asking user for permissions");
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[1]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[2]) == PackageManager.PERMISSION_GRANTED){
        }else{
            ActivityCompat.requestPermissions(SignInSignUpActivity.this,
                    permissions,
                    REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        verifyPermissions();
    }
}