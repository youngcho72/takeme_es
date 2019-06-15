package com.android.takeme_es.Home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.takeme_es.Data.Trip;
import com.android.takeme_es.R;

public class TripDetailActivity extends AppCompatActivity {

    private static final String TAG = "TripDetailActivity";

    private Trip mTrip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_trip_detail_mainfeed);

        init();

       // mRecyclerViewTtipWithMap = (RecyclerView) findViewById(R.id.container);
    }

    void init(){
        Log.e(TAG, "init: starts");
        Intent intent = getIntent();
        if(intent.hasExtra("trip")) {
            try {
                mTrip = intent.getParcelableExtra("trip");
                Log.e(TAG, "init: inflating view profile:  " + mTrip);
            }catch(NullPointerException e){
                Log.e(TAG, "NullPointer Exception " + e);
            }
        }



        MapTripViewFragment fragment = new MapTripViewFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("trip", mTrip);

        fragment.setArguments(bundle);

        FragmentTransaction transaction = TripDetailActivity.this.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack("MapTripViewFragment");
        transaction.commit();
    }
}
