package com.android.takeme_es.Add;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.takeme_es.Data.Photo;
import com.android.takeme_es.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class AddRecyclerViewPhotoAdapter extends RecyclerView.Adapter <AddRecyclerViewPhotoAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewPhotoAdapter";
    private Context mContext;
    private ArrayList<Photo> mPhoto = new ArrayList<>();


    private FirebaseFirestore mDB;

    public interface OnImageSelectedListner{
        void OnImageSelected();
    }

    OnImageSelectedListner mOnImageSelectedListner;

    public AddRecyclerViewPhotoAdapter(Context context, ArrayList<Photo> photo) {
        this.mContext = context;
        this.mPhoto = photo;

        mOnImageSelectedListner = (OnImageSelectedListner) context;

        mDB = FirebaseFirestore.getInstance();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView mPhotoImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mPhotoImage = (ImageView) itemView.findViewById(R.id.photoimage);

            mPhotoImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mOnImageSelectedListner.OnImageSelected();

                }
            });
        }
    }

    @NonNull
    @Override
    public AddRecyclerViewPhotoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_recyclerview_photo_add,viewGroup,false);
        return new AddRecyclerViewPhotoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AddRecyclerViewPhotoAdapter.ViewHolder viewHolder, int i) {
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(mPhoto.get(i).getImage_path(), viewHolder.mPhotoImage);
    }

    @Override
    public int getItemCount() {
        return mPhoto.size();
    }
}