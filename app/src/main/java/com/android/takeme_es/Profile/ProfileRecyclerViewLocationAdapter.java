package com.android.takeme_es.Profile;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.takeme_es.Data.Location;
import com.android.takeme_es.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ProfileRecyclerViewLocationAdapter extends RecyclerView.Adapter <ProfileRecyclerViewLocationAdapter.ViewHolder> {

    private static final String TAG = "ProfileRecyclerViewLocationAdapter";
    private Context mContext;
    private ArrayList<Location> mLocation = new ArrayList<>();

    private FirebaseFirestore mDB;

    public ProfileRecyclerViewLocationAdapter(Context context, ArrayList<Location> location) {
        this.mContext = context;
        this.mLocation = location;

        mDB = FirebaseFirestore.getInstance();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private RecyclerView mRecyclerViewPhotos;
        private TextView mPlaceCaption;
        private TextView mPlaceTitle;
        //private ImageView mAddPhoto;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mPlaceCaption = (TextView) itemView.findViewById(R.id.placeCaption);
            mPlaceTitle = (TextView) itemView.findViewById(R.id.placeTitle);
            mRecyclerViewPhotos = (RecyclerView) itemView.findViewById(R.id.recyclerviewphotos);
        }
    }

    @NonNull
    @Override
    public ProfileRecyclerViewLocationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_recyclerview_location_profile,viewGroup,false);
        return new ProfileRecyclerViewLocationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProfileRecyclerViewLocationAdapter.ViewHolder viewHolder, int i) {
        viewHolder.mPlaceTitle.setText(mLocation.get(i).getLocation_short_name());
        viewHolder.mPlaceCaption.setText(mLocation.get(i).getCaption());


       ProfileRecyclerViewPhotoAdapter mRecyclerViewPhotoAdapter = new ProfileRecyclerViewPhotoAdapter(mContext, mLocation.get(i).getmPhotos());
        viewHolder.mRecyclerViewPhotos.setHasFixedSize(true);
        viewHolder.mRecyclerViewPhotos.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        viewHolder.mRecyclerViewPhotos.setAdapter(mRecyclerViewPhotoAdapter);
        viewHolder.mRecyclerViewPhotos.setNestedScrollingEnabled(false);

    }

    @Override
    public int getItemCount() {
        return mLocation.size();
    }

}
