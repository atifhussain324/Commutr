package com.example.atif.maps_;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.dbconnection.subham.dblibrary.MyView;

/**
 * Created by Atif on 12/9/16.
 */

public class Parse extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = new MyView(this);
        setContentView(v);
        String[] tagnames={"SNO","Paper","Country"}; //These are the keys of the JSON ARRay
        String[] tagtype={"text","text","text"}; //This is type of value, it can be text or image
        String arrayname="routes";// This is the name of array of JSON File
        MyView.setinputdata("https://maps.googleapis.com/maps/api/directions/json?origin=1925_mcgraw_ave&destination=1855_broadway_ave&mode=transit&transit_mode=subway&transit_routing_preference=less_walking&transit_details=arrival_stop&transit_details=departure_stop&transit_details=line&transit_details=num_stops&alternatives=true&key=AIzaSyDmISqtltaK4I-e22Oh8W2wb-j0p1u9jSA",
                arrayname,tagnames,tagtype,R.layout.list_card_view);

        //As you see you need to pass only URL, Name of result array,names of key in JSON key value pairs,And a layout file to see the //results.Make sure while making layout files you give id to each elements as p1,p2,p3..... as specifie in tagnames.
    }
}
