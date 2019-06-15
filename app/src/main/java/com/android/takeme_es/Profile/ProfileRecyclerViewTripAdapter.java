package com.android.takeme_es.Profile;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.takeme_es.Data.Trip;
import com.android.takeme_es.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ProfileRecyclerViewTripAdapter extends RecyclerView.Adapter <ProfileRecyclerViewTripAdapter.ViewHolder> {

    private static final String TAG = "ProfileRecyclerViewTripAdapter";
    private Context mContext;
    private ArrayList<Trip> mTrips = new ArrayList<>();

    private FirebaseFirestore mDB;

    public interface OnTripDetailsClickListner{
        void OnTripDetailsClick(Trip trip);
    }

    OnTripDetailsClickListner mOnTripDetailsClickListner;

    public ProfileRecyclerViewTripAdapter(Context context, ArrayList<Trip> trips,OnTripDetailsClickListner onTripDetailsClickListner) {
        this.mContext = context;
        this.mTrips = trips;

        mOnTripDetailsClickListner = onTripDetailsClickListner;

        mDB = FirebaseFirestore.getInstance();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RecyclerView mRecyclerViewLocation;
        private ImageView mbtn_trip_detail;
        private TextView mTripName, mCityName, mTripdate;
        private OnTripDetailsClickListner mTripDetailsClickListner;

        public ViewHolder(@NonNull View itemView, OnTripDetailsClickListner clickListner) {
            super(itemView);

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
    public ProfileRecyclerViewTripAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_recyclerview_trip_profile,viewGroup,false);
        return new ViewHolder(view, mOnTripDetailsClickListner);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProfileRecyclerViewTripAdapter.ViewHolder viewHolder, int i) {
        // final String temp_user_id = mTrips.get(i).getUser_id();
        final String temp_trip_id = mTrips.get(i).getTrip_id();
        final int selected_idx = i;

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String mDateString = sdf.format(mTrips.get(i).getDate());

        viewHolder.mTripName.setText(mTrips.get(i).getTrip_title());
        viewHolder.mCityName.setText(mTrips.get(i).getTrip_city());
        viewHolder.mTripdate.setText(mDateString);

        viewHolder.mbtn_trip_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolder.mTripDetailsClickListner.OnTripDetailsClick(mTrips.get(selected_idx));
            }
        });
        ProfileRecyclerViewLocationAdapter mRecyclerViewLocationAdapter = new ProfileRecyclerViewLocationAdapter(mContext, mTrips.get(i).getmLocations());
        viewHolder.mRecyclerViewLocation.setHasFixedSize(true);
        viewHolder.mRecyclerViewLocation.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        viewHolder.mRecyclerViewLocation.setAdapter(mRecyclerViewLocationAdapter);
        viewHolder.mRecyclerViewLocation.setNestedScrollingEnabled(false);

    }

    @Override
    public int getItemCount() {
        return mTrips.size();
    }
}
