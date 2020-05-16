package com.example.qrcodescanner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    Button btnScan;
    Button instruction;
    TextView txtLogin;
    private final String LOGTAG="Scan QrCode";
    private static final int REQUEST_CODE = 1;

    DatabaseHelper databaseHelper;
    private static final int REQUEST_CODE_QR_SCAN = 101;

    private static final String TAG = "SearchActivity";
    private static int SLPASH_TIME_OUT=2000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bmsClient = BMSClient.getInstance();
        appId = AppID.getInstance();
        appId.initialize(this, "d1e16955-f793-498f-a8b6-7a8193219904", AppID.REGION_UK);
        databaseHelper = new DatabaseHelper(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                verifyPermissions();
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

            }},SLPASH_TIME_OUT);

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
            ActivityCompat.requestPermissions(MainActivity.this,
                    permissions,
                    REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        verifyPermissions();
    }
}