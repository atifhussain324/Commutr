package com.example.atif.maps_;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drop_dialog);
        new DBTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

       SwipeSelector swipeSelector = (SwipeSelector) findViewById(R.id.swipeSelector);
        swipeSelector.setItems(
                // The first argument is the value for that item, and should in most cases be unique for the
                // current SwipeSelector, just as you would assign values to radio buttons.
                // You can use the value later on to check what the selected item was.
                // The value can be any Object, here we're using ints.
                new SwipeItem(0, "Police Investigation", "Description for slide one."),
                new SwipeItem(1, "Sick Passenger", "Description for slide two."),
                new SwipeItem(2, "Train Traffic", "Description for slide three."),
                new SwipeItem(3, "Signal Malfunction", "Description for slide four.")

        );


        SwipeSelector swipeSelector2 = (SwipeSelector) findViewById(R.id.swipeSelector2);
        swipeSelector2.setItems(
                // The first argument is the value for that item, and should in most cases be unique for the
                // current SwipeSelector, just as you would assign values to radio buttons.
                // You can use the value later on to check what the selected item was.
                // The value can be any Object, here we're using ints.
                new SwipeItem(0, "1 Train", "Description for slide one."),
                new SwipeItem(1, "2 Train", "Description for slide two."),
                new SwipeItem(2, "3 Train", "Description for slide three."),
                new SwipeItem(3, "4 Train", "Description for slide four.")
        );

        SwipeSelector swipeSelector3 = (SwipeSelector) findViewById(R.id.swipeSelector3);
        swipeSelector3.setItems(
                // The first argument is the value for that item, and should in most cases be unique for the
                // current SwipeSelector, just as you would assign values to radio buttons.
                // You can use the value later on to check what the selected item was.
                // The value can be any Object, here we're using ints.
                new SwipeItem(0, "Uptown", "Description for slide one."),
                new SwipeItem(1, "Downtown", "Description for slide two.")

        );



    }
    class DBTask extends AsyncTask<String, Void, Boolean> {

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
    }
}
