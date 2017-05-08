package com.example.atif.maps_;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import Modules.NearbyStations;

public class MainActivity extends AppCompatActivity {

    Button btnHit;
    TextView name;
    ProgressDialog pd;
    String stationName;
    String photoRef;
    ImageView stationImage;
    String location;
    private static final String GOOGLE_API_KEY = "AIzaSyDmISqtltaK4I-e22Oh8W2wb-j0p1u9jSA";
    public static ArrayList<NearbyStations> stationList = new ArrayList<>();

    public MainActivity(String location) {
        this.location = location;

    }

    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nearby_cardview);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        setTitle("Stations Nearby");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        location = preferences.getString("location", "");


        Log.v("mainLoc", location);
        //btnHit = (Button) findViewById(R.id.btnHit);
        name = (TextView) findViewById(R.id.stationName);

        String locationURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + location + "&radius=500&types=subway_station&key=AIzaSyDmISqtltaK4I-e22Oh8W2wb-j0p1u9jSA";
        Log.v("locationURL", locationURL);
        new JsonTask().execute(locationURL);





    } */
    public void execute() throws UnsupportedEncodingException {
        String locationURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + location + "&radius=500&types=subway_station&key=AIzaSyDmISqtltaK4I-e22Oh8W2wb-j0p1u9jSA";
        Log.v("locationURL", locationURL);
        new JsonTask().execute(locationURL);

    }


    private class JsonTask extends AsyncTask<String, String, String> {

        /*protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
        }*/

        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
           /* if (pd.isShowing()) {
                pd.dismiss();
            }
*/

            //RouteLister.routeList.clear();


            //txtJson.setText(result);
            try {
                parseJSon(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void parseJSon(String data) throws JSONException {
            if (data == null)
                return;

            JSONObject jsonData = new JSONObject(data);
            JSONArray jsonStations = jsonData.getJSONArray("results");

            for (int i = 0; i < jsonStations.length(); i++) {
                NearbyStations station = new NearbyStations();
                try {
                    JSONObject jsonResult = jsonStations.getJSONObject(i);
                    JSONArray jsonImageRef = jsonResult.getJSONArray("photos");
                    JSONObject jsonRef = jsonImageRef.getJSONObject(0);

                    if (jsonStations.getJSONObject(i) != null) {
                        stationName = jsonResult.getString("name");
                        station.setStationName(stationName);
                        Log.i("testing", stationName);
                    }
                    else {
                        Log.i("testing", "No data found");
                    }

                    if (jsonRef != null) {
                        photoRef = jsonRef.getString("photo_reference");
                        String imageURL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=200&photoreference=" + photoRef + "&key=" + GOOGLE_API_KEY;
                        station.setImageRef(imageURL);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


                stationList.add(station);

            }

        }
    }
}