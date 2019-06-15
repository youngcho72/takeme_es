package com.android.takeme_es.Add;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.takeme_es.Common.BottomNavigationViewHelper;
import com.android.takeme_es.Common.TripManager;
import com.android.takeme_es.Common.UniversalImageLoader;
import com.android.takeme_es.R;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

public class AddActivity extends AppCompatActivity {

    private static final String TAG = "AddActivity";
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mFirebaseUser;
    private String mUserid;
    private FirebaseFirestore mDB;

    private Boolean mStoragePermissionGranted = false;

    // bottom navigation bar
    private static final int ACTIVITY_NUM = 2;
    private static final int FRAGMENT_NUM = 0;

    private Context mContext = AddActivity.this;

    private ImageView mCityNameIcon, mTripDateIcon;
    private TextView mCityName, mTripDate;
    private EditText mTripName;
    private CalendarView mCalendar;
    private Button mEditTrip;

    private PlacesClient placesClient;
    private TripManager mTripManager = TripManager.getInstance();

    private int AUTOCOMPLETE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        mTripName = (EditText) findViewById(R.id.input_trip);
        mEditTrip = (Button) findViewById(R.id.next);

        mTripDate = (TextView) findViewById(R.id.trip_date);
        mCityName = (TextView) findViewById(R.id.city_name);

        mCityNameIcon =(ImageView) findViewById(R.id.city_name_icon);
        mTripDateIcon =(ImageView) findViewById(R.id.trip_date_icon);

        mCalendar = (CalendarView) findViewById(R.id.calendarView);
        mCalendar.setVisibility(View.INVISIBLE);


        mTripName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String text = mTripName.getText().toString();
                mTripManager.SetTripName(text);
                Log.e(TAG, "afterTextChanged: trip name " + text );
            }
        });

        mEditTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AddLocationActivity.class);
                startActivity(intent);
            }
        });

        mCityNameIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            // Set the fields to specify which types of place data to
            // return after the user has made a selection.
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

            // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.OVERLAY, fields)
                        .setTypeFilter(TypeFilter.CITIES)
                        .build(mContext);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });


        mTripDateIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCalendar.setVisibility(View.VISIBLE);
                Log.e(TAG, "onClick: calendar showup");

            }
        });

        mCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int i, int i1, int i2) {
                String sDate = (i1 + 1)+ "/" + i2 + "/" + i;

                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

                try {
                    mTripManager.SetDate(sdf.parse(sDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                mTripDate.setText(sDate);
                mCalendar.setVisibility(View.INVISIBLE);
            }
        });


        setupFirebaseAuth();

        Places.initialize(mContext.getApplicationContext(), "AIzaSyCDCI-UhpEHh924yuxDkjPIcDE5xf9B1H0");
        // Create a new Places client instance.
        placesClient = Places.createClient(mContext);

        setupBottomNavigationView();
        //init();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                mCityName.setText(place.getName());
                TripManager.getInstance().SetCity(place.getName());
                Log.e(TAG, "onActivityResult: LatLng -  " + place.getLatLng() );
                TripManager.getInstance().SetCoordinates(place.getLatLng());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
            if(mStoragePermissionGranted){
                Log.e(TAG, "onResume: fds dsfadfs");
                //init_start();
            }
            else{
                getStoragePermission();
            }
    }

    private void getStoragePermission() {

        int permission_check = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if(permission_check == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "OK...", Toast.LENGTH_LONG).show();
            mStoragePermissionGranted = true;
           // init_start();
        }
        else{
            Toast.makeText(this, "NOK...", Toast.LENGTH_LONG).show();

            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        }
    }


    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 1:
                if(grantResults.length > 0){
                    if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Granted", Toast.LENGTH_LONG).show();
                        mStoragePermissionGranted = true;
                       // init_start();
                    }
                    else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(this, "Not Granted...", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(this, "NONONONO...", Toast.LENGTH_LONG).show();
                }
        }

    }


    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, this,bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();
        //mUserid = mAuth.getCurrentUser().getUid();
        //mDB = FirebaseFirestore.getInstance();
        //DocumentReference mDocRef = mDB.collection("Users").document(mUserid);


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    //Intent intent = new Intent(EditProfileActivity.this, LoginActivity.class);
                    // startActivity(intent);
                }
            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
