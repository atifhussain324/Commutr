package com.example.atif.maps_;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.ArrayList;

import Modules.NearbyStations;

public class trainSchedule extends AppCompatActivity {

    AutoCompleteTextView editText;
    Button button;

    String index;
    ListView listview;
    ArrayList<NearbyStations> temp2;

    public final static String MESSAGE_KEY = "com.example.atif.commutr_.message_key";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_schedule);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        setTitle("Schedule");


        //Bottom Navigation Bar
        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setDefaultTab(R.id.tab_schedule);
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
                    }  else if (tabId == R.id.tab_alerts) {
                        Intent alerts = new Intent(getApplicationContext(), AlertActivity.class);
                        startActivity(alerts);
                    } else if (tabId == R.id.tab_profile) {
                        Intent setting = new Intent(getApplicationContext(), ProfileActivity.class);
                        startActivity(setting);
                    }
                }


        });


        listview = (ListView) findViewById(R.id.listView1);
        editText = (AutoCompleteTextView) findViewById(R.id.stationname);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item);
        editText.setAdapter(adapter);
        button = (Button) findViewById(R.id.search);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DBuseActivity.class);
                String message = editText.getText().toString();
                intent.putExtra(MESSAGE_KEY, message);
                intent.putExtra("transportation", index);
                startActivity(intent);

            }
        });

        new MaterialDialog.Builder(trainSchedule.this)
                .title("Choose Transportation")
                .items(R.array.transportation)
                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (which == 0) {
                            index = "train";
                        } else {
                            index = "bus";
                        }
                        return true;
                    }
                })
                .negativeText(R.string.Cancel)
                .positiveText(R.string.OK)
                .show();


    }


}
