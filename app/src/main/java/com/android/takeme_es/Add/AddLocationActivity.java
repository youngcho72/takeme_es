package com.android.takeme_es.Add;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.takeme_es.Common.ImageManager;
import com.android.takeme_es.Common.PlaceListManager;
import com.android.takeme_es.Common.TripManager;
import com.android.takeme_es.Data.LocationArea;
import com.android.takeme_es.Data.Photo;
import com.android.takeme_es.Data.User;
import com.android.takeme_es.Home.MainFeedActivity;
import com.android.takeme_es.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddLocationActivity extends AppCompatActivity implements PlaceListRecyclerAdapter.OnEditPlaceSelectedLinstner {

    private static final String TAG = "AddLocationActivity";
    private final int REQUEST_ACT = 1001;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mFirebaseUser;
    private FirebaseFirestore mDB;

    // Listview widgets
    private RecyclerView mPlaceListRecyclerView;
    private PlaceListRecyclerAdapter mPlaceListRecyclerAdapter;

    private Context mContext = AddLocationActivity.this;
    private TripManager mTripManager = TripManager.getInstance();
    private PlaceListManager mPlaceListManager = PlaceListManager.getInstance();

    private TextView mCityname,mAddplace;
    private Button mPost;

    // other global variables
    private int currentWorkingPlace;
    private int global_num_photos, current_num_photos, posting_num_photos, global_num_trips;
    private double mPhotoUploadProgress = 0;
    private String userName, profileImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_location);

        mCityname = findViewById(R.id.city_name);
        mPlaceListRecyclerView = findViewById(R.id.user_list_recycler_view);
        mAddplace = findViewById(R.id.addplace);
        mPost = findViewById(R.id.post);

        setupFirebaseAuth();

        mCityname.setText(mTripManager.GetTripName());
        mAddplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlaceListManager.addPlace("Place Name..");
                mPlaceListRecyclerAdapter.notifyDataSetChanged();
            }
        });

        mPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload_Trip();;
            }
        });
        Log.e(TAG, "onCreate: trip name " + mTripManager.GetTripName());

        mPlaceListRecyclerAdapter = new PlaceListRecyclerAdapter(mContext, mPlaceListManager.getPlaceList());
        mPlaceListRecyclerView.setAdapter(mPlaceListRecyclerAdapter);
        mPlaceListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void OnEditPlaceSelected(int position) {
        Log.e(TAG, "OnEditPlaceSelected: position : " + position);

        // store current working place id to use it when AddPhotoActivity returns with error or success.
        currentWorkingPlace = position;

        Intent intent = new Intent(mContext, AddPhotoActivity.class);
        intent.putExtra("place_id", position);
        startActivityForResult(intent, REQUEST_ACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            Log.e(TAG, "onActivityResult: NOT OK");
            if(mTripManager.GetPlaces().get(currentWorkingPlace).getLocation_short_name() != null){
                mPlaceListManager.setPlace(currentWorkingPlace, mTripManager.GetPlaces().get(currentWorkingPlace).getLocation_short_name());
                mPlaceListRecyclerAdapter.notifyDataSetChanged();
            }

            return;
        }
        if (requestCode == REQUEST_ACT) {
            int resultMsg = data.getIntExtra("complete_flag",-1);
            Log.e(TAG, "onActivityResult: complete flag :  " + resultMsg + " ooshi.. " +mPlaceListManager.getPlace(0) );

            if(resultMsg == 1) {
                mPlaceListManager.setPlace(currentWorkingPlace, mTripManager.GetPlaces().get(currentWorkingPlace).getLocation_short_name());
                Log.e(TAG, "onActivityResult: currentWorkingPlace : " + currentWorkingPlace
                        + " size of getPlaceList:  " + mPlaceListManager.getPlaceList().size()
                        + " place name : " + mPlaceListManager.getPlace(0));
               // if(!(mPlaceListManager.getPlace(mPlaceListManager.getPlaceList().size()-1).equals("Place Name.."))) {
              //      mPlaceListManager.addPlace("Place Name..");
                //    Log.e(TAG, "onActivityResult: Adding default place...Place Name...");
                //}
                mPlaceListRecyclerAdapter.notifyDataSetChanged();
            }
            else if(resultMsg == -1){
                Log.e(TAG, "onActivityResult: Nothing Changes");
                if(mPlaceListManager.getSize() > 1) {
                    mPlaceListManager.removePlace(currentWorkingPlace);
                    mPlaceListRecyclerAdapter.notifyDataSetChanged();
                }
                else{
                    mPlaceListManager.setPlace(0,"Place Name..");
                    mPlaceListRecyclerAdapter.notifyDataSetChanged();
                }

            }

        } else {
            Log.e(TAG, "onActivityResult: NOT Request ACT");
        }
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
                    getUserInfo(user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    //Intent intent = new Intent(EditProfileActivity.this, LoginActivity.class);
                    // startActivity(intent);
                }
            }
        };
    }

    private void getUserInfo(String userid){
        mDB.collection("users").document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            User user = task.getResult().toObject(User.class);
                            global_num_photos = user.getNumOfphotos();
                            global_num_trips = user.getNumOfTrips();
                            userName = user.getUser_name();
                            profileImage = user.getProfile_image();
                        }
                    }
                });
    }

    public void upload_Trip() {
        Log.d(TAG, "upload_Trip: start uploading trip");

        final String mTripID = mDB.collection("trips").document().getId();

        mTripManager.SetTripUserID(mAuth.getCurrentUser().getUid());
        mTripManager.SetTripID(mTripID);
        mTripManager.SetTripPrivacy(0);
        mTripManager.SetProfileImage(profileImage);
        mTripManager.SetUserName(userName);


        mDB.collection("users").document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            User mUser = task.getResult().toObject(User.class);
                            if(mUser.getFollower() == null) {
                                Log.e(TAG, "onComplete: getfollowing is null");
                                return;
                            }

                            int num_followers = mUser.getFollower().size();
                            Log.e(TAG, "onComplete: num followings " + num_followers);

                            for(String s : mUser.getFollower()){
                                mDB.collection("followings").document(s)
                                        .update("tripid_list", FieldValue.arrayUnion(mTripID))
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                    Log.d(TAG, "onComplete: done ....updated followings..");
                                            }
                                        });
                            }
                        }
                    }
                });

        for(int i=0; i<mTripManager.GetPlaces().size(); i++){
            String mLocationID = mDB.collection("locations").document().getId();

            mTripManager.GetPlaces().get(i).setUser_id(mAuth.getCurrentUser().getUid());
            mTripManager.GetPlaces().get(i).setTrip_id(mTripID);
            mTripManager.GetPlaces().get(i).setLocation_id(mLocationID);

            LocationArea mLocationArea = new LocationArea();
            mLocationArea.setLocation_id(mLocationID);
            mLocationArea.setTrip_id(mTripID);
            mLocationArea.setUser_id(mAuth.getCurrentUser().getUid());

            // update location collection first
            mDB.collection("locations").document(mLocationID)
                    .set(mLocationArea)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "onSuccess: location area update success");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: location area update failed...");
                        }
                    });

            // remove the default picture before uploading to DB
            mTripManager.GetPlaces().get(i).getmPhotos().remove(0);

            for(Photo sPhoto : mTripManager.GetPlaces().get(i).getmPhotos()){ ;
                uploadNewPhoto("new_photo", "caption...temp", posting_num_photos++, sPhoto.getImage_path(), null, null, mLocationID, mTripID, sPhoto);
            }
        }
    }

    public void uploadNewPhoto(String photoType, final String caption, final long count, final String imgUrl, Bitmap bm, final String photo_id, final String location_id, final String trip_id, final Photo tphoto){
        // Log.d(TAG, "uploadNewPhoto: attempting to uplaod new photo.");

        StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();

        current_num_photos++;

        if(photoType.equals("new_photo")){
            Log.d(TAG, "uploadNewPhoto: uploading NEW photo.");

            String mImgUrl = imgUrl.substring(7);

            //Log.e(TAG, "uploadNewPhoto: new photo url  " + mImgUrl);
            //convert image url to bitmap
            if(bm == null){
                bm = ImageManager.getBitmap(mImgUrl);
                Log.d(TAG, "uploadNewPhoto:  conversion to bitmap from url");
            }

            byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100);

            tphoto.setUser_id(mAuth.getCurrentUser().getUid());
            tphoto.setLocation_id(location_id);
            tphoto.setTrip_id(trip_id);

            UploadTask uploadTask = null;

            int picture_idx = global_num_photos+current_num_photos;

            uploadTask = mStorageReference.child("photos/users/" + mAuth.getCurrentUser().getUid() + "/" + mAuth.getCurrentUser().getUid() + "_" + picture_idx).putBytes(bytes);
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

                            tphoto.setImage_path(url);

                            Log.e(TAG, "current NUM photos" + current_num_photos );
                            if(--current_num_photos == 0) {
                                // update the total photo count to track how many photos have been uploaded so photo_index can be correctly created.

                                mDB.collection("trips").document(mTripManager.GetTripID())
                                        .set(mTripManager.GetTrip())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "onSuccess: trip update success");

                                                // update the total photo count to track how many photos have been uploaded so photo_index can be correctly created.
                                                global_num_photos += posting_num_photos;
                                                DocumentReference needUpdate = mDB.collection("users").document(mAuth.getCurrentUser().getUid());
                                                needUpdate.update("numOfTrips", ++global_num_trips);
                                                needUpdate.update("numOfphotos", global_num_photos)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Log.d(TAG, "onSuccess: updated: numOfPhotos ");
                                                                posting_num_photos = 0;

                                                                // This needs to be discussed for mTripManager.. memory leak??
                                                                mTripManager.ResetTrip();
                                                                mPlaceListManager.removeAllPlace();

                                                                Intent intent = new Intent(mContext, MainFeedActivity.class);
                                                                mContext.startActivity(intent);
                                                                finish();
                                                            }
                                                        });

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG, "onFailure: trip update failed...");
                                            }
                                        });


                            }
                        }
                    });
                    //add the new photo to 'photos' node and 'user_photos' node
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
        //case new profile photo
        else if(photoType.equals(mContext.getString(R.string.profile_photo))){
            Log.d(TAG, "uploadNewPhoto: uploading new PROFILE photo");

            // String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                    .child("photos/users/" + mAuth.getCurrentUser().getUid() + "/profile_photo");


            //convert image url to bitmap
            if(bm == null){
                bm = ImageManager.getBitmap(imgUrl);
            }
            byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100);

            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Uri firebaseUrl = taskSnapshot.getDownloadUrl();

                    Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();

                    //insert into 'user_account_settings' node
                    //setProfilePhoto(firebaseUrl.toString());

                    //((AccountSettingsActivity)mContext).setViewPager(
                    //        ((AccountSettingsActivity)mContext).pagerAdapter
                    //                .getFragmentNumber(mContext.getString(R.string.edit_profile_fragment))
                    //);

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

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "onStart: called in AddLocationActivity");
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "onStop: called in AddLocationActivity");
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.e(TAG, "onPostResume: called in AddLocationActivity");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: called in AddLocationActivity");
    }
}
