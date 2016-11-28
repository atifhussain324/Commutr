package Modules;

import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by Atif on 11/27/16.
 */

public class DBHelper {
    private static final String url = "jdbc:mysql://104.196.170.96:3306/commutr?"
            + "user=root&password=Seniorproject1";
    private static final String dbUsername = "";
    private static final String dbPassword = "";
    private boolean mError = false;

    public void connect(){
        try {
        }
        catch (Exception e){

        }
    }

    public ResultSet getData(String query, DBResultSetInterface dBResultSetInterface){
        ResultSet resultSet = null;
        new SelectTask(query, dBResultSetInterface).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return resultSet;
    }
    private class SelectTask extends AsyncTask<String, Void, Boolean> {
        private String mQuery;
        private DBResultSetInterface mDBResultSetInterface;
        private ResultSet mResultSet;
        private Statement mStatement;
        private Connection mConnection;

        public SelectTask(String query, DBResultSetInterface dBResultSetInterface) {
            mQuery = query;
            mDBResultSetInterface = dBResultSetInterface;
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            boolean error = false;
            try {
                Class.forName("com.mysql.jdbc.Driver");
                mConnection = DriverManager.getConnection(url, dbUsername, dbPassword);
                mStatement = mConnection.createStatement();
                mResultSet = mStatement.executeQuery(mQuery);


            }
            catch (Exception e) {
                Log.e("DB", e.getMessage());
                error = true;
                mError = true;
            }
            return error;
        }
        @Override
        protected void onPostExecute(Boolean error) {
            if (!error){
                mDBResultSetInterface.onDataRetrieved(mResultSet);
            }
        }
    }
}
