package com.example.mytrackeeapp;

import android.Manifest;
import android.Manifest.permission;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.Projection;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

  final static int PERMISSION_ALL = 1;
  final static String[] PERMISSIONS = {permission.ACCESS_COARSE_LOCATION,
      permission.ACCESS_FINE_LOCATION};
  private GoogleMap mMap;
  LatLng myCoordinates;
  LocationManager locationManager;
  MarkerOptions markerOptions;
  Marker marker;
  private Handler handler;

  public final static int SENDING = 1;
  public final static int CONNECTING = 2;
  public final static int ERROR = 3;
  public final static int SENT = 4;
  public final static int SHUTDOWN = 5;

  private static final String TAG = "LocationActivity";
  private static final long INTERVAL = 1000 * 10;
  private static final long FASTEST_INTERVAL = 1000 * 5;

  LocationRequest mLocationRequest;
  GoogleApiClient mGoogleApiClient;
  Location mCurrentLocation;
  private Location previousLocation;

  protected void createLocationRequest() {
    mLocationRequest = new LocationRequest();
    mLocationRequest.setInterval(INTERVAL);
    mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_maps);
    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);
    locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    double longitude = 0;
    double latitude = 0;
//    getIntent().getDoubleExtra("longitude", longitude);
//    getIntent().getDoubleExtra("latitude", latitude);
//    markerOptions = new MarkerOptions().position(new LatLng(longitude, latitude)).title("My Current Location");
    createLocationRequest();
    mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addApi(LocationServices.API)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .build();


    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
     mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);

    handler = new Handler() {
      @Override
      public void handleMessage(Message msg) {
        switch (msg.what) {

          case SENDING:

            break;

        }

      }
    };

    if (VERSION.SDK_INT >= 23 && isPermissionGrandted()) {
      requestPermissions(PERMISSIONS, PERMISSION_ALL);
    } else {
      requestLocation();
    }
    if (!isLocationEnabled()) {
      showAlert(1);
    }
  }


  /**
   * Manipulates the map once available. This callback is triggered when the map is ready to be
   * used. This is where we can add markers or lines, add listeners or move the camera. In this
   * case, we just add a marker near Sydney, Australia. If Google Play services is not installed on
   * the device, the user will be prompted to install it inside the SupportMapFragment. This method
   * will only be triggered once the user has installed Google Play services and returned to the
   * app.
   */
  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;

    // Add a marker in Sydney and move the camera
    LatLng sydney = new LatLng(-34, 151);

    marker = mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in " +
        "Sydney"));
    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED) {
      mMap.setMyLocationEnabled(true);
    } else {
      // Show rationale and request permission.
    }

    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED) {
      mMap.setMyLocationEnabled(true);
    } else {
      // Show rationale and request permission.
    }




    // Add a marker in Sydney and move the camera
//    requestLocation();

//    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

//    LatLng sydney = new LatLng(-34, 151);

//    mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//    mMap.addMarker(this.markerOptions);
//    mMap.moveCamera(CameraUpdateFactory.newLatLng(this.markerOptions.getPosition()));
  }

//  @Override
//  public void onLocationChanged(Location location) {
//    LatLng myCoordinates = new LatLng(location.getLatitude(), location.getLongitude());
//    marker.setPosition(myCoordinates);
//    mMap.moveCamera(CameraUpdateFactory.newLatLng(myCoordinates));
//
//  }

  public void rotateMarker(final Marker marker, final float toRotation, final float st) {
    final Handler handler = new Handler();
    final long start = SystemClock.uptimeMillis();
    final float startRotation = st;
    final long duration = 1555;

    final Interpolator interpolator = new LinearInterpolator();

    handler.post(new Runnable() {
      @Override
      public void run() {
        long elapsed = SystemClock.uptimeMillis() - start;
        float t = interpolator.getInterpolation((float) elapsed / duration);

        float rot = t * toRotation + (1 - t) * startRotation;

        marker.setRotation(-rot > 180 ? rot / 2 : rot);
        if (t < 1.0) {
          // Post again 16ms later.
          handler.postDelayed(this, 16);
        }
      }
    });
  }

  public void animateMarker(final LatLng toPosition, final boolean hideMarke) {
    final Handler handler = new Handler();
    final long start = SystemClock.uptimeMillis();
    Projection proj = mMap.getProjection();
    Point startPoint = proj.toScreenLocation(marker.getPosition());
    final LatLng startLatLng = proj.fromScreenLocation(startPoint);
    final long duration = 5000;

    final Interpolator interpolator = new LinearInterpolator();

    handler.post(new Runnable() {
      @Override
      public void run() {
        long elapsed = SystemClock.uptimeMillis() - start;
        float t = interpolator.getInterpolation((float) elapsed
            / duration);
        double lng = t * toPosition.longitude + (1 - t)
            * startLatLng.longitude;
        double lat = t * toPosition.latitude + (1 - t)
            * startLatLng.latitude;
        marker.setPosition(new LatLng(lat, lng));

        if (t < 1.0) {
          // Post again 16ms later.
          handler.postDelayed(this, 16);
        } else {
          if (hideMarke) {
            marker.setVisible(false);
          } else {
            marker.setVisible(true);
          }
        }
      }
    });
  }

  private double bearingBetweenLocations(LatLng latLng1, LatLng latLng2) {

    double PI = 3.14159;
    double lat1 = latLng1.latitude * PI / 180;
    double long1 = latLng1.longitude * PI / 180;
    double lat2 = latLng2.latitude * PI / 180;
    double long2 = latLng2.longitude * PI / 180;

    double dLon = (long2 - long1);

    double y = Math.sin(dLon) * Math.cos(lat2);
    double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
        * Math.cos(lat2) * Math.cos(dLon);

    double brng = Math.atan2(y, x);

    brng = Math.toDegrees(brng);
    brng = (brng + 360) % 360;

    return brng;
  }




//  @Override
//  public void onStatusChanged(String provider, int status, Bundle extras) {
//
//  }
//
//  @Override
//  public void onProviderEnabled(String provider) {
//
//  }
//
//  @Override
//  public void onProviderDisabled(String provider) {
//
//  }

  private void requestLocation() {
    Criteria criteria = new Criteria();
    criteria.setAccuracy(Criteria.ACCURACY_FINE);
    criteria.setPowerRequirement(Criteria.POWER_HIGH);
    String provider = locationManager.getBestProvider(criteria, true);
    if (checkSelfPermission(permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        && checkSelfPermission(permission.ACCESS_COARSE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
      // TODO: Consider calling
      //    Activity#requestPermissions
      // here to request the missing permissions, and then overriding
      //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
      //                                          int[] grantResults)
      // to handle the case where the user grants the permission. See the documentation
      // for Activity#requestPermissions for more details.
      return;
    }
//    locationManager.requestLocationUpdates(provider, 0, 0, this);
  }

  private boolean isLocationEnabled() {
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
  }

  private boolean isPermissionGrandted() {
    if(checkSelfPermission(permission.ACCESS_COARSE_LOCATION)
    == PackageManager.PERMISSION_GRANTED || checkSelfPermission(permission.ACCESS_FINE_LOCATION)
    == PackageManager.PERMISSION_GRANTED) {
      Log.v("mylog", "Permission is granted");
      return true;
    } else {
      Log.v("mylog", "Permission is denied");
      return false;
    }

  }

  private void showAlert(final int status) {
    String message, title, btnText;
    if(status == 1) {
      message = "Your location settings is set to 'Off'.\nPlease enable location to use this app";
      title = "Enable Location";
      btnText = "Location Settings";
    } else {
      message = "Please allow this app to access location!";
      title = "Permission access";
      btnText = "Grant";
    }
    final AlertDialog.Builder dialog = new Builder(this);
    dialog.setCancelable(false);
    dialog.setTitle(title)
        .setMessage(message)
        .setPositiveButton(btnText, new OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            if (status == 1) {
              Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
              startActivity(myIntent);
            } else {
              requestPermissions(PERMISSIONS, PERMISSION_ALL);
            }
          }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            finish();
          }
    });
    dialog.show();
  }

  @Override
  public void onConnected(@Nullable Bundle bundle) {

    Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
    startLocationUpdates();
  }


  @Override
  public void onPointerCaptureChanged(boolean hasCapture) {

  }

  @Override
  protected void onStart() {
    super.onStart();
    Log.d(TAG, "onStart fired ..............");
    mGoogleApiClient.connect();
  }

  @Override
  protected void onStop() {
    super.onStop();

    mGoogleApiClient.disconnect();
    Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
  }

  @Override
  public void onConnectionSuspended(int i) {

  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

  }

  protected void startLocationUpdates() {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      // TODO: Consider calling
      //    ActivityCompat#requestPermissions
      // here to request the missing permissions, and then overriding
      //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
      //                                          int[] grantResults)
      // to handle the case where the user grants the permission. See the documentation
      // for ActivityCompat#requestPermissions for more details.
      return;
    }
    PendingResult<Status> pendingResult = LocationServices.FusedLocationApi
        .requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    Log.d(TAG, "Location update started ..............: ");
  }

  LatLng previouslatLng;

  @Override
  public void onLocationChanged(Location location) {
    previouslatLng = new LatLng(location.getLatitude(), location.getLongitude());

    double rota = 0.0;
    double startrota = 0.0;
    if (previousLocation != null) {

      rota = bearingBetweenLocations(previouslatLng, new LatLng(location.getLatitude
          (), location.getLongitude()));
    }


    rotateMarker(marker, (float) rota, (float) startrota);


    previousLocation = location;
    Log.d(TAG, "Firing onLocationChanged..........................");
    Log.d(TAG, "lat :" + location.getLatitude() + "long :" + location.getLongitude());
    Log.d(TAG, "bearing :" + location.getBearing());

    animateMarker(new LatLng(location.getLatitude(), location.getLongitude()), false);
//        new ServerConnAsync(handler, MapsActivity.this,location).execute();


  }

  @Override
  protected void onPause() {
    super.onPause();
    if(mGoogleApiClient.isConnected()) {

      stopLocationUpdates();
    }

  }

  protected void stopLocationUpdates() {
    LocationServices.FusedLocationApi.removeLocationUpdates(
        mGoogleApiClient, this);
    Log.d(TAG, "Location update stopped .......................");
  }

  @Override
  public void onResume() {
    super.onResume();
    if (mGoogleApiClient.isConnected()) {
      startLocationUpdates();
      Log.d(TAG, "Location update resumed .....................");
    }
  }
}
