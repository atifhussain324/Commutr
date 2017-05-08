package com.example.atif.maps_;

/**
 * Created by rahman on 12/10/16.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class AlertActivity extends AppCompatActivity {
    private ResponseReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        //getSupportActionBar().hide();
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        setTitle("MTA Alerts");


        final Intent intent= new Intent(this, myIntentService.class);
        //startService(intent);
        //new DataFeedTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        //Bottom Navigation Bar
        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setDefaultTab(R.id.tab_alerts);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_planner) {
                    Intent planner = new Intent(getApplicationContext(), MapsActivity.class);
                    startActivity(planner);
                }
                    else if (tabId == R.id.tab_nearby) {
                        Intent nearby = new Intent(getApplicationContext(), NearbyActivity.class);
                        startActivity(nearby);
                    } else if (tabId == R.id.tab_schedule) {
                        Intent schedule = new Intent(getApplicationContext(), trainSchedule.class);
                        startActivity(schedule);
                    } /*else if (tabId == R.id.tab_alerts) {
                        Intent alerts = new Intent(getApplicationContext(), AlertActivity.class);
                        startActivity(alerts);
                    }*/ else if (tabId == R.id.tab_profile) {
                        Intent setting = new Intent(getApplicationContext(), ProfileActivity.class);
                        startActivity(setting);
                    }
                }


        });






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
