package com.android.takeme_es.Home;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.takeme_es.Data.Trip;
import com.android.takeme_es.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class RecyclerViewTripAdapter extends RecyclerView.Adapter <RecyclerViewTripAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewTripAdapter";
    private Context mContext;
    private ArrayList<Trip> mTrip = new ArrayList<>();

    private FirebaseFirestore mDB;

    public interface OnTripDetailsClickListner{
        void OnTripDetailsClick(Trip trip);
    }

    OnTripDetailsClickListner mOnTripDetailsClickListner;

    public RecyclerViewTripAdapter(Context context, ArrayList<Trip> trip,OnTripDetailsClickListner onTripDetailsClickListner) {
        this.mContext = context;
        this.mTrip = trip;

        Log.e(TAG, "RecyclerViewTripAdapter: passed trip " + mTrip );

        mDB = FirebaseFirestore.getInstance();
        mOnTripDetailsClickListner = onTripDetailsClickListner;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private RecyclerView mRecyclerViewLocation;
        private ImageView mProfileImage;
        private TextView mUserName, mTripdate;
        private TextView mTripName, mCityName;
        private ImageView mbtn_trip_detail;
        private OnTripDetailsClickListner mTripDetailsClickListner;

        public ViewHolder(@NonNull View itemView, OnTripDetailsClickListner clickListner) {
            super(itemView);

            mUserName = (TextView) itemView.findViewById(R.id.username);
            mProfileImage = (ImageView) itemView.findViewById(R.id.profileimage);
            mTripName = (TextView) itemView.findViewById(R.id.tripName);
            mCityName = (TextView) itemView.findViewById(R.id.cityName);
            mTripdate = (TextView) itemView.findViewById(R.id.tripdate);
            mbtn_trip_detail = (ImageView) itemView.findViewById(R.id.btn_trip_detail);
            mRecyclerViewLocation = (RecyclerView) itemView.findViewById(R.id.recyclerviewtriplist);

            mTripDetailsClickListner = clickListner;
        }
    }

    @NonNull
    @Override
    public RecyclerViewTripAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_recyclerview_trip, viewGroup, false);
        return new ViewHolder(view, mOnTripDetailsClickListner);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewTripAdapter.ViewHolder viewHolder, int i) {
        //final String temp_user_id = mTrip.get(i).getUser_id();
       // final String temp_trip_id = mTrip.get(i).getTrip_id();

        final int selected_idx = i;

        Log.e(TAG, "onBindViewHolder: mtrip i = " + i +"  trip: " + mTrip.get(i));

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String mDateString = sdf.format(mTrip.get(i).getDate());

        viewHolder.mUserName.setText(mTrip.get(i).getUser_name());
        viewHolder.mTripdate.setText(mDateString);
        viewHolder.mTripName.setText(mTrip.get(i).getTrip_title());
        viewHolder.mCityName.setText(mTrip.get(i).getTrip_city());


        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(mTrip.get(i).getProfile_image(), viewHolder.mProfileImage);

        viewHolder.mbtn_trip_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolder.mTripDetailsClickListner.OnTripDetailsClick(mTrip.get(selected_idx));
                //viewHolder.mTripDetailsClickListner.OnTripDetailsClick(selected_idx);
            }
        });

        RecyclerViewLocationAdapter mRecyclerViewLocationAdapter = new RecyclerViewLocationAdapter(mContext, mTrip.get(i).getmLocations());
        viewHolder.mRecyclerViewLocation.setHasFixedSize(true);
        viewHolder.mRecyclerViewLocation.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        viewHolder.mRecyclerViewLocation.setAdapter(mRecyclerViewLocationAdapter);
        viewHolder.mRecyclerViewLocation.setNestedScrollingEnabled(false);

    }

    @Override
    public int getItemCount() {
        return mTrip.size();
    }
}
