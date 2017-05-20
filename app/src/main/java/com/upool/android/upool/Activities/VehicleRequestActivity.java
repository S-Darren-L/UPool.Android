package com.upool.android.upool.Activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.upool.android.upool.R;
import com.upool.android.upool.Utils.PermissionUtils;

public class VehicleRequestActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "TAG";
    private static final int DEFAULT_ZOOM = 20;
    //Request code for location permission request.
    //@see #onRequestPermissionsResult(int, String[], int[])
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    //Flag indicating whether a requested permission has been denied after returning in
    //{@link #onRequestPermissionsResult(int, String[], int[])}.
    private boolean isPermissionDenied = false;
    private boolean isLocationPermissionGranted;

    private GoogleMap googleMap;
    private GoogleApiClient googleApiClient;
    private CameraPosition cameraPosition;
    private Location lastKnownLocation;

    // defaultLocation is in Sydney, Australia
    private LatLng defaultLocation = new LatLng(-33.852, 151.211);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_request);
//        ButterKnife.bind(this);

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        googleApiClient.connect();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        enableLocation();
        updateLocationUI();
        getDeviceLocation();

//        showCurrentPlace();
    }

    /**
     * Enables the Location layer if the fine location permission has been granted.
     */
    private void enableLocation() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            isLocationPermissionGranted = true;
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (googleMap != null) {
            // Access to the location has been granted to the app.
            googleMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            isPermissionDenied = true;
        }

        updateLocationUI();
        getDeviceLocation();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (isPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            isPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true);
    }

    /**
     * Builds the map when the Google Play services client is successfully connected.
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.vehicleRequestMap);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void getDeviceLocation() {
        if (isLocationPermissionGranted) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            lastKnownLocation = LocationServices.FusedLocationApi
                    .getLastLocation(googleApiClient);
        }

        // Set the map's camera position to the current location of the device.
        if (cameraPosition != null) {
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        } else if (lastKnownLocation != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(lastKnownLocation.getLatitude(),
                            lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
        } else {
            Log.d(TAG, "Current location is null. Using defaults.");
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    private void updateLocationUI() {
        if (googleMap == null) {
            return;
        }

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            isLocationPermissionGranted = true;
        } else {
            return;
        }

        if (isLocationPermissionGranted) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            googleMap.setMyLocationEnabled(false);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            lastKnownLocation = null;
        }
    }

//    private void showCurrentPlace() {
//        if (googleMap == null) {
//            return;
//        }
//
//        if (isLocationPermissionGranted) {
//            // Get the likely places - that is, the businesses and other points of interest that
//            // are the best match for the device's current location.
//            @SuppressWarnings("MissingPermission")
//            PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
//                    .getCurrentPlace(googleApiClient, null);
//            result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
//                @Override
//                public void onResult(@NonNull PlaceLikelihoodBuffer likelyPlaces) {
//                    int i = 0;
//                    mLikelyPlaceNames = new String[mMaxEntries];
//                    mLikelyPlaceAddresses = new String[mMaxEntries];
//                    mLikelyPlaceAttributions = new String[mMaxEntries];
//                    mLikelyPlaceLatLngs = new LatLng[mMaxEntries];
//                    for (PlaceLikelihood placeLikelihood : likelyPlaces) {
//                        // Build a list of likely places to show the user. Max 5.
//                        mLikelyPlaceNames[i] = (String) placeLikelihood.getPlace().getName();
//                        mLikelyPlaceAddresses[i] = (String) placeLikelihood.getPlace().getAddress();
//                        mLikelyPlaceAttributions[i] = (String) placeLikelihood.getPlace()
//                                .getAttributions();
//                        mLikelyPlaceLatLngs[i] = placeLikelihood.getPlace().getLatLng();
//
//                        i++;
//                        if (i > (mMaxEntries - 1)) {
//                            break;
//                        }
//                    }
//                    // Release the place likelihood buffer, to avoid memory leaks.
//                    likelyPlaces.release();
//
//                    // Show a dialog offering the user the list of likely places, and add a
//                    // marker at the selected place.
//                    openPlacesDialog();
//                }
//            });
//        } else {
//            // Add a default marker, because the user hasn't selected a place.
//            mMap.addMarker(new MarkerOptions()
//                    .title(getString(R.string.default_info_title))
//                    .position(mDefaultLocation)
//                    .snippet(getString(R.string.default_info_snippet)));
//        }
//    }
}
