package com.example.atif.maps_;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;

import Modules.Recycler_Route_Adapter;
import Modules.Route;
import Modules.RouteOption;

public class routeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        RecyclerView recyclerView= (RecyclerView) findViewById(R.id.recycler_view_route);


        final LinearLayoutManager layoutManager= new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        ArrayList<RouteOption> routeList = (ArrayList<RouteOption>) getIntent().getSerializableExtra("FILES_TO_SEND");
        Log.v("ListSize",String.valueOf(routeList.size()));
        Recycler_Route_Adapter adapter = new Recycler_Route_Adapter(routeList, getApplication());
        recyclerView.setAdapter(adapter);







    }
}
