package com.example.sharedcfc;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.sharedcfc.R;
import com.google.android.material.navigation.NavigationView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class NavBar extends AppCompatActivity{

    private static final String TAG ="MainActivity" ;
    private AppBarConfiguration mAppBarConfiguration;
    private boolean doubleBackToExitPressedOnce = false;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);

        databaseHelper = new DatabaseHelper(this);
        String[] userDetails=databaseHelper.fetchLocalInstance();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_requests, R.id.nav_call_in_a_favour,
                R.id.nav_my_requests, R.id.nav_my_contributions, R.id.nav_settings,
                R.id.nav_help, R.id.nav_sign_out)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                int menuId = destination.getId();
                switch (menuId){
                    case R.id.nav_requests:
                        break;
                    case R.id.nav_sign_out:
//                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                switch (which){
//                                    case DialogInterface.BUTTON_POSITIVE:
                                        //Yes button clicked
                                        Toast.makeText(NavBar.this,"Signed Out",Toast.LENGTH_SHORT).show();
                                        databaseHelper.deleteInstance();
                                        Intent intent = new Intent(NavBar.this, SplashScreen.class);
                                        startActivity(intent);
                                        finish();
//                                        break;
//
//                                    case DialogInterface.BUTTON_NEGATIVE:
//                                        //No button clicked
//                                        break;
//                                }
//                            }
//                        };
//                        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
//                                .setNegativeButton("No", dialogClickListener).show();
                    default:
                        break;
                }
            }
        });

        //changing profile info
        NavigationView mNavigationView = findViewById(R.id.nav_view);
        View headerView = mNavigationView.getHeaderView(0);

        // get user name and email textViews
        TextView userName = headerView.findViewById(R.id.name);
        TextView userEmail = headerView.findViewById(R.id.email);

        //image needs to be downloaded first (make use of glide )
        ImageView userImage = headerView.findViewById(R.id.imageView);

        //Set user name, email, picture in the navigation drawer
        userName.setText(userDetails[2]);
        userEmail.setText(userDetails[0]);
        if(userDetails[3]!=null)
            Glide.with(this).load(userDetails[3]).apply(RequestOptions.circleCropTransform()).into(userImage);



        // [START handle_data_extras]
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }
        // [END handle_data_extras]
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
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
