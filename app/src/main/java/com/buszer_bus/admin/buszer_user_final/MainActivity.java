package com.buszer_bus.admin.buszer_user_final;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;

import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.location.LocationListener;
import com.github.clans.fab.FloatingActionButton;

import android.view.Window;

import android.net.Uri;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import static com.buszer_bus.admin.buszer_user_final.R.layout.dialog;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMapReadyCallback, BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mroot = FirebaseDatabase.getInstance().getReference();
    DatabaseReference passenger =  mroot.child("Passenger").getRef();

    DatabaseReference Bus_location_seats = mroot.child("Bus_location_seats").getRef();
    DatabaseReference mBooking = mroot.child("Booking").getRef();

   public static final String tag = "No";

    private GoogleApiClient mgoogle_client;
    private LocationRequest mlocationrequest;
    private FusedLocationProviderApi locationprovider = LocationServices.FusedLocationApi;
    private static final int MY_PERMISSION_REQUEST_FINE_LOCATION = 101;
    private static final int MY_PERMSSION_REQUEST_COURSE_LOCATION = 102;
    private boolean permissionIsGranted = false;
    GoogleMap mgooglemap;
    Marker marker, bus_marker, bus_marker2;



 Integer bus1_available_seats, update_seat1;
  Integer bus2_available_seats2 , update_seat2;

    public FirebaseAuth firebaseAuth;

    Boolean ismapReady = false;
    private static final String TAG = "myApp";

     private static Integer slotint;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toogle;
    NavigationView nav_menu;
    public String received_email, received_name, received_username, received_address ;

    BottomNavigationView bottomNavigationView;
    private LatLng ll;
    public double lat_send, lng_send;
    public ProgressDialog loader;

    public String userid , vacant_string;
    FirebaseUser user_email, user_id;
    public  double bus1_lat, bus1_lng , bus2_lat, bus2_lng;

    LatLng Bus1_location , Bus2_location;

    FloatingActionMenu map_menu;
    FloatingActionButton hybrid,normal,satellite;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.getSupportActionBar().hide();

        hybrid = (FloatingActionButton) findViewById(R.id.hybrid);
        normal = (FloatingActionButton) findViewById(R.id.normal);
        satellite = (FloatingActionButton) findViewById(R.id.satellite);
        map_menu = (FloatingActionMenu) findViewById(R.id.fab_menu);



        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser();
        userid = user_id.getUid();

        fetching_user();
        fetching_bus_location();
        fetching_bus_location2();
        loader = new ProgressDialog(this);



        init();

        mgoogle_client = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mlocationrequest = new LocationRequest();
        mlocationrequest.setInterval(100);
        mlocationrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        hybrid.setOnClickListener(this);
        normal.setOnClickListener(this);
        satellite.setOnClickListener(this);
    }

    private void fetching_bus_location() {
        Bus_location_seats.child("000333").child("Lat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bus1_lat = dataSnapshot.getValue(double.class);

                movemarker();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Bus_location_seats.child("000333").child("Lng").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bus1_lng = dataSnapshot.getValue(double.class);
                movemarker();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Bus_location_seats.child("000333").child("Vacant").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bus1_available_seats = dataSnapshot.getValue(Integer.class);



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void fetching_bus_location2() {

        Bus_location_seats.child("000222").child("Lat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bus2_lat = dataSnapshot.getValue(double.class);

                movemarker2();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Bus_location_seats.child("000222").child("Lng").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bus2_lng = dataSnapshot.getValue(double.class);
                movemarker2();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Bus_location_seats.child("000222").child("Vacant").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bus2_available_seats2 = dataSnapshot.getValue(Integer.class);
                movemarker2();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }







    private void init() {
        MapFragment mapfragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapfragment.getMapAsync(this);


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        requesLocationUpdate();
    }

    private void requesLocationUpdate() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_FINE_LOCATION);
            } else {
                permissionIsGranted = true;
            }
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mgoogle_client, mlocationrequest, this);
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public void onLocationChanged(Location location) {

        lat_send = location.getLatitude();
        lng_send = location.getLongitude();

        ll = new LatLng(location.getLatitude(), location.getLongitude());


        if(ll== null){
            Toast.makeText(getApplicationContext(),"GPS or Internet Error",Toast.LENGTH_LONG).show();
        }

        if (marker != null) {
            marker.remove();
        }
        MarkerOptions options = new MarkerOptions()
                .title("I'm Here")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.my))
                .position(ll);
        marker = mgooglemap.addMarker(options);

        movemarker();
        movemarker2();
    }


    @Override
    protected void onStart() {
        super.onStart();
        mgoogle_client.connect();


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (permissionIsGranted) {
            if (mgoogle_client.isConnected()) {
                requesLocationUpdate();

            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (permissionIsGranted)
            LocationServices.FusedLocationApi.removeLocationUpdates(mgoogle_client, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (permissionIsGranted)
            mgoogle_client.disconnect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mgooglemap = googleMap;
        ismapReady = true;

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case MY_PERMISSION_REQUEST_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionIsGranted = true;
                } else {
                    permissionIsGranted = false;
                    Toast.makeText(getApplicationContext(), "Permission is denied", Toast.LENGTH_LONG).show();
                }
                break;
            case MY_PERMSSION_REQUEST_COURSE_LOCATION:
                break;

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toogle.onOptionsItemSelected(item)) {
            return true;

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.my_location:
                if (ll == null){Toast.makeText(MainActivity.this,"No location yet",Toast.LENGTH_LONG).show(); return true; }
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 15);
                mgooglemap.animateCamera(update);

                break;
            case R.id.reserve:
                dialog();
                break;
            case R.id.log_out:

                  Logout logut = new Logout(this.getApplicationContext(), MainActivity.this);
                     logut.logout_dialog(loader,firebaseAuth);


                break;
        }
        return false;
    }

    private void dialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mview = getLayoutInflater().inflate(R.layout.dialog, null);

        final EditText etbus = (EditText) mview.findViewById(R.id.bus_number);
        final EditText etqty = (EditText) mview.findViewById(R.id.reserve_number);
        final Button book = (Button) mview.findViewById(R.id.book);
        final Button cancel = (Button) mview.findViewById(R.id.cancel);
        mBuilder.setView(mview);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if (etbus.getText().toString().isEmpty()) {
                        Toast.makeText(MainActivity.this, "Bus No. is Required", Toast.LENGTH_LONG).show();
                        return;
                    } else if (etqty.getText().toString().isEmpty()) {
                        Toast.makeText(MainActivity.this, "Fillout Qty", Toast.LENGTH_LONG).show();
                        return;
                    }

                    String busnumber = etbus.getText().toString();
                    Integer qty = Integer.parseInt(etqty.getText().toString());

                     if(busnumber.equals("000333"))

                     {

                         if (bus1_available_seats < qty) {

                             Toast.makeText(MainActivity.this, "Only " + bus1_available_seats + " seats available", Toast.LENGTH_LONG).show();

                         } else {

                             update_seat1 = bus1_available_seats - qty;

                             if (update_seat1 < 0)

                             {
                                 update_seat1 = 0;
                             }
                             Bus_location_seats.child(busnumber).child("Vacant").setValue(update_seat1);
                             DatabaseReference Bookers_id = mBooking.child(busnumber).child("Booker_ID");
                             Bookers_id.setValue(received_username);

                             DatabaseReference Booked = mBooking.child(busnumber).child(received_username);
                             Booked.child("Name").setValue(received_name);
                             Booked.child("Qty_reserved").setValue(qty);
                             Booked.child("Lat").setValue(lat_send);
                             Booked.child("Lng").setValue(lng_send);
                             Toast.makeText(MainActivity.this, "Success Fully Booked", Toast.LENGTH_LONG).show();

                             dialog.dismiss();

                         }

                     }else if(busnumber.equals("000222")){
                         if (bus2_available_seats2 < qty) {

                             Toast.makeText(MainActivity.this, "Only " + bus2_available_seats2 + " seats available", Toast.LENGTH_LONG).show();

                         } else {

                             update_seat2 = bus2_available_seats2 - qty;

                             if (update_seat2 < 0)

                             {
                                 update_seat2 = 0;
                             }
                             Bus_location_seats.child(busnumber).child("Vacant").setValue(update_seat2);
                             DatabaseReference Bookers_id = mBooking.child(busnumber).child("Booker_ID");
                             Bookers_id.setValue(received_username);

                             DatabaseReference Booked = mBooking.child(busnumber).child(received_username);
                             Booked.child("Name").setValue(received_name);
                             Booked.child("Qty_reserved").setValue(qty);
                             Booked.child("Lat").setValue(lat_send);
                             Booked.child("Lng").setValue(lng_send);
                             Toast.makeText(MainActivity.this, "Success Fully Booked", Toast.LENGTH_LONG).show();

                             dialog.dismiss();

                         }
















                     }


                }







        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });



    }


    private void fetching_user() {


        passenger.child(userid).child("Username").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                received_username = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        passenger.child(userid).child("Name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                received_name = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        passenger.child(userid).child("Email").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                received_email = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void movemarker() {

        Double lat1 = bus1_lat;
        Double lng1 = bus1_lng;
        Bus1_location = new LatLng(lat1,lng1);
        if (bus_marker != null) {
            bus_marker.remove();
        }

        MarkerOptions options = new MarkerOptions()
                .title("Bus No:000333")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.bus))
                .snippet("Vacant Seat: "+  bus1_available_seats)
                .position(Bus1_location);
        bus_marker = mgooglemap.addMarker(options);
        mgooglemap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                bus_marker.showInfoWindow();
                bus_marker2.hideInfoWindow();
                return false;
            }
        });


    }

    private void movemarker2() {

        Double lat2 = bus2_lat;
        Double lng2 = bus2_lng;
        Bus2_location = new LatLng(lat2,lng2);
        if (bus_marker2 != null) {
            bus_marker2.remove();
        }

        MarkerOptions options = new MarkerOptions()
                .title("Bus No:000222")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.bus))
                .snippet("Vacant Seat: "+  bus2_available_seats2)
                .position(Bus2_location);
        bus_marker2 = mgooglemap.addMarker(options);



        mgooglemap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                bus_marker.hideInfoWindow();
                bus_marker2.showInfoWindow();
              return false;
            }
        });

    }




   public void onClick(View view){
        int  id = view.getId();

        switch (id){
            case R.id.satellite:
                mgooglemap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case R.id.hybrid:
                mgooglemap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.normal:
                mgooglemap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;

        }



   }

    @Override
    public void onBackPressed() {

        Toast.makeText(this.getApplicationContext(),"Use the exit button to sign-out",Toast.LENGTH_LONG).show();


    }


}



