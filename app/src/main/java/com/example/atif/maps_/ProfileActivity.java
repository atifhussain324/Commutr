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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alexzh.circleimageview.CircleImageView;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

public class ProfileActivity extends AppCompatActivity {
    private TextView displayName, reputation;
    private ImageView proPic;
    private Button signOut, editProfile;
    private String name;
    private DatabaseReference mDatabase;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        mDatabase = FirebaseDatabase.getInstance().getReference();

        displayName = (TextView) findViewById(R.id.displayName);
        proPic = (ImageView) findViewById(R.id.propic);
        reputation = (TextView) findViewById(R.id.txtView_reputation);

        user = FirebaseAuth.getInstance().getCurrentUser();



        // Reference to an image file in Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://commutr-149323.appspot.com").child("Images").child(user.getUid());

        // Load the image using Glide
        if(storageRef!=null) {
            Glide.with(ProfileActivity.this)
                    .using(new FirebaseImageLoader())
                    .load(storageRef)
                    .into(proPic);
        }




        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                String uid = user.getUid();
                com.example.atif.maps_.UserInfo info = dataSnapshot.child("users").child(uid).getValue(com.example.atif.maps_.UserInfo.class);

                if(info!=null) {
                    displayName.setText(info.getFirstName()+" "+info.getLastName());
                    reputation.setText(Integer.toString(info.getReputation()));
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("dbtest", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addValueEventListener(postListener);

        displayName.setText(name);

        signOut= (Button) findViewById(R.id.btn_signout);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(com.example.atif.maps_.ProfileActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


        editProfile= (Button) findViewById(R.id.btn_editProfile);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(com.example.atif.maps_.ProfileActivity.this, EditProfileActivity.class);
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
