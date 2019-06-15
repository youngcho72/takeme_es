package com.android.takeme_es.Add;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.takeme_es.Common.TripManager;
import com.android.takeme_es.Data.Location;
import com.android.takeme_es.Data.Photo;
import com.android.takeme_es.R;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddPhotoActivity extends AppCompatActivity implements AddRecyclerViewPhotoAdapter.OnImageSelectedListner {

    private static final String TAG = "AddPhotoActivity";
    private RecyclerView mPhotoRecyclerView;

    private final int REQUEST_ACT = 1000;
    private final int AUTOCOMPLETE_REQUEST_CODE = 1001;

    private Context mContext = AddPhotoActivity.this;
    private ArrayList<Photo> mPhotoList = new ArrayList<>();
    private AddRecyclerViewPhotoAdapter mAddRecyclerViewPhotoAdapter;
    private TripManager mTripManager = TripManager.getInstance();
    private int passed_loc_idx;

    private RatingBar mRatingbar;
    private TextView mplacename,mSave,mCancel,minput_place_addr,placedetail;
    private EditText minput_caption_place;


    // Google Place API
    private PlacesClient placesClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_place_edit);

        mPhotoRecyclerView = (RecyclerView) findViewById(R.id.photo_recycler_view);
        mCancel = (TextView) findViewById(R.id.ivBackArrow);
        mSave = (TextView) findViewById(R.id.placesave);
        mRatingbar = (RatingBar) findViewById(R.id.ratingBar);
        mplacename = (TextView) findViewById(R.id.placename);
        minput_caption_place = (EditText) findViewById(R.id.input_caption_place);
        minput_place_addr = (TextView) findViewById(R.id.input_place_addr);
        placedetail = (TextView) findViewById(R.id.placedetail);

        Intent intent = getIntent();
        passed_loc_idx = intent.getIntExtra("place_id", 0);

        googlePlaceAPIInit();

        if(mTripManager.GetPlaces().size() == 0 || (passed_loc_idx >= mTripManager.GetPlaces().size())){
            Location sLocation = new Location();
            Photo sPhoto = new Photo();

            sPhoto.setImage_path("file://storage/emulated/0/DCIM/camera/Actions-insert-image-icon.png");

            sLocation.getmPhotos().add(sPhoto);
            mTripManager.AddPlace(sLocation);
        }

        if(mTripManager.GetPlaces().get(passed_loc_idx).getLocation_short_name() != null) {
            minput_place_addr.setText(mTripManager.GetPlaces().get(passed_loc_idx).getLocation_short_name());
            mplacename.setText(mTripManager.GetPlaces().get(passed_loc_idx).getLocation_short_name());
        }
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "onClick: pressed SAVE...  : " + mTripManager.GetPlaces().size() );
                Intent intent = new Intent(AddPhotoActivity.this, AddLocationActivity.class);
                intent.putExtra("complete_flag", 1);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // remove all photos and place info...
                mTripManager.GetPlaces().get(passed_loc_idx).getmPhotos().clear();
                mTripManager.GetPlaces().remove(passed_loc_idx);

                Intent intent = new Intent(AddPhotoActivity.this, AddLocationActivity.class);
                intent.putExtra("complete_flag", -1);
                setResult(RESULT_OK, intent);
                finish();
            }
        });


        if(mTripManager.GetPlaces().get(passed_loc_idx).getCaption() != null){
            minput_caption_place.setText(mTripManager.GetPlaces().get(passed_loc_idx).getCaption());
        }

        minput_caption_place.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = minput_caption_place.getText().toString();
                mTripManager.GetPlaces().get(passed_loc_idx).setCaption(text);
                Log.e(TAG, "afterTextChanged: caption " + text);
            }
        });

        minput_place_addr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Set the fields to specify which types of place data to
                // return after the user has made a selection.
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields)
                        //       .setTypeFilter(TypeFilter.CITIES)
                        //   .setLocationRestriction()
                        .build(mContext);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        placedetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Set the fields to specify which types of place data to
                // return after the user has made a selection.
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields)
                //       .setTypeFilter(TypeFilter.CITIES)
                     //   .setLocationRestriction()
                        .build(mContext);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        if(mTripManager.GetPlaces().get(passed_loc_idx).getRating() != 0)
            mRatingbar.setRating(mTripManager.GetPlaces().get(passed_loc_idx).getRating());

        mRatingbar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                mTripManager.GetPlaces().get(passed_loc_idx).setRating((int) v);
            }
        });

        mAddRecyclerViewPhotoAdapter = new AddRecyclerViewPhotoAdapter(mContext, mTripManager.GetPhotos(passed_loc_idx));
        mPhotoRecyclerView.setHasFixedSize(true);
        mPhotoRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mPhotoRecyclerView.setAdapter(mAddRecyclerViewPhotoAdapter);
        mPhotoRecyclerView.setNestedScrollingEnabled(false);
    }


    private void googlePlaceAPIInit(){
        Places.initialize(mContext.getApplicationContext(), "AIzaSyCDCI-UhpEHh924yuxDkjPIcDE5xf9B1H0");
        // Create a new Places client instance.
        placesClient = Places.createClient(mContext);
    }

    @Override
    public void OnImageSelected() {
        Intent intent = new Intent(mContext, GalleryActivity.class);
        startActivityForResult(intent, REQUEST_ACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ACT) {
            if(resultCode == RESULT_OK) {
                String resultMsg = data.getStringExtra("image_path");
                Log.e(TAG, "onActivityResult: imagepath " + resultMsg);
                Photo sPhoto = new Photo();
                sPhoto.setImage_path("file:/" + resultMsg);
                mTripManager.GetPhotos(passed_loc_idx).add(sPhoto);

                mAddRecyclerViewPhotoAdapter.notifyDataSetChanged();
            } else {
                Log.e(TAG, "onActivityResult: NOT OK");
                return;
            }
        } else if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                minput_place_addr.setText(place.getName());
                TripManager.getInstance().GetPlaces().get(passed_loc_idx).setLocation_short_name(place.getName());
                Log.e(TAG, "onActivityResult: LATLONG:  " + place.getLatLng());
                Log.e(TAG, "onActivityResult: LATLONG:  " + place.getId());
                Log.e(TAG, "onActivityResult: LATLONG:  " + place.getViewport());

                GeoPoint mGeopoint = new GeoPoint( place.getLatLng().latitude, place.getLatLng().longitude);

                TripManager.getInstance().GetPlaces().get(passed_loc_idx).setCoordinates(mGeopoint);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        else {
            Log.e(TAG, "onActivityResult: NOT Request ACT");
        }


    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.e(TAG, "onPostResume: called in AddPhotoActivity");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: called in AddPhotoActivity");
    }
}

