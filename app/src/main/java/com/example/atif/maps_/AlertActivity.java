package com.example.atif.maps_;

/**
 * Created by rahman on 12/10/16.
 */

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.transit.realtime.GtfsRealtime;
import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AlertActivity extends AppCompatActivity {
    private ResponseReceiver receiver;
    ImageButton btnAlerts;
    ImageButton btnMaps;
    ImageButton btnSchedule;
    ImageButton btnoffMap;
    ImageButton btnSetting;

    //Button
    public void Schedule() {
        btnSchedule = (ImageButton) findViewById(R.id.schedule);
        btnSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent time = new Intent(AlertActivity.this, trainSchedule.class);
                startActivity(time);
            }
        });
    }

    public void Alerts(){
        btnAlerts = (ImageButton) findViewById(R.id.btnAlerts);
        btnAlerts.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent alerts = new Intent(AlertActivity.this, AlertActivity.class);
                startActivity(alerts);
            }
        });
    }
    public void Maps(){
        btnMaps = (ImageButton) findViewById(R.id.planner);
        btnMaps.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent alerts = new Intent(AlertActivity.this, MapsActivity.class);
                startActivity(alerts);
            }
        });
    }
    public void offMap(){
        btnoffMap = (ImageButton) findViewById(R.id.offlinemap);
        btnoffMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent offMaps = new Intent(AlertActivity.this, offMap.class);
                startActivity(offMaps);
            }
        });
    }
    public void Settings(){
        btnSetting = (ImageButton) findViewById(R.id.setting);
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent setting = new Intent(AlertActivity.this, offMap.class);
                startActivity(setting);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        //getSupportActionBar().hide();

        final Intent intent= new Intent(this, myIntentService.class);
        //startService(intent);
        //new DataFeedTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        IntentFilter filter = new IntentFilter(ResponseReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        registerReceiver(receiver, filter);

        //scheduleAlarm();
        final long period = 50*1000;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                startService(intent);
            }
        }, 0, period);

        Alerts();
        Maps();
        offMap();
        Schedule();
        Settings();

    }


    public class ResponseReceiver extends BroadcastReceiver {
        public static final String ACTION_RESP =
                "com.rahman.myapplication.action";

        @Override
        public void onReceive(Context context, Intent intent) {
            //Toast.makeText(context,"Alerts Updated",Toast.LENGTH_LONG).show();
            ArrayList<Alert> list =(ArrayList<Alert>) intent.getSerializableExtra(myIntentService.PARAM_OUT_MSG);

            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
            Recycler_View_Adapter adapter = new Recycler_View_Adapter(list, getApplication());
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));



            //To show if there are any alerts
            if(list.size()==0) {
                TextView noAlerts = (TextView) findViewById(R.id.txtNoAlerts);
                noAlerts.setText("No Alerts At This Moment");
            }
            else{
                //To show the last updated date and time
                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                TextView lastUpdated = (TextView) findViewById(R.id.txtLastUpdated);
                lastUpdated.setText("Last Updated  "+currentDateTimeString);
            }

        }

    }
}
