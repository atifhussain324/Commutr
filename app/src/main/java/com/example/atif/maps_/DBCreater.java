package com.example.atif.maps_;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by aaronou on 12/12/16.
 */

public class DBCreater {
    private Connection conn;
    private static final String url = "jdbc:mysql://104.196.170.96:3306/commutr";
    private static final String dbUsername = "root";
    private static final String dbPassword = "Seniorproject1";
    private static final String driver = "com.mysql.jdbc.Driver";

    public DBCreater(){
        conn = null;
        try{
            Class.forName(driver);
            conn = DriverManager.getConnection(url, dbUsername, dbPassword);
        }catch(ClassNotFoundException | SQLException e){
            e.printStackTrace();
        }
    }
    public ArrayList<String> trainInfo(){
        final ArrayList<String> annoucements = new ArrayList<>();

        String stationName ="";
        String query = "select route_id, trip_headsign, round(time_to_sec(subtime(time_format(now(),'%H:%i:%S'), arrival_time))/60) as estTime from stops, stop_times, trips where stops.stop_id = stop_times.stop_id and stop_times.trip_id = trips.trip_id and stops.stop_name = '"+stationName+"' and stop_times.arrival_time < time_format(now(),'%H:%i:%S') order by estTime asc limit 15;";

        try{
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()){
                Log.v("DB", rs.getString(1)); // retrives station name
                Log.v("DB", rs.getString(2));
                Log.v("DB", rs.getString(3));
                String trainID = rs.getString(1);
                String Direction = rs.getString(2);
                String time = rs.getString(3);
                String display = trainID +"                                                     "+time+"Minutes" + "\n" +"Towards "+ Direction;
                annoucements.add(display);
            }
            rs.close();
            st.close();
        }catch (SQLException e){
            e.getStackTrace();
        }
        return annoucements;
    }
}
