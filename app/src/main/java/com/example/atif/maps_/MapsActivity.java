package com.example.atif.maps_;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.geofire.LocationCallback;
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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnPoiClickListener, ActivityCompat.OnRequestPermissionsResultCallback, GoogleApiClient.OnConnectionFailedListener, DirectionFinderListener, GoogleMap.OnInfoWindowClickListener, LocationListener, GoogleApiClient.ConnectionCallbacks {

    private static final int MY_PERMISSION_FINE_LOCATION = 101;
    ArrayList<RouteOption> temp;
    ArrayList<MapsActivity> mSelectedItems;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
    GeoFire geoFire = new GeoFire(ref);
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

    Location mLastLocation;
    private double mLatitudeText;
    private double mLongitudeText;

    GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mLatitudeText, mLongitudeText), 1.0);


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
                    Intent setting = new Intent(MapsActivity.this, SettingsActivity.class);
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
        System.out.println("USERID " + uid);
        Userid = uid;



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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng new_york = new LatLng(40.758879, -73.985110);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new_york, 12));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(new_york));

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


        //Dropping Marker
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {
                //Log.v("lat_lng", latLng.latitude + "," + latLng.longitude);
                Marker eventDrop = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Select Event:")
                        .snippet("Police Investigation")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.police))
                        .draggable(false));
                geoFire.setLocation(Userid, new GeoLocation(latLng.latitude, latLng.longitude));






                /*AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogLayout = inflater.inflate(R.layout.drop_dialog, null);
                builder.setView(dialogLayout);
                builder.create();
*/
                /*SwipeSelector swipeSelector = (SwipeSelector) findViewById(R.id.swipeSelector);
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

                );*/
                //builder.show();

                /*  builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Marker eventDrop = mMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title("Select Event:")
                                .snippet("Police Investigation")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.police))
                                .draggable(false));
                        geoFire.setLocation("Drop1", new GeoLocation(latLng.latitude, latLng.longitude));
                    }
                });

                builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });*/

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

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {

            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                //System.out.println(String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));
                Log.d("Tag1","Key %s entered the search area at [%f,%f]");
            }

            @Override
            public void onKeyExited(String key) {
                //System.out.println(String.format("Key %s is no longer in the search area", key));
                Log.d("Tag2","Key %s is no longer in the search area");

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                //System.out.println(String.format("Key %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude));
                Log.d("Tag3","Key %s moved within the search area to [%f,%f]");

            }

            @Override
            public void onGeoQueryReady() {
                //System.out.println("All initial data has been loaded and events have been fired!");
                Log.d("Tag4","All initial data has been loaded and events have been fired!");

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                //System.err.println("There was an error with this query: " + error);
                Log.d("Tag5","There was an error with this query: ");

            }
        });
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, "Info window clicked",
                Toast.LENGTH_SHORT).show();
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




