package com.example.atif.maps_;

import android.Manifest;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.LocationCallback;
import com.firebase.geofire.util.Constants;
import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;
import com.roughike.swipeselector.SwipeItem;
import com.roughike.swipeselector.SwipeSelector;

import java.util.ArrayList;
import java.util.List;

import Modules.DirectionFinder;
import Modules.DirectionFinderListener;
import Modules.Route;
import Modules.RouteLister;
import Modules.RouteOption;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnPoiClickListener, ActivityCompat.OnRequestPermissionsResultCallback, GoogleApiClient.OnConnectionFailedListener, DirectionFinderListener, LocationListener, GoogleApiClient.ConnectionCallbacks {

    private static final int MY_PERMISSION_FINE_LOCATION = 101;
    ArrayList<RouteOption> temp;
    ArrayList<MapsActivity> mSelectedItems;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
    GeoFire geoFire = new GeoFire(ref);
    Location mLastLocation;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private String locationName;
    private String mOrigin, mDestination;
    private ProgressDialog progressDialog;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private PlaceAutocompleteFragment mOriginAutocompleteFragment, mDestinationAutocompleteFragment;
    private ListView lv;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth mAuthListener;
    private String Userid;
    private double mLatitudeText;
    private double mLongitudeText;
    GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mLatitudeText, mLongitudeText), 1.0);
    private Effectstype effect;
TextView displayName;
    TextView repScore;
    String eventName;

    AlertDialog dialog;
    private static final String GEOFENCE_REQ_ID = "My Geofence";
    private Circle geoFenceLimits;

    String[] latlong = "40.836405, -73.859922".split(",");
    double testLat = Double.parseDouble(latlong[0]);
    double testLng = Double.parseDouble(latlong[1]);
    LatLng testlocation = new LatLng(testLat, testLng);
    CircleOptions circleOptions;
    private PendingIntent geoFencePendingIntent;
    private final int GEOFENCE_REQ_CODE = 0;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    UserInfo profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Bottom Navigation Bar
        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_planner) {
                    Intent planner = new Intent(MapsActivity.this, MapsActivity.class);
                    startActivity(planner);
                } else if (tabId == R.id.tab_nearby) {
                    Intent offlineMap = new Intent(MapsActivity.this, offMap.class);
                    startActivity(offlineMap);
                } /*else if (tabId == R.id.tab_schedule) {
                    Intent schedule = new Intent(MapsActivity.this, trainSchedule.class);
                    startActivity(schedule);
                } */ else if (tabId == R.id.tab_alerts) {
                    Intent alerts = new Intent(MapsActivity.this, AlertActivity.class);
                    startActivity(alerts);
                } else if (tabId == R.id.tab_profile) {
                    Intent setting = new Intent(MapsActivity.this, ProfileActivity.class);
                    startActivity(setting);
                }
            }

        });

        // Top Bar
        BottomBar bottomBar2 = (BottomBar) findViewById(R.id.bottomBar2);
        bottomBar2.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_search) {
                    sendRequest();
                } else if (tabId == R.id.tab_directions) {
                    temp = RouteLister.routeList;
                    Intent i = new Intent(MapsActivity.this, routeActivity.class);
                    i.putExtra("FILES_TO_SEND", temp);
                    startActivity(i);
                } /*else if (tabId == R.id.tab_preference_menu) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this, R.style.AppCompatAlertDialogStyle);
                    mSelectedItems = new ArrayList();

                    builder.setTitle("Route Preferences");
                    builder.setMultiChoiceItems(R.array.perferences, null, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which, boolean isSelected) {
                            if (isSelected) {
                                System.out.println("test");
                            } else if (mSelectedItems.contains(which)) {
                                mSelectedItems.remove(Integer.valueOf(which));
                            }
                        }
                    });
                    builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.show();
                }*/

            }

        });

        // Top Bar Reselect
        bottomBar2.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_search) {
                    if (mOrigin.isEmpty()) {
                        Toast.makeText(MapsActivity.this, "Please enter origin address!", Toast.LENGTH_SHORT).show();

                    } else if (mDestination.isEmpty()) {
                        Toast.makeText(MapsActivity.this, "Please enter destination address!", Toast.LENGTH_SHORT).show();

                    } else if (mOrigin.isEmpty() && mDestination.isEmpty()) {
                        Toast.makeText(MapsActivity.this, "Please enter a starting and destination address", Toast.LENGTH_SHORT).show();
                    } else sendRequest();
                }
                if (tabId == R.id.tab_directions) {
                    temp = RouteLister.routeList;
                    Intent i = new Intent(MapsActivity.this, routeActivity.class);
                    i.putExtra("FILES_TO_SEND", temp);
                    startActivity(i);
                }
                if (tabId == R.id.tab_preference_menu) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this, R.style.AppCompatAlertDialogStyle);
                    mSelectedItems = new ArrayList();

                    builder.setTitle("Route Preferences");
                    builder.setMultiChoiceItems(R.array.perferences, null, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which, boolean isSelected) {
                            if (isSelected) {
                                System.out.println("test");
                            } else if (mSelectedItems.contains(which)) {
                                mSelectedItems.remove(Integer.valueOf(which));
                            }
                        }
                    });
                    builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.show();
                }
            }
        });

        mOrigin = "";
        mDestination = "";


        //Google Start Search Bar
        mOriginAutocompleteFragment = (PlaceAutocompleteFragment)

                getFragmentManager().

                        findFragmentById(R.id.place_autocomplete_origin);
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
        mDestinationAutocompleteFragment = (PlaceAutocompleteFragment)

                getFragmentManager().

                        findFragmentById(R.id.place_autocomplete_destination);
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


        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        Userid = uid;
        Log.v("UserID", Userid);


        Geofence geofence = new Geofence.Builder()
                .setRequestId(GEOFENCE_REQ_ID) // Geofence ID
                .setCircularRegion(40.836405, -73.859922, 100) // defining fence region
                .setExpirationDuration(8000) // expiring date

                // Transition types that it should look for
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
        Log.v("fence", "created");
        GeofencingRequest request = new GeofencingRequest.Builder()
                // Notification to trigger when the Geofence is created
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence) // add a Geofence
                .build();

        circleOptions = new CircleOptions()
                .center(testlocation)
                .strokeColor(Color.argb(50, 70, 70, 70))
                .fillColor(Color.argb(100, 150, 150, 150))
                .radius(100);




        //  geoFenceLimits = mMap.addCircle( circleOptions );







    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng new_york = new LatLng(40.758879, -73.985110);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new_york, 12));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(new_york));

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnPoiClickListener(this);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.addCircle(circleOptions);

        //Location Permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_FINE_LOCATION);
            }
        }

        //Map Style
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));


        //Dropping Marker
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogLayout = inflater.inflate(R.layout.drop_dialog, null);


                final SwipeSelector swipeSelector = (SwipeSelector) dialogLayout.findViewById(R.id.swipeSelector);

                swipeSelector.setItems(

                        new SwipeItem(0, "Train Traffic", "Description for slide three."),
                        new SwipeItem(1, "Police Investigation", "Description for slide one."),
                        new SwipeItem(2, "Sick Passenger", "Description for slide two."),
                        new SwipeItem(3, "Signal Malfunction", "Description for slide four."),
                        new SwipeItem(4, "FastTrack", "Description for slide four.")

                );

                SwipeSelector swipeSelector2 = (SwipeSelector) dialogLayout.findViewById(R.id.swipeSelector2);
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

                SwipeSelector swipeSelector3 = (SwipeSelector) dialogLayout.findViewById(R.id.swipeSelector3);
                swipeSelector3.setItems(
                        new SwipeItem(0, "Uptown", "Description for slide one."),
                        new SwipeItem(1, "Downtown", "Description for slide two.")

                );
                builder.setView(dialogLayout);
                builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SwipeItem selectedItem = swipeSelector.getSelectedItem();
                        int value = (Integer) selectedItem.value;
                        if (value == 0) {
                            Marker eventDrop = mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.traffic))
                                    .draggable(false));
                            eventName = "Train Traffic";
                            geoFire.setLocation(Userid, new GeoLocation(latLng.latitude, latLng.longitude));
                        } else if (value == 1) {
                            Marker eventDrop = mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.police))
                                    .draggable(false));
                            eventName = "Police Investigation";
                            geoFire.setLocation(Userid, new GeoLocation(latLng.latitude, latLng.longitude));

                        } else if (value == 2) {
                            Marker eventDrop = mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.sick))
                                    .draggable(false));
                            eventName = "Sick Passenger";
                            geoFire.setLocation(Userid, new GeoLocation(latLng.latitude, latLng.longitude));

                        } else if (value == 3) {
                            Marker eventDrop = mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.signal))
                                    .draggable(false));
                            eventName = "Signal Malfunction";
                            geoFire.setLocation(Userid, new GeoLocation(latLng.latitude, latLng.longitude));

                        } else if (value == 4) {
                            Marker eventDrop = mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.fasttrack))
                                    .draggable(false));
                            eventName = "FastTrack";
                            geoFire.setLocation(Userid, new GeoLocation(latLng.latitude, latLng.longitude));

                        }

                    }
                });
                builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();


                geoFire.getLocation(Userid, new LocationCallback() {
                    @Override
                    public void onLocationResult(String key, GeoLocation location) {
                        if (location != null) {
                            System.out.println(String.format("The location for key %s is [%f,%f]", key, location.latitude, location.longitude));
                        } else {
                            System.out.println(String.format("There is no location for key %s in GeoFire", key));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.err.println("There was an error getting the GeoFire location: " + databaseError);
                    }


                });
            }


        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker arg0) {


                /*AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                LayoutInflater inflater = MapsActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.bottomdialog_layout, null);


                ImageButton upvoteButton = (ImageButton) findViewById(R.id.upvote);
                ImageButton downvoteButton = (ImageButton) findViewById(R.id.downvote);

                displayName = (TextView) dialogView.findViewById(R.id.user);
                repScore = (TextView) dialogView.findViewById(R.id.score);

                Bundle extras = getIntent().getExtras();
                String NAME = extras.getString("userName");
                displayName.setText("By:" + NAME);

                builder.setTitle(eventName)
                        .setIcon(R.drawable.icon)
                        .setMessage("test")
                        .setCancelable(true);

                dialog = builder.create();






                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

                        builder.setView(dialogView);
                        dialog = builder.show();

getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);

                return true;*/


                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View customView = inflater.inflate(R.layout.bottomdialog_layout, null);
                ImageButton upvoteButton = (ImageButton) customView.findViewById(R.id.upvote);
                ImageButton downvoteButton = (ImageButton) customView.findViewById(R.id.downvote);
                //TextView textView = (TextView) customView.findViewById(R.id.);
                //String comment = textView.getText().toString();
                
                displayName = (TextView) findViewById(R.id.user);
                repScore = (TextView) findViewById(R.id.score);

                BottomDialog bottomDialog = new BottomDialog.Builder(MapsActivity.this)

                        .setIcon(R.drawable.icon)
                        .setTitle(eventName)
                        .setCustomView(customView)
                        .setContent("comment")


            .build();



                bottomDialog.show();
                return true;
            }


        });
    }



    //Search Button Request
    private void sendRequest() {
        if (mOrigin.isEmpty()) {
            Toast.makeText(this, "Please enter origin address!", Toast.LENGTH_SHORT).show();

        } else if (mDestination.isEmpty()) {
            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();

        } else if (mOrigin.isEmpty() && mDestination.isEmpty()) {
            Toast.makeText(this, "Please enter a starting and destination address", Toast.LENGTH_SHORT).show();

        } else {
            try {
                new DirectionFinder(this, mOrigin, mDestination).execute();
                new RouteLister(MapsActivity.this, mOrigin, mDestination).execute();
            } catch (Exception e) {
                Log.e("Find route", e.getMessage());
                e.printStackTrace();
            }
        }
        //Toast.makeText(this,RouteLister.routes.get(0).getArrivalTime(),Toast.LENGTH_LONG).show();
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
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            // ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            // ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);

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
                    .width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));

        }
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();

    }


    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    //After Permission granted for location
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


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            mLatitudeText = Double.parseDouble(String.valueOf(mLastLocation.getLatitude()));

            mLongitudeText = Double.parseDouble(String.valueOf(mLastLocation.getLongitude()));
            Log.v("Mylocation", mLatitudeText + "," + mLongitudeText);

        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }


}




