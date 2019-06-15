package com.android.takeme_es.Profile;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.takeme_es.Add.AddRecyclerViewLocationAdapter;
import com.android.takeme_es.Common.BottomNavigationViewHelper;
import com.android.takeme_es.Common.MyClusterManagerRenderer;
import com.android.takeme_es.Common.ViewWeightAnimationWrapper;
import com.android.takeme_es.Data.ClusterMarker;
import com.android.takeme_es.Data.Location;
import com.android.takeme_es.Data.Photo;
import com.android.takeme_es.Data.Trip;
import com.android.takeme_es.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.maps.android.clustering.ClusterManager;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

public class ProfileTripSelectedFragment extends Fragment implements OnMapReadyCallback {
    private static final String TAG = "PostUnderProgressFragme";

    private RecyclerView mRecyclerViewLocation;
    private AddRecyclerViewLocationAdapter mAddRecyclerViewLocationAdapter;
    private EditText mDate;
    private Button mAdd_Location,mSubmitTrip;
    private ImageView mBtnFullScreenMap,mBtnFullScreenTrip, mBtnReset;
    private BottomNavigationViewEx bottomNavigationView;


    private Context mContext;
    private Location mlocatiion_from_edit_framgemn;
    private Photo mPhoto;
    private int mLocationNum;
    private ArrayList<Location> mLocations;
    private Trip mTrip;
    private int photo_cnt;

    private SharedPreferences shared_pref_trip;
    private String mTrip_date;
    private int total_photo_count;

    // firestrorefirebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore mDB;
    private double mPhotoUploadProgress = 0;
    private int mMapLayoutState = 0;
    private int mTripLayoutState = 0;

    private static final int ACTIVITY_NUM = 2;
    private static final int MAP_LAYOUT_STATE_CONTRACTED = 0;
    private static final int MAP_LAYOUT_STATE_EXPANDED = 1;
    private static final int TRIP_LAYOUT_STATE_CONTRACTED = 0;
    private static final int TRIP_LAYOUT_STATE_EXPANDED = 1;


    // google map
    private MapView mMapView;
    private RelativeLayout mMapContainer, mTripContainer;
    public static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private GoogleMap mGoogleMap;
    private MyClusterManagerRenderer mClusterManagerRenderer;
    private ArrayList<ClusterMarker> mClusterMarkers = new ArrayList<>();
    private ClusterManager<ClusterMarker> mClusterManager;
    private LatLngBounds mMapBoundary;

    // private GeoApiContext mGeoApiContext;


    public ProfileTripSelectedFragment() {
        super();
        setArguments(new Bundle());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip_selected_profile, container, false);
        mRecyclerViewLocation = (RecyclerView) view.findViewById(R.id.recyclerviewlocation);
        bottomNavigationView = (BottomNavigationViewEx) view.findViewById(R.id.bottomNavViewBar);
        mMapView = view.findViewById(R.id.user_list_map);
        mBtnFullScreenMap = view.findViewById(R.id.btn_full_screen_map);
        mBtnFullScreenTrip = view.findViewById(R.id.btn_full_screen_trip);
        // mBtnShirkview.findViewById(R.id.btn_reset_map).setOnClickListener(this);
        mBtnReset = view.findViewById(R.id.btn_reset_map);
        mMapContainer = view.findViewById(R.id.map_container);
        mTripContainer = view.findViewById(R.id.trip_container);
        mContext = getActivity();

        // initUserListRecyclerView();
        initGoogleMap(savedInstanceState);

        //setUserPosition();

        setupFirebaseAuth();

        mBtnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMapMarkers();
            }
        });

        mBtnFullScreenMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mMapLayoutState == MAP_LAYOUT_STATE_CONTRACTED){
                    mMapLayoutState = MAP_LAYOUT_STATE_EXPANDED;
                    expandMapAnimation();
                }
                else if(mMapLayoutState == MAP_LAYOUT_STATE_EXPANDED){
                    mMapLayoutState = MAP_LAYOUT_STATE_CONTRACTED;
                    contractMapAnimation();
                }
            }
        });

        mBtnFullScreenTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mTripLayoutState == TRIP_LAYOUT_STATE_CONTRACTED){
                    mTripLayoutState = TRIP_LAYOUT_STATE_EXPANDED;
                    expandTripAnimation();
                }
                else if(mTripLayoutState == TRIP_LAYOUT_STATE_EXPANDED){
                    mTripLayoutState = TRIP_LAYOUT_STATE_CONTRACTED;
                    contractTripAnimation();
                }
            }
        });



        //setupBottomNavigationView();

        Bundle bundle =getArguments();

        mTrip = bundle.getParcelable("trip");

        ProfileRecyclerViewLocationAdapter mRecyclerViewLocationAdapter = new ProfileRecyclerViewLocationAdapter(mContext, mTrip.getmLocations());
        mRecyclerViewLocation.setHasFixedSize(true);
        //mRecyclerViewLocation.addOnScrollListener(new HorizontalRecyclerViewScrollListener(this);
        mRecyclerViewLocation.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRecyclerViewLocation.setAdapter(mRecyclerViewLocationAdapter);
        mRecyclerViewLocation.setNestedScrollingEnabled(false);

        return view;
    }

    //OnItemCoverListener method implementation
    //@Override
    public void onItemCover(final int position) {
     //   mMapView.showMarker(position); // notify Marker here
    }

    private void expandMapAnimation(){
        ViewWeightAnimationWrapper mapAnimationWrapper = new ViewWeightAnimationWrapper(mMapContainer);
        ObjectAnimator mapAnimation = ObjectAnimator.ofFloat(mapAnimationWrapper,
                "weight",
                50,
                100);
        mapAnimation.setDuration(800);

        ViewWeightAnimationWrapper recyclerAnimationWrapper = new ViewWeightAnimationWrapper(mTripContainer);
        ObjectAnimator recyclerAnimation = ObjectAnimator.ofFloat(recyclerAnimationWrapper,
                "weight",
                50,
                0);
        recyclerAnimation.setDuration(800);

        recyclerAnimation.start();
        mapAnimation.start();
    }

    private void contractMapAnimation(){
        ViewWeightAnimationWrapper mapAnimationWrapper = new ViewWeightAnimationWrapper(mMapContainer);
        ObjectAnimator mapAnimation = ObjectAnimator.ofFloat(mapAnimationWrapper,
                "weight",
                100,
                50);
        mapAnimation.setDuration(800);

        ViewWeightAnimationWrapper recyclerAnimationWrapper = new ViewWeightAnimationWrapper(mTripContainer);
        ObjectAnimator recyclerAnimation = ObjectAnimator.ofFloat(recyclerAnimationWrapper,
                "weight",
                0,
                50);
        recyclerAnimation.setDuration(800);

        recyclerAnimation.start();
        mapAnimation.start();
    }

    private void expandTripAnimation(){
        ViewWeightAnimationWrapper  recyclerAnimationWrapper= new ViewWeightAnimationWrapper(mTripContainer);
        ObjectAnimator recyclerAnimation = ObjectAnimator.ofFloat(recyclerAnimationWrapper,
                "weight",
                50,
                100);
        recyclerAnimation.setDuration(800);

        ViewWeightAnimationWrapper mapAnimationWrapper = new ViewWeightAnimationWrapper(mMapContainer);
        ObjectAnimator mapAnimation = ObjectAnimator.ofFloat(mapAnimationWrapper,
                "weight",
                50,
                0);

        mapAnimation.setDuration(800);

        recyclerAnimation.start();
        mapAnimation.start();
    }

    private void contractTripAnimation(){
        ViewWeightAnimationWrapper recyclerAnimationWrapper = new ViewWeightAnimationWrapper(mTripContainer);
        ObjectAnimator recyclerAnimation = ObjectAnimator.ofFloat(recyclerAnimationWrapper,
                "weight",
                100,
                50);
        recyclerAnimation.setDuration(800);

        ViewWeightAnimationWrapper mapAnimationWrapper = new ViewWeightAnimationWrapper(mMapContainer);
        ObjectAnimator mapAnimation = ObjectAnimator.ofFloat(mapAnimationWrapper,
                "weight",
                0,
                50);
        mapAnimation.setDuration(800);

        recyclerAnimation.start();
        mapAnimation.start();
    }

    private void initGoogleMap(Bundle savedInstanceState) {
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);

//        if(mGeoApiContext == null){
  //          mGeoApiContext = new GeoApiContext.Builder()
    //                .apiKey(getString(R.string.google_maps_api_key))
      //              .build();
      //  }
    }

    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();
        mDB = FirebaseFirestore.getInstance();

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
                }
                // ...
            }
        };
    }

    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView);
        BottomNavigationViewHelper.enableNavigation(mContext, getActivity() ,bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
       // startUserLocationsRunnable(); // update user locations every 'LOCATION_UPDATE_INTERVAL'
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
//        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        map.setMyLocationEnabled(true);
//        mGoogleMap = map;
//        setCameraView();

        map.addMarker(new MarkerOptions().position(new LatLng(0,0)).title("Marker"));
        mGoogleMap = map;
        addMapMarkers();
        //mGoogleMap.setOnPolylineClickListener(this);
    }


    private void resetMap(){
        if(mGoogleMap != null) {
            mGoogleMap.clear();

            if(mClusterManager != null){
                mClusterManager.clearItems();
            }

            if (mClusterMarkers.size() > 0) {
                mClusterMarkers.clear();
                mClusterMarkers = new ArrayList<>();
            }

      //      if(mPolyLinesData.size() > 0){
      //          mPolyLinesData.clear();
      //          mPolyLinesData = new ArrayList<>();
    //        }
        }
    }

    private void addMapMarkers(){

        if(mGoogleMap != null){

            resetMap();

            if(mClusterManager == null){
                mClusterManager = new ClusterManager<ClusterMarker>(getActivity().getApplicationContext(), mGoogleMap);
            }
            if(mClusterManagerRenderer == null){
                mClusterManagerRenderer = new MyClusterManagerRenderer(
                        getActivity(),
                        mGoogleMap,
                        mClusterManager
                );
                mClusterManager.setRenderer(mClusterManagerRenderer);
            }
            //mGoogleMap.setOnInfoWindowClickListener(this);
            mGoogleMap.setOnCameraIdleListener(mClusterManager);

            for(int i=0; i<mTrip.getmLocations().size(); i++){

                Location sLocation = mTrip.getmLocations().get(i);
                String snippet = sLocation.getLocation_short_name();


                ArrayList<ClusterMarker> mClusterMarkerList = new ArrayList<>();
                LatLng mLatLng = new LatLng(sLocation.getCoordinates().getLatitude(), sLocation.getCoordinates().getLongitude());
                Log.e(TAG, "addMapMarkers: location :  " + mLatLng );

                for(Photo sPhoto: sLocation.getmPhotos()) {
                    String imgPath = sPhoto.getImage_path();

                    ClusterMarker newClusterMarker = new ClusterMarker(
                            mLatLng,
                            sLocation.getLocation_short_name(),
                            snippet,
                            imgPath
                    );

                    mClusterMarkerList.add(newClusterMarker);
                }
                Log.e(TAG, "addMapMarkers:" + mClusterMarkerList);

                //mClusterManager.addItem(newClusterMarker);
                mClusterManager.addItems(mClusterMarkerList);

               // mClusterMarkers.add(newClusterMarker);
            }
            mClusterManager.cluster();

            setCameraView();
        }
    }

    private void setCameraView() {

        // Set a boundary to start
        double bottomBoundary = mTrip.getmLocations().get(0).getCoordinates().getLatitude() - .1;
        double leftBoundary = mTrip.getmLocations().get(0).getCoordinates().getLongitude() - .1;
        double topBoundary = mTrip.getmLocations().get(0).getCoordinates().getLatitude() + .1;
        double rightBoundary = mTrip.getmLocations().get(0).getCoordinates().getLongitude() + .1;

        mMapBoundary = new LatLngBounds(
                new LatLng(bottomBoundary, leftBoundary),
                new LatLng(topBoundary, rightBoundary)
        );

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary, 0));
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        //stopLocationUpdates(); // stop updating user locations
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

}
