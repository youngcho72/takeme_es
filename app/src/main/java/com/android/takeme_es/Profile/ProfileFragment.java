package com.android.takeme_es.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.takeme_es.Common.BottomNavigationViewHelper;
import com.android.takeme_es.Common.FirebaseMethods;
import com.android.takeme_es.Common.UniversalImageLoader;
import com.android.takeme_es.Data.Trip;
import com.android.takeme_es.Data.User;
import com.android.takeme_es.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    public ProfileFragment(){
        setArguments(new Bundle());
    }

    private static final int ACTIVITY_NUM = 3;
    private static final int NUM_GRID_COLUMNS = 3;

    public static final int RETRIEVE_IMG = 9004;


    //firebase
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private FirebaseMethods mFirebaseMethods;
    private FirebaseUser mFirebaseUser;
    private String mUserid;
    private FirebaseFirestore mDB;
    private String profileImage;


    private User dbUser;

    //widgets
    private TextView mPosts, mFollowers, mFollowing, mDisplayName, mUsername, mWebsite, mDescription;
    private ProgressBar mProgressBar;
    private CircleImageView mProfilePhoto;
    private GridView gridView;
    private Toolbar toolbar;
    private ImageView profileMenu;
    private BottomNavigationViewEx bottomNavigationView;
    private Context mContext;
    private ArrayList<Trip> mTrips = new ArrayList<>();


    private RecyclerView mRecyclerViewTtip;
    private ProfileRecyclerViewTripAdapter.OnTripDetailsClickListner mOnTripDetailsClickListner;

    // private VerticalRecyclerViewAdapter mVerticalRecyclerViewAdapter;

    //vars
    private int mFollowersCount = 0;
    private int mFollowingCount = 0;
    private int mPostsCount = 0;


    public interface OnMenuPressedListner{
        void OnMenuPressed();
    }

    public interface OnProfilePhotoPressedListner{
        void OnProfilePhotoPressed();
    }

    OnMenuPressedListner mOnMenuPressedListner;
    OnProfilePhotoPressedListner mOnProfilePhotoPressedListner;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mDisplayName = (TextView) view.findViewById(R.id.display_name);
        mUsername = (TextView) view.findViewById(R.id.username);
        mWebsite = (TextView) view.findViewById(R.id.website);
        mDescription = (TextView) view.findViewById(R.id.description);
        mProfilePhoto = (CircleImageView) view.findViewById(R.id.profile_photo);
        mPosts = (TextView) view.findViewById(R.id.tvPosts);
        mFollowers = (TextView) view.findViewById(R.id.tvFollowers);
        mFollowing = (TextView) view.findViewById(R.id.tvFollowing);
        mProgressBar = (ProgressBar) view.findViewById(R.id.profileProgressBar);
        toolbar = (Toolbar) view.findViewById(R.id.profileToolBar);
        profileMenu = (ImageView) view.findViewById(R.id.profileMenu);
        bottomNavigationView = (BottomNavigationViewEx) view.findViewById(R.id.bottomNavViewBar);
        mContext = getActivity();

        mOnMenuPressedListner = (OnMenuPressedListner) mContext;
        mOnProfilePhotoPressedListner = (OnProfilePhotoPressedListner) mContext;

        mFirebaseMethods = new FirebaseMethods(getActivity());

        mRecyclerViewTtip = (RecyclerView) view.findViewById(R.id.recycler_view_trip_profile);

        Log.d(TAG, "onCreateView: stared.");


        setupBottomNavigationView();
        //setupToolbar();

        setupFirebaseAuth();
        // setupTripView();

        TextView editProfile = (TextView) view.findViewById(R.id.textEditProfile);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to " + mContext.getString(R.string.edit_profile_fragment));
                Intent intent = new Intent(getActivity(), AccountSettingsActivity.class);
                intent.putExtra(getString(R.string.calling_activity), getString(R.string.profile_activity));
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        mProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "onClick: profile update click");
                mOnProfilePhotoPressedListner.OnProfilePhotoPressed();

            }
        });

        Bundle bundle = getArguments();

        mTrips = bundle.getParcelableArrayList("trips");

        //Log.e(TAG, "onCreateView: profile image path  " + profileImage);

        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnMenuPressedListner.OnMenuPressed();
            }
        });

        ProfileRecyclerViewTripAdapter mRecyclerViewTripAdapter = new ProfileRecyclerViewTripAdapter(mContext, mTrips, mOnTripDetailsClickListner);

        mRecyclerViewTtip.setHasFixedSize(true);
        mRecyclerViewTtip.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRecyclerViewTtip.setAdapter(mRecyclerViewTripAdapter);
        mRecyclerViewTtip.setNestedScrollingEnabled(false);

        //getFollowersCount();
        //getFollowingCount();
        //getPostsCount();

        //setupBottomNavigationView();


        return view;
    }


    @Override
    public void onAttach(Context context) {
        try{
            mOnTripDetailsClickListner = (ProfileRecyclerViewTripAdapter.OnTripDetailsClickListner) getActivity();
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage() );
        }
        super.onAttach(context);
    }


    private void getPostsCount(){
        mPostsCount = 0;

        // DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        mDB.collection("users").document(mUserid).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot doc = task.getResult();
                            String num_following = "", num_follower = "";

                            Log.e(TAG, "onComplete: numOfTrips" + doc.get("numOfTrips"));

                            User mUser = doc.toObject(User.class);

                            if(doc.get("numOfTrips") != null)
                                mPosts.setText(doc.get("numOfTrips").toString());
                            else
                                mPosts.setText("0");

                            if(doc.get("following") != null)
                                mFollowing.setText(num_following + mUser.getFollowing().size());
                            else
                                mFollowing.setText("0");

                            if(doc.get("follower") != null)
                                mFollowers.setText(num_follower + mUser.getFollower().size());
                            else
                                mFollowers.setText("0");

                            mUsername.setText(doc.get("user_name").toString());
                        }

                    }
                });
    }

/*
    private void setProfileWidgets(User mUser) {
        //Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.toString());
        //Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.getSettings().getUsername());

        if(mUser == null)
            return;

        //User user = userSettings.getUser();
        //User mUser = userSettings.getSettings();
  //      if(mUser.getProfile_url() != null)
   //         UniversalImageLoader.setImage(mUser.getProfile_url(), mProfilePhoto, null, "");

   //     Log.d(TAG, "setProfileWidgets: follower " + mUser.getFollower_count() + " following  " + mUser.getFollowing_count());

  //      mPosts.setText(String.valueOf(mUser.getPost()));
 //       mFollowers.setText(String.valueOf(mUser.getFollower_count()));
 //       mFollowing.setText(String.valueOf(mUser.getFollowing_count()));

        // mDisplayName.setText(mUser.getDisplay_name());
        // mUsername.setText(mUser.getUsername());
        // mWebsite.setText(mUser.getWebsite());
        // mDescription.setText(mUser.getDescription());
        mProgressBar.setVisibility(View.GONE);
    }


    /**
     * Responsible for setting up the profile toolbar
     */
    private void setupToolbar(){

        ((ProfileActivity)getActivity()).setSupportActionBar(toolbar);

        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to account settings.");
                Intent intent = new Intent(mContext, AccountSettingsActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }

    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView);
        BottomNavigationViewHelper.enableNavigation(mContext,getActivity() ,bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

      /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();
        mDB = FirebaseFirestore.getInstance();
        mUserid = mAuth.getCurrentUser().getUid();

        DocumentReference mDocRef = mDB.collection("users").document(mUserid);

        Log.d(TAG, "setupFirebaseAuth: user id  " + mUserid);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                    getPostsCount();

                    mDB.collection("users").document(mAuth.getCurrentUser().getUid())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        DocumentSnapshot doc = task.getResult();

                                        try {
                                            if (doc.get("profile_image") != null) {
                                                profileImage = doc.get("profile_image").toString();
                                                UniversalImageLoader.setImage(profileImage, mProfilePhoto, null, "");
                                            }
                                            else{
                                                Log.e(TAG, "onComplete: here??");
                                               // UniversalImageLoader.setImage(null, mProfilePhoto, null, "");
                                            }

                                        } catch (NullPointerException e){
                                            Log.e(TAG, "onComplete: " + e );
                                        }
                                    }
                                }
                            });
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
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

