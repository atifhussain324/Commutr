package com.example.atif.maps_;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;

import Modules.RouteLister;

public class trainSchedule extends AppCompatActivity {

    AutoCompleteTextView editText;
    Button button;
    ImageButton btnAlerts;
    ImageButton btnMaps;
    ImageButton btnSchedule;
    ImageButton btnoffMap;
    ImageButton btnSetting;
String index;
    ListView listview;
    public final static String MESSAGE_KEY = "com.example.atif.commutr_.message_key";

/*
    //Button
    public void Schedule() {
        btnSchedule = (ImageButton) findViewById(R.id.schedule);
        btnSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent time = new Intent(trainSchedule.this, trainSchedule.class);
                startActivity(time);
            }
        });
    }

    public void Alerts(){
        btnAlerts = (ImageButton) findViewById(R.id.btnAlerts);
        btnAlerts.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent alerts = new Intent(trainSchedule.this, AlertActivity.class);
                startActivity(alerts);
            }
        });
    }
    public void Maps(){
        btnMaps = (ImageButton) findViewById(R.id.planner);
        btnMaps.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent alerts = new Intent(trainSchedule.this, MapsActivity.class);
                startActivity(alerts);
            }
        });
    }
    public void offMap(){
        btnoffMap = (ImageButton) findViewById(R.id.offlinemap);
        btnoffMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent offMaps = new Intent(trainSchedule.this, offMap.class);
                startActivity(offMaps);
            }
        });
    }
    public void Settings(){
        btnSetting = (ImageButton) findViewById(R.id.setting);
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent setting = new Intent(trainSchedule.this, SettingsActivity.class);
                startActivity(setting);
            }
        });
    }
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_schedule);
        listview = (ListView) findViewById(R.id.listView1);
        editText = (AutoCompleteTextView) findViewById(R.id.stationname);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item);
        editText.setAdapter(adapter);
        button = (Button) findViewById(R.id.search);

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DBuse.class);
                String message = editText.getText().toString();
                intent.putExtra(MESSAGE_KEY,message);
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
                        }
                        else{
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
