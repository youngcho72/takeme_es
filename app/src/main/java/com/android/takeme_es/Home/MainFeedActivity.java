package com.android.takeme_es.Home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.takeme_es.Common.BottomNavigationViewHelper;
import com.android.takeme_es.Common.UniversalImageLoader;
import com.android.takeme_es.Data.Followings;
import com.android.takeme_es.Data.Photo;
import com.android.takeme_es.Data.Trip;
import com.android.takeme_es.Login.LoginActivity;
import com.android.takeme_es.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainFeedActivity extends AppCompatActivity implements RecyclerViewTripAdapter.OnTripDetailsClickListner {

    private RecyclerView recyclerView;

    private PostVerticalListAdapter mAdapter;
    private Context mContext = MainFeedActivity.this;

    private ArrayList<Photo> mPhotos;
    private ArrayList<Photo> mPaginatedPhotos;
    private ArrayList<String> mFollowing;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    // private FirebaseMethods mFirebaseMethods;
    private FirebaseUser mFirebaseUser;
    private String mUserid;
    // private User mUser;
    // private User dbUser;
    private FirebaseFirestore mDB;

    private ArrayList<Trip> mTrips = new ArrayList<>();
    private RecyclerView mRecyclerViewTtip;

    // bottom navigation bar
    private static final int ACTIVITY_NUM = 0;
    private static final String TAG = "MainFeedActivity";
    private static int num_followings;
    private int trip_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail_mainfeed);

       // mRecyclerViewTtip = findViewById(R.id.recycler_view_item);

       // mFollowing = new ArrayList<>();
        //mPhotos = new ArrayList<>();

       // initImageLoader();
       // setupFirebaseAuth();
       // setupBottomNavigationView();


        MainFeedFragment fragment = new MainFeedFragment();

        FragmentTransaction transaction = MainFeedActivity.this.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack("MainFeedFragment");
        transaction.commit();
       // if(mAuth.getCurrentUser() != null)
        //   getFollowing();

        //recyclerView = (RecyclerView) findViewById(R.id.recycler_view_item);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void getFollowing(){
        Log.d(TAG, "getFollowing: searching for following  " + mAuth.getCurrentUser().getUid());

        mDB.collection("followings").document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            // ArrayList<String> list_tripid = task.getResult().get();
                            Followings mFollowings = task.getResult().toObject(Followings.class);

                            if(mFollowings == null) {
                                Log.e(TAG, "onComplete: getfollowing is null");
                                display_mainfeed();
                                return;
                            }

                            if(mFollowings.getTripid_list() == null || mFollowings.getTripid_list().size() == 0) {
                                Log.e(TAG, "onComplete: size is 0");
                                display_mainfeed();
                                return;
                            }

                            trip_count = mFollowings.getTripid_list().size();

                            Log.e(TAG, "onComplete: trip count" + trip_count);
                            //num_followings = mUser.getFollowing().size();
                            //Log.e(TAG, "onComplete: num followings " + num_followings);

                            for(String tripid : mFollowings.getTripid_list()) {

                                mDB.collection("trips")
                                        .document(tripid)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if(task.isSuccessful()){
                                                    Trip sTrip = task.getResult().toObject(Trip.class);
                                                    mTrips.add(sTrip);

                                                    if(--trip_count == 0){
                                                        Log.e(TAG, "onComplete: finally display");
                                                        display_mainfeed();
                                                    }
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
    }



    private void display_mainfeed(){
        Log.e(TAG, "display_mainfeed: lets print..");
        Log.e(TAG, "display_mainfeed: " + mTrips );
        RecyclerViewTripAdapter mRecyclerViewTripAdapter = new RecyclerViewTripAdapter(mContext, mTrips, MainFeedActivity.this);

        mRecyclerViewTtip.setHasFixedSize(true);
        mRecyclerViewTtip.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRecyclerViewTtip.setAdapter(mRecyclerViewTripAdapter);
        mRecyclerViewTtip.setNestedScrollingEnabled(false);
    }



    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth in MainFeedActivity.");

        mAuth = FirebaseAuth.getInstance();
        mDB = FirebaseFirestore.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                checkCurrentUser(mAuth.getCurrentUser());

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                    mUserid = mAuth.getCurrentUser().getUid();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Intent intent = new Intent(MainFeedActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                // ...
            }
        };

/*
        mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d(TAG, "onSuccess: succesffuly retrieved user profile data");
                //dbUser = documentSnapshot.toObject(User.class);

                //setProfileWidgets();
            }
        });
        */
    }

    private int getTimestampDifference(Photo photo){
        Log.d(TAG, "getTimestampDifference: getting timestamp difference.");

        int difference = 0;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));//google 'android list of timezones'
        Date today = c.getTime();
        sdf.format(today);
        Date timestamp;
        final String photoTimestamp = photo.getDate_created();
        try{
            timestamp = sdf.parse(photoTimestamp);
            difference = Math.round(((today.getTime() - timestamp.getTime()) / 1000 / 60 / 60 / 24 ));
        }catch (ParseException e){
            Log.e(TAG, "getTimestampDifference: ParseException: " + e.getMessage() );
            difference = 0;
        }
        return difference;
    }

    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
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

    private void checkCurrentUser(FirebaseUser user){
        Log.d(TAG, "checkCurrentUser: checking if user is logged in.");

        if(user == null){
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
        }
    }
  //  @Override
/*    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        checkCurrentUser(mAuth.getCurrentUser());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
*/
    @Override
    public void OnTripDetailsClick(Trip trip) {
        //Intent intent = new Intent(mContext, TripDetailActivity.class);


        Bundle bundle = new Bundle();

        bundle.putParcelable("trip",trip);

        Log.e(TAG, "OnTripDetailsClick: trip: " + trip);
        // startActivity(intent);

        MapTripViewFragment fragment = new MapTripViewFragment();
        fragment.setArguments(bundle);

        FragmentTransaction transaction = MainFeedActivity.this.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack("MapTripViewFragment");
        transaction.commit();
    }
}
