package com.example.atif.maps_;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

public class trainSchedule extends AppCompatActivity {

    EditText editText;
    Button button;
    ImageButton btnAlerts;
    ImageButton btnMaps;
    ImageButton btnSchedule;
    ImageButton btnoffMap;
    ImageButton btnSetting;

    ListView listview;
    public final static String MESSAGE_KEY = "com.example.atif.commutr_.message_key";


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_schedule);
        listview = (ListView) findViewById(R.id.listView1);
        editText = (EditText) findViewById(R.id.stationname);
        button = (Button) findViewById(R.id.search);

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d("Main","Click started");
                Intent intent = new Intent(getApplicationContext(), DBuse.class);
                String message = editText.getText().toString();
                intent.putExtra(MESSAGE_KEY,message);
                startActivity(intent);
                Log.d("Main","Click");
            }
        });
        getTrainInfo ga = new getTrainInfo();
        ga.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);



        //access different activity in ImageButton
        Alerts();
        Maps();
        offMap();
        Schedule();
        Settings();

    }
    private class getTrainInfo extends AsyncTask<Void, Void, Void>{

        ArrayList<String> announcements = new ArrayList<>();
        private ProgressDialog pDiaLog;

        @Override
        protected void onPreExecute(){
            pDiaLog = new ProgressDialog(trainSchedule.this);
            pDiaLog.setCancelable(false);
            pDiaLog.setMessage("Waiting...");
            showDialog();

            super.onPreExecute();

        }
        @Override
        protected Void doInBackground(Void... voids) {
            DBCreater db = new DBCreater();
            announcements = db.trainInfo();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid){
            pushToView(announcements);
            hideDialog();
            super.onPostExecute(aVoid);
        }

        private void showDialog(){
            if(!pDiaLog.isShowing()){
                pDiaLog.show();
            }
        }

        private void hideDialog(){
            if(pDiaLog.isShowing()){
                pDiaLog.hide();
            }
        }
    }

    private void pushToView(ArrayList<String> announcements){
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, announcements);
        listview.setAdapter(adapter);
    }
}
