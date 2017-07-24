package com.upool.android.upool.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

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
import com.google.firebase.auth.FirebaseAuth;
import com.upool.android.upool.Adapters.AddressAutoCompleteAdapter;
import com.upool.android.upool.Fragments.MenuFragment;
import com.upool.android.upool.Models.AutoCompletePlace;
import com.upool.android.upool.R;
import com.upool.android.upool.Services.FetchAddressIntentService;
import com.upool.android.upool.Utils.PermissionUtils;
import com.upool.android.upool.Utils.ServiceConstants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;

public class VehicleRequestActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "VehicleRequestActivity";
    private static final int DEFAULT_ZOOM = 16;
    private static final String AUTO_COMPLETE_DEPARTURE_ADDRESS_STATE_TAG = "auto_complete_departure_address_state";
    private static final String AUTO_COMPLETE_DESTINATION_ADDRESS_STATE_TAG = "auto_complete_destination_address_state";

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    //Request code for location permission request.
    //@see #onRequestPermissionsResult(int, String[], int[])
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
//    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    //Flag indicating whether a requested permission has been denied after returning in
    //{@link #onRequestPermissionsResult(int, String[], int[])}.
    private boolean isPermissionDenied = false;
    private boolean isLocationPermissionGranted;

    private GoogleMap googleMap;
    private GoogleApiClient googleApiClient;
    private CameraPosition cameraPosition;
    private Location lastKnownLocation;

    private AddressResultReceiver addressResultReceiver;
    // Set departure auto complete variables
    private ArrayList<AutoCompletePlace> autoCompleteDeparturePlacesList;
    private AddressAutoCompleteAdapter departureAddressAutoCompleteAdapter;
    private LinearLayoutManager acDeparturePlacesLinearLayoutManager;
    private Parcelable acDeparturePlacesRecyclerViewState;
    // Set destination auto complete variables
    private ArrayList<AutoCompletePlace> autoCompleteDestinationPlacesList;
    private AddressAutoCompleteAdapter destinationAddressAutoCompleteAdapter;
    private LinearLayoutManager acDestinationPlacesLinearLayoutManager;
    private Parcelable acDestinationPlacesRecyclerViewState;

    // defaultLocation is in Sydney, Australia
    private LatLng defaultLocation = new LatLng(-33.852, 151.211);

    @BindView(R.id.vehicle_request_toolbar)
    Toolbar vehicleRequestToolbar;
    @BindView(R.id.departureLocationEditText)
    EditText departureLocationET;
    @BindView(R.id.destinationLocationEditText)
    EditText destinationLocationET;
    @BindView(R.id.autoCompleteDepartureAddressRecyclerView)
    RecyclerView autoCompleteDepartureAddressRV;
    @BindView(R.id.autoCompleteDestinationAddressRecyclerView)
    RecyclerView autoCompleteDestinationAddressRV;

    private MenuFragment menuFragment;

    private boolean isDepartureLocationFocused;
    private boolean isDestinationLocationFocused;
    private String departureLocation;
    private String destinationLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_request);
        ButterKnife.bind(this);

        // Set firebase
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null) {
                    Intent logInIntent = new Intent(VehicleRequestActivity.this, SignInActivity.class);
                    logInIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(logInIntent);
                }
            }
        };

        // Set tool bar
        setSupportActionBar(vehicleRequestToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Set Menu
        FragmentManager menuFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = menuFragmentManager.beginTransaction();
        menuFragment = new MenuFragment();
        fragmentTransaction.add(R.id.menu, menuFragment);
        fragmentTransaction.commit();


        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        googleApiClient.connect();

        // Init departure auto complete variables
        if(acDeparturePlacesLinearLayoutManager == null) {
            acDeparturePlacesLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        }
        if(acDeparturePlacesRecyclerViewState != null) {
            acDeparturePlacesLinearLayoutManager.onRestoreInstanceState(acDeparturePlacesRecyclerViewState);
        }
        autoCompleteDepartureAddressRV.setLayoutManager(acDeparturePlacesLinearLayoutManager);

        if(autoCompleteDeparturePlacesList == null) {
            autoCompleteDeparturePlacesList = new ArrayList<>();
        }
        if(departureAddressAutoCompleteAdapter == null) {
            departureAddressAutoCompleteAdapter = new AddressAutoCompleteAdapter(this);
        }
        autoCompleteDepartureAddressRV.setAdapter(departureAddressAutoCompleteAdapter);

        // Init destination auto complete variables
        if(acDestinationPlacesLinearLayoutManager == null) {
            acDestinationPlacesLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        }
        if(acDestinationPlacesRecyclerViewState != null) {
            acDestinationPlacesLinearLayoutManager.onRestoreInstanceState(acDestinationPlacesRecyclerViewState);
        }
        autoCompleteDestinationAddressRV.setLayoutManager(acDestinationPlacesLinearLayoutManager);

        if(autoCompleteDestinationPlacesList == null) {
            autoCompleteDestinationPlacesList = new ArrayList<>();
        }
        if(destinationAddressAutoCompleteAdapter == null) {
            destinationAddressAutoCompleteAdapter = new AddressAutoCompleteAdapter(this);
        }
        autoCompleteDestinationAddressRV.setAdapter(destinationAddressAutoCompleteAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    //Add navigation back button on ToolBar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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

        if(departureAddressAutoCompleteAdapter != null)
            departureAddressAutoCompleteAdapter.setGoogleApiClient(googleApiClient);
        if(destinationAddressAutoCompleteAdapter != null)
            destinationAddressAutoCompleteAdapter.setGoogleApiClient(googleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();

        firebaseAuth.addAuthStateListener(authStateListener);

        if(googleApiClient != null)
            googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if(googleApiClient != null && googleApiClient.isConnected()) {
            departureAddressAutoCompleteAdapter.setGoogleApiClient(null);
            destinationAddressAutoCompleteAdapter.setGoogleApiClient(null);
            googleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (googleMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, googleMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
            super.onSaveInstanceState(outState);
        }

        // Save auto complete departure address recycler view
        acDeparturePlacesRecyclerViewState = acDeparturePlacesLinearLayoutManager.onSaveInstanceState();
        outState.putParcelable(AUTO_COMPLETE_DEPARTURE_ADDRESS_STATE_TAG, acDeparturePlacesRecyclerViewState);

        // Save auto complete destination address recycler view
        acDestinationPlacesRecyclerViewState = acDestinationPlacesLinearLayoutManager.onSaveInstanceState();
        outState.putParcelable(AUTO_COMPLETE_DESTINATION_ADDRESS_STATE_TAG, acDestinationPlacesRecyclerViewState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        if(bundle != null) {
            acDeparturePlacesRecyclerViewState = bundle.getParcelable(AUTO_COMPLETE_DEPARTURE_ADDRESS_STATE_TAG);
            acDestinationPlacesRecyclerViewState = bundle.getParcelable(AUTO_COMPLETE_DESTINATION_ADDRESS_STATE_TAG);
        }
    }

    @OnFocusChange(R.id.departureLocationEditText)
    public void onDepartureFocusChanged() {
        Log.i(TAG, "onDepartureFocusChanged");
        isDepartureLocationFocused = true;
        isDestinationLocationFocused = false;
        autoCompleteDestinationAddressRV.setVisibility(View.GONE);
        autoCompleteDepartureAddressRV.setVisibility(View.VISIBLE);
    }

    @OnTextChanged(R.id.departureLocationEditText)
    public void onDepartureLocationChanged(CharSequence address) {
        departureLocation = departureLocationET.getText().toString();
        if(isDepartureLocationFocused && departureAddressAutoCompleteAdapter != null)
            departureAddressAutoCompleteAdapter.getFilter().filter(address);
        if(departureLocation.isEmpty() || departureLocation == null)
            autoCompleteDepartureAddressRV.setVisibility(View.GONE);
        else
            autoCompleteDepartureAddressRV.setVisibility(View.VISIBLE);
        Log.i(TAG, "onDepartureLocationChanged");
    }

    @OnFocusChange(R.id.destinationLocationEditText)
    public void onDestinationFocusChanged() {
        Log.i(TAG, "onDestinationFocusChanged");
        isDestinationLocationFocused = true;
        isDepartureLocationFocused = false;
        autoCompleteDepartureAddressRV.setVisibility(View.GONE);
        autoCompleteDestinationAddressRV.setVisibility(View.VISIBLE);
    }

    @OnTextChanged(R.id.destinationLocationEditText)
    public void onDestinationLocationChanged(CharSequence address) {
        destinationLocation = destinationLocationET.getText().toString();
        if(isDestinationLocationFocused && destinationAddressAutoCompleteAdapter != null)
            destinationAddressAutoCompleteAdapter.getFilter().filter(address);
        if(destinationLocation.isEmpty() || destinationLocation == null)
            autoCompleteDestinationAddressRV.setVisibility(View.GONE);
        else
            autoCompleteDestinationAddressRV.setVisibility(View.VISIBLE);
        Log.i(TAG, "onDestinationLocationChanged");
    }

    //Dismiss keyboard when touch outside of EditText
    @Override
    public boolean onTouchEvent(MotionEvent event){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    // Start fetch address(revers geolocation) service
    private void startFetchAddressIntentService() {
        if(addressResultReceiver == null){
            addressResultReceiver = new AddressResultReceiver(new Handler());
        }
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(ServiceConstants.RECEIVER, addressResultReceiver);
        intent.putExtra(ServiceConstants.LOCATION_DATA_EXTRA, lastKnownLocation);
        startService(intent);
    }

    private void updateDepartureLocationText(String location) {
        departureLocation = location;
        departureLocationET.setText(location);
    }

    private void updateDestincationLocationText(String location) {
        destinationLocation = location;
        destinationLocationET.setText(location);
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

        // try get address
        if(lastKnownLocation != null) {
            startFetchAddressIntentService();
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

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            String addressResult = resultData.getString(ServiceConstants.RESULT_DATA_KEY);
            Log.i(TAG, addressResult);
            if(resultCode == ServiceConstants.SUCCESS_RESULT && !addressResult.isEmpty() && addressResult != null){
                updateDepartureLocationText(addressResult);
            } else {
                // No address found
            }
        }
    }
}
