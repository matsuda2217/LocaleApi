package com.example.tt.localeapi;

import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks
                                                    ,GoogleApiClient.OnConnectionFailedListener,LocationListener{
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private Location mLastlocation;

    private GoogleApiClient mGoogleApiClient;

    LocationRequest mLocationRequest;

    private boolean requestLocationUpdate = false;



    public static int UPDATE_INTERVAL = 10000;
    public static int FASTEST_INTERVAL = 5000;
    public static int DISPLACEMENT = 10;

    TextView lblLocation;
    Button btnShowLocation, btnStartLocationUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lblLocation = (TextView) findViewById(R.id.lblLocation);
        btnShowLocation = (Button) findViewById(R.id.btnshowlocation);
        btnStartLocationUpdate = (Button) findViewById(R.id.btnStartLocationUpdates);
        if (checkPlayServices()) {
            buildGoogleApiClient();
            createLocationRequest();

        }
        btnShowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disPlayLocation();
            }
        });
        btnStartLocationUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePeroidLocationUpdates();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
        if (mGoogleApiClient.isConnected() && requestLocationUpdate) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void disPlayLocation() {
        mLastlocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastlocation != null) {
            double latitude = mLastlocation.getLatitude();
            double longitude = mLastlocation.getLongitude();
            lblLocation.setText(latitude + " , " + longitude);
        } else {
            lblLocation.setText("Couldn't get the location, Make sur Location is enable on the device");
        }
    }

    private void togglePeroidLocationUpdates() {
        if (!requestLocationUpdate) {
            btnStartLocationUpdate.setText(getString(R.string.btn_stop_location_updates));
            requestLocationUpdate = true;
            startLocationUpdates();
        }else{
            btnStartLocationUpdate.setText(getString(R.string.btn_start_location_updates));
            requestLocationUpdate = false;
            stopLocationUpdates();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    public boolean checkPlayServices() {
        int rslcode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (rslcode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(rslcode)) {
                GooglePlayServicesUtil.getErrorDialog(rslcode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),"this device not support",Toast.LENGTH_SHORT).show();
                finish();
            }
            return  false;
        }
        return true;
    }

    public void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,  this);

    }

    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,  this);
    }
    @Override
    public void onConnected(Bundle bundle) {
        disPlayLocation();
        if (requestLocationUpdate) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "connection faild"+ connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastlocation = location;
        Toast.makeText(getApplicationContext(),"Location Changed",Toast.LENGTH_SHORT).show();
        disPlayLocation();
    }
}
