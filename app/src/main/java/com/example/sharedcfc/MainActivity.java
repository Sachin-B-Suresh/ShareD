package com.example.sharedcfc;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.sharedcfc.R;
import com.ibm.cloud.appid.android.api.AppID;
import com.ibm.cloud.appid.android.api.AppIDAuthorizationManager;
import com.ibm.cloud.appid.android.api.AuthorizationException;
import com.ibm.cloud.appid.android.api.AuthorizationListener;
import com.ibm.cloud.appid.android.api.tokens.AccessToken;
import com.ibm.cloud.appid.android.api.tokens.IdentityToken;
import com.ibm.cloud.appid.android.api.tokens.RefreshToken;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient;

public class MainActivity extends AppCompatActivity {
    private AppID appId;
    private BMSClient bmsClient;
    private AppIDAuthorizationManager appIDAuthorizationManager;
    private final String LOGTAG="Scan QrCode";
    private static final int REQUEST_CODE = 1;
    private DatabaseHelper databaseHelper;
    private static final int REQUEST_CODE_QR_SCAN = 101;
    private static final String TAG = "SearchActivity";
    private Button btnRetry;
    private boolean doubleBackToExitPressedOnce = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnRetry = (Button) findViewById(R.id.RetryButton);
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retryAction();
            }
        });

        if(isNetworkConnected()){
            btnRetry.setVisibility(View.GONE);
            //AppId service
            bmsClient = BMSClient.getInstance();
            appId = AppID.getInstance();
            appId.initialize(this, "d1e16955-f793-498f-a8b6-7a8193219904", AppID.REGION_UK);

            //Local sqlite database helper
            databaseHelper = new DatabaseHelper(this);

            //Search for local instance of logged in user to get refresh token
            if(databaseHelper.isDatabaseTableEmpty()){
                Toast.makeText(MainActivity.this, "No Local Instance, Please Login", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, SignInSignUpActivity.class);
                startActivity(intent);
                finish();
            }
            else {
                Toast.makeText(MainActivity.this, "Local Instance Found", Toast.LENGTH_SHORT).show();
                String[] userInstance=databaseHelper.fetchLocalInstance();
                String refreshTokenString= userInstance[1];

                //Use the refresh token to sign in the user
                AppID.getInstance().signinWithRefreshToken(getApplicationContext(), refreshTokenString, new AuthorizationListener() {
                    @Override
                    public void onAuthorizationFailure(AuthorizationException exception) {
                        databaseHelper.deleteInstance();
                        Intent intent = new Intent(MainActivity.this, SignInSignUpActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onAuthorizationCanceled() {
                        databaseHelper.deleteInstance();
                        Intent intent = new Intent(MainActivity.this, SignInSignUpActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onAuthorizationSuccess(AccessToken accessToken, IdentityToken identityToken, RefreshToken refreshToken) {
                        Intent intent = new Intent(MainActivity.this, NavBar.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        }
        else {
            Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void retryAction() {
        finish();
        startActivity(getIntent());
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
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
}