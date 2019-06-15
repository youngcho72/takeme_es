package com.android.takeme_es.Add;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.takeme_es.Home.MainFeedActivity;
import com.android.takeme_es.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static android.app.Activity.RESULT_OK;

public class CameraPreviewFragment extends Fragment {

    private static final String TAG = "CameraPreviewFragment";

    // widgets
    private CameraSurfaceView mSurfaceView;
    private ImageView take_pic;
    private Button mGallary, mphoto;

    private ImageView mOutputImage;

    private Uri filePath;
    private Bitmap bitmap;
    private byte[] data_byte;
    private Context mContext = getActivity();

    public CameraPreviewFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera_preview, container, false);

        mSurfaceView = (CameraSurfaceView) view.findViewById(R.id.screen_surface);
        take_pic = (ImageView) view.findViewById(R.id.imageView);
       // mGallary = (Button) view.findViewById(R.id.gallary);
        mOutputImage = (ImageView) view.findViewById(R.id.output_image);
        // mphoto = (Button) view.findViewById(R.id.photo);

        /*
        mGallary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), 0);

            }
        });

        mphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload_picture(data_byte, 1);
            }
        });

        */

        take_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSurfaceView.capture(new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 8;
                        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                        bitmap = getResizedBitmap(bitmap, 300);
                        // mOutputImage.setRotation(90);
                        // mOutputImage.setImageBitmap(bitmap);

                        Log.d(TAG, "onClick: post activity starts  ");
                        Intent intent = new Intent(getActivity(), PostActivity.class);
                        intent.putExtra(getString(R.string.selected_bitmap), bitmap);
                        startActivity(intent);

                        //camera.startPreview();
                    }
                });
            }
        });

        return view;
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //request코드가 0이고 OK를 선택했고 data에 뭔가가 들어 있다면
        if(requestCode == 0 && resultCode == RESULT_OK){
            filePath = data.getData();
            Log.d(TAG, "uri:" + String.valueOf(filePath));
           // try {
                //Uri 파일을 Bitmap으로 만들어서 ImageView에 집어 넣는다.
               // Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
               // ivPreview.setImageBitmap(bitmap);
            ///} catch (IOException e) {
            //    e.printStackTrace();
            //}
        }
    }

    private void upload_picture(byte[] bytes, final int num){

        Log.d(TAG, "uploadNewPhoto: uploading NEW photo.");

        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("photos/users/" + user_id + "/" + num);

        //mStorageReference
        //.child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo" + (count + 1));

        //convert image url to bitmap
        // if(bm == null){
        //    bm = ImageManager.getBitmap(imgUrl);
        //}

        // byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100);

        UploadTask uploadTask = null;
        uploadTask = storageReference.putBytes(bytes);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                // Uri download_link = taskSnapshot.getDownloadUrl();

                Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();

                //add the new photo to 'photos' node and 'user_photos' node
                // addPhotoToDatabase(caption, firebaseUrl.toString());

                //navigate to the main feed so the user can see their photo
                Intent intent = new Intent(mContext, MainFeedActivity.class);
                mContext.startActivity(intent);
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

                //if(progress - 15 > mPhotoUploadProgress){
                //    Toast.makeText(mContext, "photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                //    mPhotoUploadProgress = progress;
                //}

                Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
            }
        });
    }

}
