package com.example.atif.maps_;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.ArrayList;

import Modules.Recycler_Route_Adapter;
import Modules.RouteOption;

public class routeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        setTitle("Directions");

        RecyclerView recyclerView= (RecyclerView) findViewById(R.id.recycler_view_route);


        final LinearLayoutManager layoutManager= new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        ArrayList<RouteOption> routeList = (ArrayList<RouteOption>) getIntent().getSerializableExtra("FILES_TO_SEND");

        Log.v("ListSize","routerList list"+String.valueOf(RouteLister.routeList.size()));

        Log.v("ListSize","routeActivity list"+String.valueOf(routeList.size()));
        Recycler_Route_Adapter adapter = new Recycler_Route_Adapter(routeList, getApplication());
        recyclerView.setAdapter(adapter);

        RouteLister.routeList.clear();






    }
}
