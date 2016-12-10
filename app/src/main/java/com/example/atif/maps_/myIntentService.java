package com.example.atif.maps_;

/**
 * Created by rahman on 12/5/16.
 */

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.transit.realtime.GtfsRealtime;
import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;

import java.net.URL;
import java.util.ArrayList;

public class myIntentService extends IntentService {
    public String resultText;
    //public static final String PARAM_IN_MSG = "imsg";
    public static final int REQUEST_CODE = 12345;
    public static final String PARAM_OUT_MSG = "omsg";
    private static final String TAG ="intentServiceTest";

    public ArrayList<Alert> alertList=new ArrayList<>();

    public myIntentService(){
        super("myIntentService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "The service has started");


        try {
            URL url = new URL("http://datamine.mta.info/files/k38dkwh992dk/gtfs");
            Log.i(TAG, "got URL");
            FeedMessage feed = GtfsRealtime.FeedMessage.parseFrom(url.openStream());

            Log.i(TAG, "parsed URL");
            for (FeedEntity entity : feed.getEntityList()) {
                if (entity.hasAlert()) {
                     Log.i(TAG,"Alert found");

                    for (GtfsRealtime.EntitySelector informedEntity : entity.getAlert().getInformedEntityList()) {
                        Alert alert=new Alert();

                        String train_number=informedEntity.getTrip().getRouteId();
                        alert.setTrain(train_number);
                        Log.i(TAG,"setTrain done");

                        String direction=informedEntity.getTrip().getTripId().substring(10,11);
                        alert.setDirection(direction);
                        Log.i(TAG,"setDirection done");

                        String status =  entity.getAlert().getHeaderText().getTranslation(0).getText();
                        alert.setStatus(status);
                        Log.i(TAG,"setStatus done");

                        alertList.add(alert);


                        //resultText = entity.getAlert().getHeaderText().getTranslation(0).getText();
                    }




                }
                Log.i(TAG, "The service has finished running");
                //Intent broadcastIntent = new Intent();


            }
        }
        catch (Exception e){Log.i(TAG,"things went crazy");}



        //BROADCASTING
        Intent broadcastIntent= new Intent();
        Log.i(TAG,"broadcasting");
        broadcastIntent.setAction(AlertActivity.ResponseReceiver.ACTION_RESP);
        broadcastIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(PARAM_OUT_MSG,alertList);
        sendBroadcast(broadcastIntent);
        Log.i(TAG,"broadcasting");



    }


    }
