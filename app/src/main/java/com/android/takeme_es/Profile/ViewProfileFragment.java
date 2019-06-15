package com.android.takeme_es.Profile;

import android.content.Context;
import android.content.Intent;
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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.takeme_es.Common.BottomNavigationViewHelper;
import com.android.takeme_es.Common.UniversalImageLoader;
import com.android.takeme_es.Data.Photo;
import com.android.takeme_es.Data.Trip;
import com.android.takeme_es.Data.User;
import com.android.takeme_es.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by User on 6/29/2017.
 */

public class ViewProfileFragment extends Fragment {

    private static final String TAG = "ViewProfileFragment";


    public interface OnGridImageSelectedListener{
        void onGridImageSelected(Photo photo, int activityNumber);
    }
    OnGridImageSelectedListener mOnGridImageSelectedListener;

    public void ViewProfileFragment(){
        setArguments(new Bundle());
    }

    private static final int ACTIVITY_NUM = 3;
    private static final int NUM_GRID_COLUMNS = 3;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String mUserid;
    private FirebaseFirestore mDB;



    //widgets
    private TextView mPosts, mFollowers, mFollowing, mDisplayName, mUsername, mWebsite, mDescription,
    mFollow, mUnfollow ;
    private ProgressBar mProgressBar;
    private CircleImageView mProfilePhoto;
    private GridView gridView;
    private ImageView mBackArrow;
    private BottomNavigationViewEx bottomNavigationView;
    private Context mContext;
    private TextView editProfile;

    private ArrayList<Photo> photos = new ArrayList<>();

    private RecyclerView mVertical_recyclerView;
    private VerticalRecyclerViewAdapter mVerticalRecyclerViewAdapter;

    //vars
    private int mFollowersCount = 0;
    private int mFollowingCount = 0;
    private int mPostsCount = 0;

    //vars
    private User mUser;

    private ArrayList<Trip> mTrips = new ArrayList<>();
    private String mUserID;

    private RecyclerView mRecyclerViewTtip;
    private ProfileRecyclerViewTripAdapter.OnTripDetailsClickListner mOnTripDetailsClickListner;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_profile, container, false);
        mDisplayName = (TextView) view.findViewById(R.id.display_name);
        mUsername = (TextView) view.findViewById(R.id.username);
        mWebsite = (TextView) view.findViewById(R.id.website);
        mDescription = (TextView) view.findViewById(R.id.description);
        mProfilePhoto = (CircleImageView) view.findViewById(R.id.profile_photo);
        mPosts = (TextView) view.findViewById(R.id.tvPosts);
        mFollowers = (TextView) view.findViewById(R.id.tvFollowers);
        mFollowing = (TextView) view.findViewById(R.id.tvFollowing);
        mProgressBar = (ProgressBar) view.findViewById(R.id.profileProgressBar);
        gridView = (GridView) view.findViewById(R.id.gridView);
        bottomNavigationView = (BottomNavigationViewEx) view.findViewById(R.id.bottomNavViewBar);
        mFollow = (TextView) view.findViewById(R.id.follow);
        mUnfollow = (TextView) view.findViewById(R.id.unfollow);
        editProfile  = (TextView) view.findViewById(R.id.textEditProfile);
        mBackArrow = (ImageView) view.findViewById(R.id.backArrow);
        mContext = getActivity();

        mRecyclerViewTtip = (RecyclerView) view.findViewById(R.id.recycler_view_trip_profile);

        Log.d(TAG, "onCreateView: stared.");

        setupBottomNavigationView();
        setupFirebaseAuth();
        isFollowing();

        mProgressBar.setVisibility(View.GONE);
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

        Bundle bundle = getArguments();

        mTrips = bundle.getParcelableArrayList("trips");
        mUserID = bundle.getString("user_id");

        Log.e(TAG, "onEvent: pass trip list to recycler view " + mTrips.size());

        display_profile_image();

        ProfileRecyclerViewTripAdapter mRecyclerViewTripAdapter = new ProfileRecyclerViewTripAdapter(mContext, mTrips, mOnTripDetailsClickListner);

        mRecyclerViewTtip.setHasFixedSize(true);
        mRecyclerViewTtip.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRecyclerViewTtip.setAdapter(mRecyclerViewTripAdapter);
        mRecyclerViewTtip.setNestedScrollingEnabled(false);

        mFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: now following: ");

                // need to change to user id...later,
                mDB.collection("users").document(mAuth.getCurrentUser().getUid())
                        .update("following", FieldValue.arrayUnion(mTrips.get(0).getUser_id()))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: succeeded following added");
                                setFollowing();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: failed following added");
                            }
                        });

                mDB.collection("users").document(mTrips.get(0).getUser_id())
                        .update("follower", FieldValue.arrayUnion(mAuth.getCurrentUser().getUid()))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: succeeded follower added");
                                setFollowing();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: failed follower added");
                            }
                        });

            }
        });



        mUnfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: now following: ");

                mDB.collection("users").document(mAuth.getCurrentUser().getUid())
                        .update("following", FieldValue.arrayRemove(mTrips.get(0).getUser_id()))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: unfollowing successs");
                                setUnfollowing();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure:  unfollowing...failed");
                            }
                        });

                mDB.collection("users").document(mTrips.get(0).getUser_id())
                        .update("follower", FieldValue.arrayRemove(mAuth.getCurrentUser().getUid()))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: unfollower successs");
                                setUnfollowing();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure:  unfollower...failed");
                            }
                        });
            }
        });

        return view;
    }


    private void display_profile_image(){

        if(mTrips == null) {
            Log.e(TAG, "mtrips NULLL");

            mDB.collection("users").document(mUserID)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot doc = task.getResult();

                                mUser = doc.toObject(User.class);

                                UniversalImageLoader.setImage(mUser.getProfile_image(), mProfilePhoto, null, "");
                                //Log.e(TAG, "onComplete: getNumOfTrips : " + mUser.getNumOfTrips());
                                mPosts.setText(Integer.toString(mUser.getNumOfTrips()));
                            }
                        }
                    });
        }
        else {
            Log.e(TAG, "onEvent: pass trip list to recycler view " + mTrips.size());
            if(mTrips.size() > 0 && mTrips.get(0).getProfile_image() != null){

                UniversalImageLoader.setImage(mTrips.get(0).getProfile_image(), mProfilePhoto, null, "");
                mPosts.setText(Integer.toString(mTrips.size()));
            }
            else{
                mDB.collection("users").document(mUserID)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    DocumentSnapshot doc = task.getResult();

                                    mUser = doc.toObject(User.class);

                                    UniversalImageLoader.setImage(mUser.getProfile_image(), mProfilePhoto, null, "");
                                    mPosts.setText(Integer.toString(mUser.getNumOfTrips()));
                                }
                            }
                        });
            }
        }
    }

    private void isFollowing(){
        Log.d(TAG, "isFollowing: checking if following this users.");

        mDB.collection("users").document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            User temp_user = task.getResult().toObject(User.class);

                            if(temp_user.getFollowing() != null) {
                                Log.e(TAG, "onComplete: size??  " + temp_user.getFollowing().size());
                                for (String s : temp_user.getFollowing()) {
                                    if (s != null && s.equals(mUserID)) {
                                        setFollowing();
                                        break;
                                    }
                                }
                            }
                            else
                                setUnfollowing();
                        }
                        else
                            setUnfollowing();
                    }
                });
    }


    private void setFollowing(){
        Log.d(TAG, "setFollowing: updating UI for following this user");
        mFollow.setVisibility(View.GONE);
        mUnfollow.setVisibility(View.VISIBLE);
        editProfile.setVisibility(View.GONE);
    }

    private void setUnfollowing(){
        Log.d(TAG, "setFollowing: updating UI for unfollowing this user");
        mFollow.setVisibility(View.VISIBLE);
        mUnfollow.setVisibility(View.GONE);
        editProfile.setVisibility(View.GONE);
    }

    private void setCurrentUsersProfile(){
        Log.d(TAG, "setFollowing: updating UI for showing this user their own profile");
        mFollow.setVisibility(View.GONE);
        mUnfollow.setVisibility(View.GONE);
        editProfile.setVisibility(View.VISIBLE);
    }


    private User getUserFromBundle(){
        Log.d(TAG, "getUserFromBundle: arguments: " + getArguments());

        Bundle bundle = this.getArguments();
        if(bundle != null){
            return bundle.getParcelable(getString(R.string.intent_user));
        }else{
            return null;
        }
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


    private void setProfileWidgets(User user){
        //Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.toString());
        //Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.getSettings().getUsername());


        //User user = userSettings.getUser();
        //User mUser = userSettings.getSettings();
   /*     if(user.getProfile_url() != null)
            UniversalImageLoader.setImage(user.getProfile_url(), mProfilePhoto, null, "");

        Log.d(TAG, "setProfileWidgets: follower " + user.getFollower_count() + " following  " + user.getFollowing_count());

        mPosts.setText(String.valueOf(user.getPost()));
        mFollowers.setText(String.valueOf(user.getFollower_count()));
        mFollowing.setText(String.valueOf(user.getFollowing_count()));
*/
        // mDisplayName.setText(mUser.getDisplay_name());
        // mUsername.setText(mUser.getUsername());
        // mWebsite.setText(mUser.getWebsite());
        // mDescription.setText(mUser.getDescription());
        mProgressBar.setVisibility(View.GONE);

        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back");
                getActivity().getSupportFragmentManager().popBackStack();
                getActivity().finish();
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
