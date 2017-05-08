package com.example.atif.maps_;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.ArrayList;

import Modules.NearbyStations;
import Modules.Recycler_Stations_Adapter;

/**
 * Created by Atif on 5/7/17.
 */

public class NearbyActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        setTitle("Stations Nearby");

        //Bottom Navigation Bar
        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setDefaultTab(R.id.tab_nearby);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_planner) {
                    Intent planner = new Intent(getApplicationContext(), MapsActivity.class);
                    startActivity(planner);
                }

                    /*if (tabId == R.id.tab_nearby) {
                        Intent nearby = new Intent(getApplicationContext(), NearbyActivity.class);
                        startActivity(nearby);

                    }*/ else if (tabId == R.id.tab_schedule) {
                        Intent schedule = new Intent(getApplicationContext(), trainSchedule.class);
                        startActivity(schedule);
                    } else if (tabId == R.id.tab_alerts) {
                        Intent alerts = new Intent(getApplicationContext(), AlertActivity.class);
                        startActivity(alerts);
                    } else if (tabId == R.id.tab_profile) {
                        Intent setting = new Intent(getApplicationContext(), ProfileActivity.class);
                        startActivity(setting);
                    }

            }

        });


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.station_recycler);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        ArrayList<NearbyStations> list = (ArrayList<NearbyStations>) getIntent().getSerializableExtra("STATION");

        try {
            Log.v("listSize", String.valueOf(list.size()));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Recycler_Stations_Adapter adapter = new Recycler_Stations_Adapter(list, getApplication());
        recyclerView.setAdapter(adapter);



    }

}
