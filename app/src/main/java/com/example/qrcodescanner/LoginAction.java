package com.example.qrcodescanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.ibm.cloud.appid.android.api.AppID;
import com.ibm.cloud.appid.android.api.AppIDAuthorizationManager;
import com.ibm.cloud.appid.android.api.tokens.AccessToken;
import com.ibm.cloud.appid.android.api.tokens.IdentityToken;
import com.ibm.cloud.appid.android.api.tokens.RefreshToken;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient;
import com.ibm.cloud.appid.android.api.AuthorizationException;
import com.ibm.cloud.appid.android.api.AuthorizationListener;
import com.ibm.cloud.appid.android.api.LoginWidget;

public class LoginAction extends AppCompatActivity {

    private AppID appId;
    private BMSClient bmsClient;
    private AppIDAuthorizationManager appIDAuthorizationManager;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_action);
        databaseHelper = new DatabaseHelper(this);

        bmsClient = BMSClient.getInstance();
        appId = AppID.getInstance();
        appId.initialize(this, "d1e16955-f793-498f-a8b6-7a8193219904", AppID.REGION_UK);

        this.appIDAuthorizationManager = new AppIDAuthorizationManager(this.appId);

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
// nullpointer exception (refreshToken is null)
                long returnValue = databaseHelper.insertRecord(identityToken.getEmail(),"null",identityToken.getName());
                Log.d("Email",identityToken.getEmail());
                Log.d("Name",identityToken.getName());
                Log.d("return value", String.valueOf(returnValue));
                Intent intent = new Intent(LoginAction.this, NavBar.class);
                startActivity(intent);
            }
        });
    }
}
