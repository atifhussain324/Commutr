package com.example.atif.maps_;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DBuseActivity extends AppCompatActivity {
    ListView listview;

    public final static String MESSAGE_KEY = "com.example.atif.commutr_.message_key";

    private static final String url = "jdbc:mysql://130.211.231.29:3306/commutr";
    private static final String dbUsername = "root";
    private static final String dbPassword = "rahman1";
    private static final String driver = "com.mysql.jdbc.Driver";
    private String message;
    private Connection conn;
    String index;
    String query;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbuse);
        Bundle bundle = getIntent().getExtras();
        message = bundle.getString(MESSAGE_KEY);
        getTrainInfo ga = new getTrainInfo();
        ga.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        Button button =(Button)findViewById(R.id.back);
        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                startActivity(new Intent(DBuseActivity.this, trainSchedule.class));




            }
        });
        index = getIntent().getStringExtra("transportation");

    }

    private class getTrainInfo extends AsyncTask<Void, Void, Void> {

        ArrayList<String> announcements = new ArrayList();
        private ProgressDialog pDiaLog;


        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pDiaLog = new ProgressDialog(DBuseActivity.this);
            pDiaLog.setCancelable(true);
            pDiaLog.setMessage("Waiting...");
            Log.d("Dbuse","Inside preexecute");
            showDialog();


        }
        @Override
        protected Void doInBackground(Void... voids) {
            //DBuse db = new DBuse();
            conn = null;
            try{
                Class.forName(driver);
                conn = DriverManager.getConnection(url, dbUsername, dbPassword);
            }catch(Exception e){
                Log.e("DBuse network",e.getMessage());
                e.printStackTrace();
            }
            if(conn!= null) {
                Intent intent = getIntent();
                message = intent.getStringExtra(MESSAGE_KEY);
                Log.d("Dbuse","Connection established");
            }
            else{
                Log.d("Dbuse","Connection problem");
                finish();
            }
            announcements = trainInfo();
            Log.d("Dbuse","Inside doInBg");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid){
            super.onPostExecute(aVoid);
            hideDialog();
            pushToView(announcements);
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

    public ArrayList<String> trainInfo(){
        final ArrayList<String> annoucements = new ArrayList<>();
        if (index.equals("train")) {
            query = "select route_id, trip_headsign, round(time_to_sec(subtime(time_format(now(),'%H:%i:%S'), " +
                    "arrival_time))/60) as estTime " +
                    "from stops, stop_times, trips " +
                    "where stops.stop_id = stop_times.stop_id " +
                    "and stop_times.trip_id = trips.trip_id " +
                    "and stops.stop_name LIKE '%" + message + "%' " +
                    "and stop_times.arrival_time < time_format(now(),'%H:%i:%S') " +
                    "order by estTime asc limit 15;";
        }
        else {
            query = "select route_id, trip_headsign, " +
                    "round( time_to_sec(subtime(arrival_time, time_format(now(),'%H:%i:%S')))/60) as estTime " +
                    "from bustrips, newbusstop_times, busstops " +
                    "where busstops.stop_id = newbusstop_times.stop_id " +
                    "and bustrips.trip_id = newbusstop_times.trip_id " +
                    "and busstops.stop_name like '%" + message + "%' " +
                    "and arrival_time > time_format(now(),'%H:%i:%S') " +
                    "limit 5;";
        }
        try{
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            if (rs.last()) {
                Log.d("Dbuse","Records:" + rs.getRow());
                rs.beforeFirst(); // not rs.first() because the rs.next() below will move on, missing the first element
            }
            while (rs.next()){
                Log.v("DB", rs.getString(1)); // retrives station name
                Log.v("DB", rs.getString(2));
                Log.v("DB", rs.getString(3));
                String trainID = rs.getString(1);
                String Direction = rs.getString(2);
                String time = rs.getString(3);
                String display = trainID +""+time+"Minutes" + "\n" +"Towards "+ Direction;
                annoucements.add(display);
            }
            rs.close();
            st.close();
        }catch (SQLException e){
            e.getStackTrace();
        }
        Log.d("Dbuse","Announcements:" + annoucements.size());
        return annoucements;

    }
    private void pushToView(ArrayList<String> announcements){
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, announcements);
        listview = (ListView) findViewById(R.id.listView1);
        listview.setAdapter(adapter);
    }

}
