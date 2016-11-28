package com.example.atif.maps_;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new DBTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

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
