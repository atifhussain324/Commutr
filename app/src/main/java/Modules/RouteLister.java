package Modules;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import com.example.atif.maps_.MapsActivity;
import com.example.atif.maps_.routeActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Atif on 12/9/16.
 */

public class RouteLister {
    private static final String DIRECTION_URL_API = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String GOOGLE_API_KEY = "AIzaSyDmISqtltaK4I-e22Oh8W2wb-j0p1u9jSA";
    private String origin;
    private String destination;
    public static ArrayList<RouteOption> routeList=  new ArrayList<>();
    private MapsActivity mActivity;


    public RouteLister(MapsActivity activity, String origin, String destination) {
        this.mActivity = activity;
        this.origin = origin;
        this.destination = destination;
    }

    public void execute() throws UnsupportedEncodingException {
        new DownloadRawData().execute(createUrl());
    }

    private String createUrl() throws UnsupportedEncodingException {
        String urlOrigin = URLEncoder.encode(origin, "utf-8");
        String urlDestination = URLEncoder.encode(destination, "utf-8");
        return DIRECTION_URL_API + "origin=" + urlOrigin + "&destination=" + urlDestination + "&mode=transit&transit_mode=subway&key=" + GOOGLE_API_KEY;
    }

    private class DownloadRawData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String link = params[0];
            try {
                URL url = new URL(link);
                InputStream is = url.openConnection().getInputStream();
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String res) {
            try {
                parseJSon(res);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void parseJSon(String data) throws JSONException {
        if (data == null)
            return;

        //routeList = new ArrayList<>();
        JSONObject jsonData = new JSONObject(data);
        JSONArray jsonRoutes = jsonData.getJSONArray("routes");
        Log.i("testing","before for loop");
        Log.i("testing", jsonRoutes.length() + " - Routes");
        //for (int i = 0; i < jsonRoutes.length(); i++) {
            //Log.i("testing", "in loop - " + i);
            try {
                JSONObject jsonRoute = jsonRoutes.getJSONObject(0);
                //Route route = new Route();
                RouteOption routeOption= new RouteOption();

                JSONArray jsonLegs = jsonRoute.getJSONArray("legs");
                JSONObject jsonLeg = jsonLegs.getJSONObject(0);
                if (jsonLegs.getJSONObject(0) != null) {
                    String location = jsonLegs.getJSONObject(0).getString("end_address");
                    Log.i("testing", location);
                } else {
                    Log.i("testing", "No data found");
                }
                /*
                String totalDuration = jsonLeg.getJSONObject("duration").getString("text");
                Log.i("testing", totalDuration);

                String departureTime = jsonLeg.getJSONObject("departure_time").getString("text");
                Log.i("testing", departureTime);

                String arrivalTime = jsonLeg.getJSONObject("arrival_time").getString("text");
                Log.i("testing", arrivalTime);
                */
                JSONArray stepsArray=jsonLeg.getJSONArray("steps");
                for (int i=0;i<stepsArray.length();i++) {
                    Log.i("testingFinal", Integer.toString(stepsArray.length()));
                    if(stepsArray.getJSONObject(i).getString("travel_mode").equals("WALKING")){
                        String wName = stepsArray.getJSONObject(i).getString("travel_mode");
                        String wInstruction = stepsArray.getJSONObject(i).getString("html_instructions");
                        String wDuration = stepsArray.getJSONObject(i).getJSONObject("duration").getString("text");
                        String wDistance = stepsArray.getJSONObject(i).getJSONObject("distance").getString("text");
                        String travel_mode= stepsArray.getJSONObject(i).getString("travel_mode");

                        routeOption.setTravel_mode(travel_mode);
                        routeOption.setwName(wName);
                        routeOption.setwInstruction(wInstruction);
                        routeOption.setwDistance(wDistance);
                        routeOption.setwDuration(wDuration);


                    }
                    else{
                        String tName = stepsArray.getJSONObject(i).getJSONObject("transit_details").getJSONObject("departure_stop").getString("name");
                        String trainShortName = stepsArray.getJSONObject(i).getJSONObject("transit_details").getJSONObject("line").getString("short_name");
                        String trainLongName = stepsArray.getJSONObject(i).getJSONObject("transit_details").getJSONObject("line").getString("name");
                        String tInstruction = stepsArray.getJSONObject(i).getString("html_instructions");
                        int tStops = stepsArray.getJSONObject(i).getJSONObject("transit_details").getInt("num_stops");
                        String tStepDur = stepsArray.getJSONObject(i).getJSONObject("duration").getString("text");
                        String travel_mode= stepsArray.getJSONObject(i).getString("travel_mode");

                        routeOption.setTravel_mode(travel_mode);
                        routeOption.settName(tName);
                        routeOption.settInstruction(tInstruction);
                        routeOption.settShortName(trainShortName);
                        routeOption.settLongName(trainLongName);
                        routeOption.settStops(tStops);
                        routeOption.settStepDuration(tStepDur);



                    }
                    routeList.add(routeOption);

                }
                Log.i("testingFinal2", Integer.toString(routeList.size()));
                // I want to get the steps Array one sec I will go pee

                //String name2 = jsonLeg.getJSONArray("steps").getJSONObject(0).getJSONObject("transit_details").getJSONObject("departure_stop").getString("name");


                //FOR TRANSIT, STEP 1 is used and Transit Mode = TRANSIT

                // 1 - Name, 2 - trainShortname + trainLongName, 3 - html_instructions, 4- stops and stepdur

                //FOR WALKING, STEP O is used and Transit Mode = WALKING

                //1 - Name , 2 - Instruction , 3 - Duration and Distance




                //Log.i("testing3", name+instruction+stops+stepDur);



                /*
                routeOption.setName(name);
                routeOption.setInstruction(instruction);
                routeOption.setStops(stops);
                routeOption.setStepDuration(stepDur);
                */
                //System.out.println(location);




            }catch(Exception e){
                Log.e("testing", e.getMessage());
            }



        //}
        Log.i("testing for list",routeList.get(0).getInstruction());

          //final LinearLayoutManager layoutManager= new LinearLayoutManager(mActivity.getApplicationContext());
        //layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //mActivity.recyclerView.setLayoutManager(layoutManager);
        //Intent i= new Intent(mActivity,routeActivity.class);
       // Intent i = new Intent();
        //i.putExtra("FILES_TO_SEND",routeList);
        //mActivity.startActivity(i);

        //Recycler_Route_Adapter adapter = new Recycler_Route_Adapter(routeList,mActivity.getApplication());
        //mActivity.recyclerView.setAdapter(adapter);



        /*
        final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        */

        //listener.onDirectionFinderSuccess(routes);
        Log.i("test","outside");
        }
    public ArrayList<RouteOption> getlist(){
        return routeList;
    }


}

