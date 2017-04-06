package com.example.atif.maps_;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import com.roughike.swipeselector.SwipeItem;
import com.roughike.swipeselector.SwipeSelector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drop_dialog);
        //new DBTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

       SwipeSelector swipeSelector = (SwipeSelector) findViewById(R.id.swipeSelector);
        swipeSelector.setItems(

                new SwipeItem(0, "Police Investigation", "Description for slide one."),
                new SwipeItem(1, "Sick Passenger", "Description for slide two."),
                new SwipeItem(2, "Train Traffic", "Description for slide three."),
                new SwipeItem(3, "Signal Malfunction", "Description for slide four."),
                new SwipeItem(4, "FastTrack", "Description for slide four.")

        );

        SwipeSelector swipeSelector2 = (SwipeSelector) findViewById(R.id.swipeSelector2);
        swipeSelector2.setItems(
                new SwipeItem(0, "1 Train", "Broadway-7th Avenue Local"),
                new SwipeItem(1, "2 Train", "Seventh Avenue Express"),
                new SwipeItem(2, "3 Train", "Seventh Avenue Express"),
                new SwipeItem(3, "4 Train", "Lexington Avenue Express"),
                new SwipeItem(4, "5 Train", "Lexington Avenue Express"),
                new SwipeItem(5, "6 Train", "Lexington Avenue Local/Pehlam Express"),
                new SwipeItem(6, "7 Train", "Flushing Local"),
                new SwipeItem(7, "A Train", "8th Avenue Express"),
                new SwipeItem(8, "B Train", "Central Park West Local/6th Avenue Express"),
                new SwipeItem(9, "C Train", "8th Avenue Local"),
                new SwipeItem(10, "D Train", "6th Avenue Express"),
                new SwipeItem(11, "E Train", "8th Avenue Local"),
                new SwipeItem(12, "F Train", "6th Avenue Local"),
                new SwipeItem(13, "G Train", "Brooklyn-Queens Crosstown Local"),
                new SwipeItem(14, "J Train", "Nassau Street Express"),
                new SwipeItem(15, "L Train", "14th Street-Canarsie Local"),
                new SwipeItem(16, "M Train", "Queens Blvd Local/6 Av Local/Myrtle Ave Local"),
                new SwipeItem(17, "N Train", "Broadway Express"),
                new SwipeItem(18, "Q Train", "Second Avenue/Broadway Express"),
                new SwipeItem(19, "R Train", "Queens Boulevard/Broadway/4th Avenue Local"),
                new SwipeItem(20, "W Train", "Broadway Local"),
                new SwipeItem(21, "Z Train", "Nassau Street Express"),
                new SwipeItem(22, "S Train", "42nd Street Shuttle")

        );

        SwipeSelector swipeSelector3 = (SwipeSelector) findViewById(R.id.swipeSelector3);
        swipeSelector3.setItems(
                new SwipeItem(0, "Uptown", "Description for slide one."),
                new SwipeItem(1, "Downtown", "Description for slide two.")

        );


        //builder.setView(content);


    }

    /*class DBTask extends AsyncTask<String, Void, Boolean> {

        private Connection connect;
        private Statement statement;
        private ResultSet resultSet;

        @Override
        protected Boolean doInBackground(String... params) {
            boolean status = false;
            // Setup the connection with the DB
            try {
                Class.forName("com.mysql.jdbc.Driver");
                connect = DriverManager
                        .getConnection("jdbc:mysql://104.196.170.96:3306/commutr?"
                                + "user=root&password=Seniorproject1");

                // Statements allow to issue SQL queries to the database
                statement = connect.createStatement();
                // Result set get the result of the SQL query
                resultSet = statement
                        .executeQuery("select * from routes LIMIT 10");

                while(resultSet.next()){
                    Log.v("DB", resultSet.getString(4)); // retrives station name
                }
                status = true;
            }
            catch(Exception e){
                status = false;
            }
            return status;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Toast.makeText(getApplicationContext(), "Connection status: " + result, Toast.LENGTH_LONG).show();

        }
    }*/
}
