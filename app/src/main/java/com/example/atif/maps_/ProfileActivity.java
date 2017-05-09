package com.example.atif.maps_;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.ArrayList;

import Modules.NearbyStations;

public class ProfileActivity extends AppCompatActivity {
    private TextView displayName, reputation, netVote;
    private ImageView proPic;
    private Button signOut, editProfile;
    private DatabaseReference mDatabase;
    private FirebaseUser loggedUser;
    ArrayList<NearbyStations> temp2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Bottom Navigation Bar
        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setDefaultTab(R.id.tab_profile);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_planner) {
                    Intent planner = new Intent(getApplicationContext(), MapsActivity.class);
                    startActivity(planner);
                }
                     else if (tabId == R.id.tab_nearby) {
                        Intent nearby = new Intent(getApplicationContext(), NearbyActivity.class);
                        temp2 = MainActivity.stationList;
                         nearby.putExtra("STATION",temp2);
                        startActivity(nearby);
                    } else if (tabId == R.id.tab_schedule) {
                        Intent schedule = new Intent(getApplicationContext(), trainSchedule.class);
                        startActivity(schedule);
                    } else if (tabId == R.id.tab_alerts) {
                        Intent alerts = new Intent(getApplicationContext(), AlertActivity.class);
                        startActivity(alerts);
                    }

            }

        });

        mDatabase = FirebaseDatabase.getInstance().getReference();
        displayName = (TextView) findViewById(R.id.displayName);
        proPic = (ImageView) findViewById(R.id.propic);
        reputation = (TextView) findViewById(R.id.txtView_reputation);
        editProfile= (Button) findViewById(R.id.btn_editProfile);
        netVote = (TextView) findViewById(R.id.txtView_netVote);


        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            //Go to login
            Intent i = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(i);

        }
        else{
            loggedUser = FirebaseAuth.getInstance().getCurrentUser();
        }
         // Reference to an image file in Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://commutr-149323.appspot.com").child("Images").child(loggedUser.getUid());


        if(loggedUser.getProviders().get(0).equals("google.com")){
            Glide.with(ProfileActivity.this)
                    //.using(new FirebaseImageLoader())
                    //.load(storageRef)
                    .load(loggedUser.getPhotoUrl())
                    .into(proPic);
            displayName.setText(loggedUser.getDisplayName());
            editProfile.setVisibility(View.GONE);
        }
        else if(storageRef!=null) {

            Glide.with(ProfileActivity.this)
                    .using(new FirebaseImageLoader())
                    .load(storageRef)
                    .into(proPic);
        }



        //Reading values from DB to display on the profile
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                String uid = loggedUser.getUid();

                //int reputation = dataSnapshot.child("users").child(uid).child("reputation").
                if(!loggedUser.getProviders().get(0).equals("google.com")) {
                    String firstName = dataSnapshot.child("users").child(uid).child("firstName").getValue().toString();
                    String lastName = dataSnapshot.child("users").child(uid).child("lastName").getValue().toString();

                    displayName.setText(firstName+" "+lastName);
                    //reputation.setText(Integer.toString(info.getReputation()));
                }
                String userReputation = dataSnapshot.child("users").child(uid).child("reputation").getValue().toString();
                String userNetVotes= dataSnapshot.child("users").child(uid).child("netVote").getValue().toString();

                reputation.setText(userReputation);
                netVote.setText(userNetVotes);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("dbtest", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addValueEventListener(postListener);


        signOut= (Button) findViewById(R.id.btn_signout);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(com.example.atif.maps_.ProfileActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(com.example.atif.maps_.ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });


    }

}
