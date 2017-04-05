package com.example.atif.maps_;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.formats.NativeAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

public class ProfileActivity extends AppCompatActivity {
    private TextView displayName;
    private ImageView proPic;
    private Button signOut;
    String name;
    Uri photoUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        displayName = (TextView) findViewById(R.id.displayName);
        proPic = (ImageView) findViewById(R.id.propic);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                // Id of the provider (ex: google.com)
                String providerId = profile.getProviderId();

                // UID specific to the provider
                String uid = profile.getUid();

                // Name, email address, and profile photo Url
                name = profile.getDisplayName();
                String email = profile.getEmail();
                photoUrl = profile.getPhotoUrl();

            }
        }
        //Bitmap myImg = BitmapFactory.decodeFile(photoUrl.getPath());

        displayName.setText(name);
        signOut= (Button) findViewById(R.id.btn_signout);
        //proPic.setImageURI(photoUrl);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(com.example.atif.maps_.ProfileActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


        //Bottom Navigation Bar
        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_planner) {
                    Intent planner = new Intent(ProfileActivity.this, MapsActivity.class);
                    startActivity(planner);
                } else if (tabId == R.id.tab_nearby) {
                    Intent offlineMap = new Intent(ProfileActivity.this, offMap.class);
                    startActivity(offlineMap);
                } /*else if (tabId == R.id.tab_schedule) {
                    Intent schedule = new Intent(MapsActivity.this, trainSchedule.class);
                    startActivity(schedule);
                } */ else if (tabId == R.id.tab_alerts) {
                    Intent alerts = new Intent(ProfileActivity.this, AlertActivity.class);
                    startActivity(alerts);
                } else if (tabId == R.id.tab_profile) {
                    Intent setting = new Intent(ProfileActivity.this, ProfileActivity.class);
                    startActivity(setting);
                }
            }

        });

    }

}
