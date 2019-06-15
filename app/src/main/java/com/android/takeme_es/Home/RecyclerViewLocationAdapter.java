package com.android.takeme_es.Home;

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

public class RecyclerViewLocationAdapter extends RecyclerView.Adapter <RecyclerViewLocationAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewLocationAda";
    private Context mContext;
    private ArrayList<Location> mLocation = new ArrayList<>();

    private FirebaseFirestore mDB;

    public RecyclerViewLocationAdapter(Context context, ArrayList<Location> location) {
        this.mContext = context;
        this.mLocation = location;

        mDB = FirebaseFirestore.getInstance();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private RecyclerView mRecyclerViewPhotos;
        private TextView mLocationName;
        private TextView mCaption;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mLocationName = (TextView) itemView.findViewById(R.id.placeTitle);
            mCaption = (TextView) itemView.findViewById(R.id.placeCaption);
            mRecyclerViewPhotos = (RecyclerView) itemView.findViewById(R.id.recyclerviewphotos);
        }
    }

    @NonNull
    @Override
    public RecyclerViewLocationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_recyclerview_location,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewLocationAdapter.ViewHolder viewHolder, int i) {
        viewHolder.mLocationName.setText(mLocation.get(i).getLocation_short_name());
        viewHolder.mCaption.setText(mLocation.get(i).getCaption());

        RecyclerViewPhotoAdapter mRecyclerViewPhotoAdapter = new RecyclerViewPhotoAdapter(mContext, mLocation.get(i).getmPhotos());
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