package com.android.takeme_es.Profile;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.takeme_es.Add.GalleryActivity;
import com.android.takeme_es.Common.BottomNavigationViewHelper;
import com.android.takeme_es.Common.ImageManager;
import com.android.takeme_es.Common.UniversalImageLoader;
import com.android.takeme_es.Common.ViewPostFragment;
import com.android.takeme_es.Data.Photo;
import com.android.takeme_es.Data.Trip;
import com.android.takeme_es.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity implements
        HorizontalRecyclerViewAdapter.OnImageSelectedListner,
        ViewPostFragment.OnCommentThreadSelectedListener,
        ProfileRecyclerViewTripAdapter.OnTripDetailsClickListner,
        ProfileFragment.OnMenuPressedListner,
        ProfileFragment.OnProfilePhotoPressedListner {

    private static final String TAG = "ProfileActivity";
    private static final int ACTIVITY_NUM = 3;
    private static final int NUM_GRID_COLUMNS = 3;

    private double mPhotoUploadProgress = 0;

    private Context mContext = ProfileActivity.this;

    private ProgressBar mProgressBar;
    private ImageView mImageView;


    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    // private FirebaseMethods mFirebaseMethods;
    private FirebaseUser mFirebaseUser;
    private String mUserid;
    // private User mUser;
    // private User dbUser;
    private FirebaseFirestore mDB;

    private ArrayList<Trip> mTrips;

    // gooogle map

    private boolean mLocationPermissionGranted = false;
    // gps
    public static final int ERROR_DIALOG_REQUEST = 9001;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9002;
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9003;
    public static final int RETRIEVE_IMG = 9004;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Log.d(TAG, "onCreate: started.");

        mTrips = new ArrayList<>();

        setupFirebaseAuth();

    }



    public void onCommentThreadSelectedListener(Photo photo) {
        Log.d(TAG, "onCommentThreadSelectedListener:  selected a comment thread");

    }

    private void init(){
        Log.d(TAG, "init: inflating " + getString(R.string.profile_fragment));

        Intent intent = getIntent();
        if(intent.hasExtra(getString(R.string.calling_activity))){
            Log.d(TAG, "init: searching for user object attached as intent extra");
            String userID = intent.getExtras().get(getString(R.string.intent_user)).toString();

            Log.d(TAG, "init: inflating view profile");

            ViewProfileFragment fragment = new ViewProfileFragment();
            Bundle args = new Bundle();
            args.putParcelableArrayList("trips", mTrips);
            args.putString("user_id", userID);
            fragment.setArguments(args);
            Log.d(TAG, "init: gogogogo");

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, fragment);
            transaction.addToBackStack(getString(R.string.view_profile_fragment));
            transaction.commit();
        }else{
            Log.d(TAG, "init: inflating Profile ooo shit");

            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("trips",mTrips);

            ProfileFragment fragment = new ProfileFragment();
            fragment.setArguments(bundle);

            FragmentTransaction transaction = ProfileActivity.this.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, fragment);
            transaction.addToBackStack(getString(R.string.profile_fragment));
            transaction.commit();
        }

    }

    @Override
    public void OnTripDetailsClick(Trip mTrip) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("trip", mTrip);

        ProfileTripSelectedFragment fragment = new ProfileTripSelectedFragment();
        fragment.setArguments(bundle);

        FragmentTransaction transaction = ProfileActivity.this.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack("ProfileTripSelectedFragment");
        transaction.commit();
    }

    private void setupTripView(){

        Log.e(TAG, "setupTripView: start.... ");
        Intent intent = getIntent();
        String mUID;

        if(intent.hasExtra(getString(R.string.calling_activity))) {
            mUID = intent.getExtras().get(getString(R.string.intent_user)).toString();
            Log.e(TAG, "setupTripView: from search ");
        }
        else{
            mUID = mAuth.getCurrentUser().getUid();
        }

        mDB.collection("trips")
                .whereEqualTo("user_id", mUID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            Log.e(TAG, "onEvent: trip loop starts");

                            for (QueryDocumentSnapshot sTrip : task.getResult()) {
                                mTrips.add(sTrip.toObject(Trip.class));
                            }

                            Log.e(TAG, "onEvent: trip loop ends. # of trips  " + mTrips.size());

                            init();
                        }
                        else
                            Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }


    private void setProfileImage(){
        Log.d(TAG, "setProfileImage: ");
        String imgURL = "i.stack.imgur.com/dWrvS.png";
        UniversalImageLoader.setImage(imgURL, mImageView, null, "https://");
    }

    private void setupActivityWidgets(){
        //mProgrssBar = (ProgressBar)findViewById(R.id.profileProgressBar);
        mProgressBar.setVisibility(View.GONE);
        mImageView = (ImageView)findViewById(R.id.profile_photo);
    }

    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.profileToolBar);
        setSupportActionBar(toolbar);

        ImageView profileMenu = (ImageView) findViewById(R.id.profileMenu);
        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to account settings.");
                Intent intent = new Intent(mContext, AccountSettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupBottomNavigationView()
    {
        Log.d(TAG, "setupBottomNavigationView: setting...");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx)findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, this,bottomNavigationViewEx);

        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);

    }


    @Override
    public void onImageSelected(Photo photo, int activityNumber) {
        ViewPostFragment fragment = new ViewPostFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photo), photo);
        args.putInt(getString(R.string.activity_number), activityNumber);

        fragment.setArguments(args);

        FragmentTransaction transaction  = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(getString(R.string.view_post_fragment));
        transaction.commit();
    }

    @Override
    public void OnMenuPressed() {

        Log.e(TAG, "OnMenuPressed: ");
        SignOutFragment fragment = new SignOutFragment();

        FragmentTransaction transaction  = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack("SignOutFragment");
        transaction.commit();
    }


    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth in MainFeedActivity.");

        mAuth = FirebaseAuth.getInstance();
        mDB = FirebaseFirestore.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                    mUserid = mAuth.getCurrentUser().getUid();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }


    private boolean checkMapServices(){
        if(isServicesOK()){
            if(isMapsEnabled()){
                return true;
            }
        }
        return false;
    }



    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });

        final AlertDialog alert = builder.create();
        alert.show();
    }



    public boolean isMapsEnabled(){

        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }

        Log.e(TAG, "isMapsEnabled: true ");

        return true;
    }



    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "getLocationPermission: true ");
            mLocationPermissionGranted = true;
            setupTripView();
        }
        else {
            Log.e(TAG, "getLocationPermission: no...need to submit request");
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(ProfileActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");

            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(ProfileActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {

        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   mLocationPermissionGranted = true;
                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called." + requestCode);

        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS:
                if(mLocationPermissionGranted){
                    setupTripView();
                }
                else{
                    getLocationPermission();
                }
                break;
            case RETRIEVE_IMG:
                if(resultCode == RESULT_OK) {
                    String resultMsg = data.getStringExtra("image_path");
                    Log.e(TAG, "onActivityResult: imagepath " + resultMsg);

                    uploadProfilePhoto("file:/"+ resultMsg, null);
                    // update Trip's profile images?????
                } else {
                    Log.e(TAG, "onActivityResult: NOT OK");
                    return;
                }
                break;
            }
    }

    public void uploadProfilePhoto(final String imgUrl, Bitmap bm){

        StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();

        Log.d(TAG, "uploadNewPhoto: uploading new PROFILE photo");

        // String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("profile_image/" + mAuth.getCurrentUser().getUid() + "/profile_photo");

        String mImgUrl = imgUrl.substring(7);

        //convert image url to bitmap
        if(bm == null){
            bm = ImageManager.getBitmap(mImgUrl);
        }
        byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100);

        UploadTask uploadTask = null;
        uploadTask = storageReference.putBytes(bytes);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();

                Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();

                firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String url = uri.toString();
                        Log.e("TAG:", "the url is: " + url);

                        Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();
                       // UniversalImageLoader.setImage(url, mProfilePhoto, null, "");

                        mDB.collection("users").document(mAuth.getCurrentUser().getUid())
                                .update("profile_image",url)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.e(TAG, "onComplete: done profile photo update");
                                    }
                                });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Photo upload failed.");
                Toast.makeText(mContext, "Photo upload failed ", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                if(progress - 15 > mPhotoUploadProgress){
                    Toast.makeText(mContext, "photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                    mPhotoUploadProgress = progress;
                }

                Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
            }
        });


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

    @Override
    protected void onResume() {
        super.onResume();
        if(checkMapServices()){
            Log.e(TAG, "onResume: called");
            if(mLocationPermissionGranted){
                Log.e(TAG, "onResume: calling setuptripview");
                setupTripView();
            }
            else{
                getLocationPermission();
            }
        }
    }

    @Override
    public void OnProfilePhotoPressed() {

        Log.d(TAG, "onClick: picccccc " + mContext.getString(R.string.edit_profile_fragment));
        Intent intent = new Intent(ProfileActivity.this, GalleryActivity.class);
        intent.putExtra(getString(R.string.calling_activity), getString(R.string.profile_activity));
        startActivityForResult(intent, RETRIEVE_IMG);
    }
}

