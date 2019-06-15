package com.android.takeme_es.Profile;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.takeme_es.Common.UniversalImageLoader;
import com.android.takeme_es.Data.Photo;
import com.android.takeme_es.R;

import java.util.ArrayList;

public class HorizontalRecyclerViewAdapter extends RecyclerView.Adapter<HorizontalRecyclerViewAdapter.HorizontalViewHolder> {

    private static final String TAG = "HorizontalRecyclerViewA";
    private ArrayList<String> picture_urls;
    private Context context;
    private static final int ACTIVITY_NUM = 3;


    public interface OnImageSelectedListner{
        void onImageSelected(Photo photo, int activityNumber);
    }

    OnImageSelectedListner mOnImageSelectedListner;

    public HorizontalRecyclerViewAdapter(Context context, ArrayList<String> picture_urls) {
        this.picture_urls = picture_urls;

        this.context = context;
        this.mOnImageSelectedListner = (OnImageSelectedListner) context;
    }

    @NonNull
    @Override
    public HorizontalViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Log.d(TAG, "onCreateViewHolder: horizon  i  " + i);
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_add_main_item,viewGroup,false);

        return new HorizontalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalViewHolder horizontalViewHolder, int i) {

        Log.d(TAG, "onBindViewHolder: Horizontal i " + i + "getiteecount()" + getItemCount());

       // if(i >= getItemCount())
         //   return;

        String mSinglePictureURL = picture_urls.get(i);

        if(mSinglePictureURL != "")
         UniversalImageLoader.setImage(mSinglePictureURL, horizontalViewHolder.mImageView, null, "");

        horizontalViewHolder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // mOnImageSelectedListner.onImageSelected(photos.get(position), ACTIVITY_NUM);
            }
        });
    }

    @Override
    public int getItemCount() {
        return picture_urls.size();
    }

    public class HorizontalViewHolder extends RecyclerView.ViewHolder{

        protected ImageView mImageView;

        public HorizontalViewHolder(@NonNull View itemView) {
            super(itemView);

            Log.d(TAG, "HorizontalViewHolder: horizon viewholder create");
            mImageView = (ImageView) itemView.findViewById(R.id.imageView_add);

            //itemView.setOnClickListener(new View.OnClickListener() {
              //  @Override
                //public void onClick(View v) {

                  //  Log.d(TAG, "onClick: clicked....");

                    //mOnImageSelectedListner.onImageSelected(photos.get(position), ACTIVITY_NUM);


                   // CameraPreviewFragment fragment = new CameraPreviewFragment();
                   // FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
                   // transaction.replace(R.id.container, fragment);
                    //transaction.addToBackStack(getString(R.string.view_profile_fragment));
                    //transaction.commit();

               // }
            //});
        }
    }


}
