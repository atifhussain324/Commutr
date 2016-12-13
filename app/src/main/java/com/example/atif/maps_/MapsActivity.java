package com.example.atif.maps_;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import Modules.DirectionFinder;
import Modules.DirectionFinderListener;
import Modules.Route;
import Modules.RouteLister;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnPoiClickListener, ActivityCompat.OnRequestPermissionsResultCallback, GoogleApiClient.OnConnectionFailedListener, DirectionFinderListener {

    public ImageButton btnAlerts;
    private GoogleMap mMap;
    private static final int MY_PERMISSION_FINE_LOCATION = 101;
    private GoogleApiClient mGoogleApiClient;
    private String locationName;
    private Button searchButton;
    private String mOrigin, mDestination;
    private ProgressDialog progressDialog;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private PlaceAutocompleteFragment mOriginAutocompleteFragment, mDestinationAutocompleteFragment;
    private ListView lv;
    ArrayList<Route> temp;


    public void Alerts(){
        btnAlerts = (ImageButton) findViewById(R.id.btnAlerts);
        btnAlerts.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent alerts = new Intent(MapsActivity.this, AlertActivity.class);
                startActivity(alerts);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        temp=RouteLister.routes;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mOrigin = "";
        mDestination = "";

        //Search Button
        searchButton = (Button) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
                Log.i("testing2",String.valueOf(temp.size()));

                 }
        });




        //Google Start Search Bar
        mOriginAutocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_origin);
        mOriginAutocompleteFragment.setHint("Start");
        mOriginAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                mOriginAutocompleteFragment.setText(place.getName());
                mOrigin = place.getAddress().toString();
                Log.i("Map", mOrigin);
            }

            @Override
            public void onError(Status status) {
            }
        });

        //Google Destination Search Bar
        mDestinationAutocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_destination);
        mDestinationAutocompleteFragment.setHint("Destination");
        mDestinationAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                mDestinationAutocompleteFragment.setText(place.getName());
                mDestination = place.getAddress().toString();
            }

            @Override
            public void onError(Status status) {
            }
        });

        Alerts();

    }

    //Search Button Request
    private void sendRequest() {
        if (mOrigin.isEmpty()) {
            Toast.makeText(this, "Please enter origin address!", Toast.LENGTH_SHORT).show();

        }
        else if (mDestination.isEmpty()) {
            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();

        }
        else if (mOrigin.isEmpty() && mDestination.isEmpty()) {
            Toast.makeText(this, "Please enter a starting and destination address", Toast.LENGTH_SHORT).show();

        }
        else {
            try {
                new DirectionFinder(this, mOrigin, mDestination).execute();
                new RouteLister(this, mOrigin, mDestination).execute();
            } catch (Exception e) {
                Log.e("Find route", e.getMessage());
                e.printStackTrace();
            }
        }
        //Toast.makeText(this,RouteLister.routes.get(0).getArrivalTime(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(NEWYORK.getCenter(), 10));
        LatLng newyork = new LatLng(40.758879, -73.985110);
        //mMap.addMarker(new MarkerOptions().position(newyork).title("Marker in New York"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(newyork));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnPoiClickListener(this);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_FINE_LOCATION);
            }
        }
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e("MapsActivityRaw", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapsActivityRaw", "Can't find style.", e);
        }

    }

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait...",
                "Searching", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                    } else {
                        Toast.makeText(getApplicationContext(), "This Application Requires Location Permissions to be enabled.", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
                break;

        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);
            //Toast.makeText(getApplicationContext(), route.htmlinstructions, Toast.LENGTH_LONG ).show();


            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions()
                    .geodesic(true)
                    .color(Color.BLUE)
                    .width(10)
                    ;

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));

        }
    }
    public class ResponseReceiver extends BroadcastReceiver {
        public static final String ACTION_RESP =
                "com.rahman.myapplication.action";

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context,"Alerts Updated",Toast.LENGTH_LONG).show();
            ArrayList<Alert> list =(ArrayList<Alert>) intent.getSerializableExtra(myIntentService.PARAM_OUT_MSG);
             /*RecyclerView recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
            Recycler_View_Adapter adapter = new Recycler_View_Adapter(list, getApplication());
            recyclerView.setAdapter(adapter);
            */

            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
            Recycler_View_Adapter adapter = new Recycler_View_Adapter(list, getApplication());
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            if(list.size()==0) {
                TextView noAlerts = (TextView) findViewById(R.id.txtNoAlerts);
                noAlerts.setText("No Alerts At This Moment");
            }
            //result.setText(text);
        }

    }

    @Override
    public void onPoiClick(PointOfInterest poi) {
        Toast.makeText(getApplicationContext(), "Clicked: " +
                        poi.name + "\nPlace ID:" + poi.placeId +
                        "\nLatitude:" + poi.latLng.latitude +
                        " Longitude:" + poi.latLng.longitude,
                Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}

