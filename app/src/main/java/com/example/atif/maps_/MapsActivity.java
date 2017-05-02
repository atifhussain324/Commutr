package com.example.atif.maps_;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;
import com.roughike.swipeselector.SwipeItem;
import com.roughike.swipeselector.SwipeSelector;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
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
    private Location mLastLocation;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private String mOrigin, mDestination;
    private ProgressDialog progressDialog;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private PlaceAutocompleteFragment mOriginAutocompleteFragment, mDestinationAutocompleteFragment;
    private DatabaseReference mDatabase;
    private FirebaseUser loggedUser;
    private double mLatitudeText;
    private double mLongitudeText;
    private TextView displayName;
    private TextView repScore;
    private TextView postTime;
    private String eventName;
    private Drawable icon;
    private String dropName;
    private View customView;

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
        final BottomBarTab directionBadge = bottomBar2.getTabWithId(R.id.tab_directions);
        bottomBar2.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_search:
                        sendRequest();
                        directionBadge.setBadgeCount(1);

                        break;
                    case R.id.tab_directions:
                        temp = RouteLister.routeList;
                        Intent i = new Intent(MapsActivity.this, routeActivity.class);
                        i.putExtra("FILES_TO_SEND", temp);
                        Log.v("routetest", "test");
                        startActivity(i);
                        break;
                }


            }

        });

        // Top Bar Reselect
        bottomBar2.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_search:
                        if (mOrigin.isEmpty()) {
                            Toast.makeText(MapsActivity.this, "Please enter origin address!", Toast.LENGTH_SHORT).show();

                        } else if (mDestination.isEmpty()) {
                            Toast.makeText(MapsActivity.this, "Please enter destination address!", Toast.LENGTH_SHORT).show();

                        } else if (mOrigin.isEmpty() && mDestination.isEmpty()) {
                            Toast.makeText(MapsActivity.this, "Please enter a starting and destination address", Toast.LENGTH_SHORT).show();
                        } else sendRequest();
                        break;
                    case R.id.tab_directions:
                        temp = RouteLister.routeList;
                        Intent i = new Intent(MapsActivity.this, routeActivity.class);
                        i.putExtra("FILES_TO_SEND", temp);
                        Log.v("routetest", "test");
                        startActivity(i);
                        break;
                    case R.id.tab_preference_menu:
                        new MaterialDialog.Builder(MapsActivity.this)
                                .title("Route Preferences")
                                .items(R.array.perferences)
                                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                                    @Override
                                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                        Intent i = new Intent(getApplicationContext(), RouteLister.class);
                                        String preference;
                                        if (which == 0) {
                                            Log.v("Dialog", "Best Route");
                                            preference = "";
                                            i.putExtra("preference", preference);
                                            startActivity(i);

                                        }
                                        if (which == 1) {
                                            Log.v("Dialog", "Less walking");
                                            preference = "transit_routing_preference=less_walking";
                                            i.putExtra("preference", preference);
                                            startActivity(i);
                                        }
                                        if (which == 2) {
                                            Log.v("Dialog", "Fewer transfer");
                                            preference = "transit_routing_preference=fewer_transfers";
                                            i.putExtra("preference", preference);
                                            startActivity(i);

                                        }
                                        return true;
                                    }
                                })
                                .negativeText(R.string.Cancel)
                                .positiveText(R.string.OK)
                                .show();
                        break;
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

        if (mGoogleApiClient == null)

        {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng new_york = new LatLng(40.758879, -73.985110);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new_york, 12));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnPoiClickListener(this);
        mMap.getUiSettings().setMapToolbarEnabled(false);

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


        //Reading Database and Displaying Existing Markers
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {


                Iterator<DataSnapshot> iterator = snapshot.getChildren().iterator();
                while (iterator.hasNext()) {
                    snapshot = iterator.next();

                    BitmapDescriptor icon;
                    String userID = snapshot.getKey();
                    Log.v("snapshotUID", userID);

                    if (snapshot.hasChild("marker")) {
                        Log.v("snapshotLat", "rakhaa");
                        String lat = snapshot.child("marker").child("latlng").child("latitude").getValue().toString();
                        String lng = snapshot.child("marker").child("latlng").child("longitude").getValue().toString();
                        LatLng latlng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                        Log.v("snapshotLat", latlng.toString());
                        String eventName = snapshot.child("marker").child("name").getValue().toString();
                        Log.v("snapshotName", eventName);
                        String expirationTime = snapshot.child("marker").child("expiration").getValue().toString();


                        switch (eventName) {

                            case "Police Investigation":
                                icon = BitmapDescriptorFactory.fromResource(R.drawable.police);
                                break;
                            case "Sick Passenger":
                                icon = BitmapDescriptorFactory.fromResource(R.drawable.sick);
                                break;
                            case "Signal Malfunction":
                                icon = BitmapDescriptorFactory.fromResource(R.drawable.signal);
                                break;
                            case "FastTrack":
                                icon = BitmapDescriptorFactory.fromResource(R.drawable.fasttrack);
                                break;
                            default:
                                icon = BitmapDescriptorFactory.fromResource(R.drawable.traffic);
                                break;

                        }

                        try {
                            SimpleDateFormat parser = new SimpleDateFormat(("MM/dd/yyyy HH:mm"));
                            Date expTime = parser.parse(expirationTime);
                            Calendar currentT = GregorianCalendar.getInstance();
                            Date cTime = currentT.getTime();
                            Log.v("expTime", expTime.toString());
                            Log.v("cTime", cTime.toString());
                            if (expTime.after(cTime)) {

                                Log.v("TimeTest", "If executed");
                                mMap.addMarker(new MarkerOptions()
                                        .position(latlng)
                                        .icon(icon)
                                        .title(userID)
                                        .draggable(false));
                            } else {
                                Log.v("TimeTest", "Else executed");
                                snapshot.child("marker").getRef().removeValue();


                            }
                        } catch (ParseException e) {
                            Log.v("TimeTest", "parse exception caught");
                        }


                    }

                }//Iterator ends here


            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Log.w("dbtest", "loadPost:onCancelled", databaseError.toException());
            }
        });


        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild("marker")) {
                    Log.v("Marker1", "If");
                    Toast.makeText(MapsActivity.this, "This is my Toast message!",
                            Toast.LENGTH_LONG).show();
                } else {
                    Log.v("Marker1", "Else");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //Dropping Own Marker
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {
                mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    public void onDataChange(DataSnapshot snapshot) {
                        // Reads if Marker exists for user in database
                        if (snapshot.hasChild("marker")) {
                            Log.v("Marker1", "If");
                            Toast.makeText(MapsActivity.this, "You have dropped an alert too recently!",
                                    Toast.LENGTH_LONG).show();
                        }
                        // Allows for marker drop if one does not exist
                        else {
                            Log.v("Marker1", "Else");
                            AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                            LayoutInflater inflater = getLayoutInflater();
                            View dialogLayout = inflater.inflate(R.layout.drop_dialog, null);

                            final SwipeSelector swipeSelector = (SwipeSelector) dialogLayout.findViewById(R.id.swipeSelector);
                            swipeSelector.setItems(

                                    new SwipeItem(0, "Train Traffic", "Delay due to train traffic ahead"),
                                    new SwipeItem(1, "Police Investigation", "Police presence at station"),
                                    new SwipeItem(2, "Sick Passenger", "Delay due to medical attention needed for passenger"),
                                    new SwipeItem(3, "Signal Malfunction", "Delay due to signal issues at station"),
                                    new SwipeItem(4, "FastTrack", "Planned MTA construction")

                            );

                            final SwipeSelector swipeSelector2 = (SwipeSelector) dialogLayout.findViewById(R.id.swipeSelector2);
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
                                    new SwipeItem(0, "Uptown", "Train headed Uptown"),
                                    new SwipeItem(1, "Downtown", "Train headed Downtown")

                            );


                            builder.setView(dialogLayout);
                            builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    loggedUser = FirebaseAuth.getInstance().getCurrentUser();
                                    mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(loggedUser.getUid());
                                    Calendar expirationTime = GregorianCalendar.getInstance();
                                    Calendar postedTime = GregorianCalendar.getInstance();
                                    SimpleDateFormat timeFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                                    SimpleDateFormat timeFormat12 = new SimpleDateFormat("h:mm a");
                                    expirationTime.add(GregorianCalendar.MINUTE, 10);

                                    SwipeItem selectedItem2 = swipeSelector2.getSelectedItem();
                                    int iconValue = (Integer) selectedItem2.value;

                                    SwipeItem selectedItem = swipeSelector.getSelectedItem();
                                    int value = (Integer) selectedItem.value;

                                    switch (value) {
                                        case 0:
                                            Marker eventDrop = mMap.addMarker(new MarkerOptions()
                                                    .position(latLng)
                                                    .title(loggedUser.getUid())
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.traffic))
                                                    .draggable(false));
                                            eventName = "Train Traffic";
                                            mDatabase.child("marker").child("latlng").setValue(latLng);
                                            mDatabase.child("marker").child("name").setValue(eventName);
                                            mDatabase.child("marker").child("expiration").setValue(timeFormat.format(expirationTime.getTime()));
                                            mDatabase.child("marker").child("icon").setValue(iconValue);
                                            mDatabase.child("marker").child("postedTime").setValue(timeFormat.format(postedTime.getTime()));
                                            mDatabase.child("marker").child("postedTime12").setValue(timeFormat12.format(postedTime.getTime()));

                                            break;
                                        case 1:
                                            Marker eventDrop1 = mMap.addMarker(new MarkerOptions()
                                                    .position(latLng)
                                                    .title(loggedUser.getUid())
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.police))
                                                    .draggable(false));
                                            eventName = "Police Investigation";
                                            mDatabase.child("marker").child("latlng").setValue(latLng);
                                            mDatabase.child("marker").child("name").setValue(eventName);
                                            mDatabase.child("marker").child("expiration").setValue(timeFormat.format(expirationTime.getTime()));
                                            mDatabase.child("marker").child("icon").setValue(iconValue);
                                            mDatabase.child("marker").child("postedTime").setValue(timeFormat.format(postedTime.getTime()));
                                            mDatabase.child("marker").child("postedTime12").setValue(timeFormat12.format(postedTime.getTime()));

                                            break;
                                        case 2:
                                            Marker eventDrop2 = mMap.addMarker(new MarkerOptions()
                                                    .position(latLng)
                                                    .title(loggedUser.getUid())
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.sick))
                                                    .draggable(false));
                                            eventName = "Sick Passenger";
                                            mDatabase.child("marker").child("latlng").setValue(latLng);
                                            mDatabase.child("marker").child("name").setValue(eventName);
                                            mDatabase.child("marker").child("expiration").setValue(timeFormat.format(expirationTime.getTime()));
                                            mDatabase.child("marker").child("icon").setValue(iconValue);
                                            mDatabase.child("marker").child("postedTime").setValue(timeFormat.format(postedTime.getTime()));
                                            mDatabase.child("marker").child("postedTime12").setValue(timeFormat12.format(postedTime.getTime()));

                                            break;
                                        case 3:
                                            Marker eventDrop3 = mMap.addMarker(new MarkerOptions()
                                                    .position(latLng)
                                                    .title(loggedUser.getUid())
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.signal))
                                                    .draggable(false));
                                            eventName = "Signal Malfunction";
                                            mDatabase.child("marker").child("latlng").setValue(latLng);
                                            mDatabase.child("marker").child("name").setValue(eventName);
                                            mDatabase.child("marker").child("expiration").setValue(timeFormat.format(expirationTime.getTime()));
                                            mDatabase.child("marker").child("icon").setValue(iconValue);
                                            mDatabase.child("marker").child("postedTime").setValue(timeFormat.format(postedTime.getTime()));
                                            mDatabase.child("marker").child("postedTime12").setValue(timeFormat12.format(postedTime.getTime()));

                                            break;
                                        default:
                                            Marker eventDrop4 = mMap.addMarker(new MarkerOptions()
                                                    .position(latLng)
                                                    .title(loggedUser.getUid())
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.fasttrack))
                                                    .draggable(false));
                                            eventName = "FastTrack";
                                            mDatabase.child("marker").child("latlng").setValue(latLng);
                                            mDatabase.child("marker").child("name").setValue(eventName);
                                            mDatabase.child("marker").child("expiration").setValue(timeFormat.format(expirationTime.getTime()));
                                            mDatabase.child("marker").child("icon").setValue(iconValue);
                                            mDatabase.child("marker").child("postedTime").setValue(timeFormat.format(postedTime.getTime()));
                                            mDatabase.child("marker").child("postedTime12").setValue(timeFormat12.format(postedTime.getTime()));

                                            break;

                                    }
                                    //mDatabase.child("marker").
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            builder.show();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker arg0) {

                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                customView = inflater.inflate(R.layout.bottomdialog_layout, null);

                ImageButton upvoteButton = (ImageButton) customView.findViewById(R.id.upvote);
                ImageButton downvoteButton = (ImageButton) customView.findViewById(R.id.downvote);

                upvoteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

                downvoteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                displayName = (TextView) customView.findViewById(R.id.user);
                repScore = (TextView) customView.findViewById(R.id.score);
                postTime = (TextView) customView.findViewById(R.id.postedTime);

                mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    public void onDataChange(DataSnapshot snapshot) {
                        String fName = snapshot.child("users").child(arg0.getTitle()).child("firstName").getValue().toString();
                        String lName = snapshot.child("users").child(arg0.getTitle()).child("lastName").getValue().toString();
                        String iconIndex = snapshot.child("users").child(arg0.getTitle()).child("marker").child("icon").getValue().toString();
                        dropName = snapshot.child("users").child(arg0.getTitle()).child("marker").child("name").getValue().toString();
                        String postedTime = snapshot.child("users").child(arg0.getTitle()).child("marker").child("postedTime12").getValue().toString();

                       /* try {
                            SimpleDateFormat timeFormat12 = new SimpleDateFormat("h:mma");
                            Date pTime= timeFormat12.parse(postedTime);
                            postedTime = timeFormat12.format(pTime);
                        }
                        catch (ParseException e){

                        }*/
                        switch (iconIndex) {
                            default:
                                icon = getResources().getDrawable(R.drawable.t1);
                                break;
                            case "1":
                                icon = getResources().getDrawable(R.drawable.t2);
                                break;
                            case "2":
                                icon = getResources().getDrawable(R.drawable.t3);
                                break;
                            case "3":
                                icon = getResources().getDrawable(R.drawable.t4);
                                break;
                            case "4":
                                icon = getResources().getDrawable(R.drawable.t5);
                                break;
                            case "5":
                                icon = getResources().getDrawable(R.drawable.t6);
                                break;
                            case "6":
                                icon = getResources().getDrawable(R.drawable.t7);
                                break;
                            case "7":
                                icon = getResources().getDrawable(R.drawable.ta);
                                break;
                            case "8":
                                icon = getResources().getDrawable(R.drawable.tb);
                                break;
                            case "9":
                                icon = getResources().getDrawable(R.drawable.tc);
                                break;
                            case "10":
                                icon = getResources().getDrawable(R.drawable.td);
                                break;
                            case "11":
                                icon = getResources().getDrawable(R.drawable.te);
                                break;
                            case "12":
                                icon = getResources().getDrawable(R.drawable.tf);
                                break;
                            case "13":
                                icon = getResources().getDrawable(R.drawable.tg);
                                break;
                            case "14":
                                icon = getResources().getDrawable(R.drawable.tj);
                                break;
                            case "15":
                                icon = getResources().getDrawable(R.drawable.tl);
                                break;
                            case "16":
                                icon = getResources().getDrawable(R.drawable.tm);
                                break;
                            case "17":
                                icon = getResources().getDrawable(R.drawable.tn);
                                break;
                            case "18":
                                icon = getResources().getDrawable(R.drawable.tq);
                                break;
                            case "19":
                                icon = getResources().getDrawable(R.drawable.tr);
                                break;
                            case "20":
                                icon = getResources().getDrawable(R.drawable.tr);
                                break;
                            case "21":
                                icon = getResources().getDrawable(R.drawable.tz);
                                break;
                            case "22":
                                icon = getResources().getDrawable(R.drawable.ts);
                                break;
                        }


                        if (fName != null) {
                            displayName.setText("By: " + fName + " " + lName);
                            repScore.setText("120");
                            postTime.setText(postedTime);
                            BottomDialog bottomDialog = new BottomDialog.Builder(MapsActivity.this)

                                    .setIcon(icon)
                                    .setTitle(dropName)
                                    .setCustomView(customView)
                                    .setContent("comment")
                                    .build();
                            bottomDialog.show();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //Log.w("dbtest", "loadPost:onCancelled", databaseError.toException());
                    }
                });


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
      /*  Toast.makeText(getApplicationContext(), "Clicked: " +
                        poi.name + "\nPlace ID:" + poi.placeId +
                        "\nLatitude:" + poi.latLng.latitude +
                        " Longitude:" + poi.latLng.longitude,
                Toast.LENGTH_SHORT).show();*/
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




