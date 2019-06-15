package com.android.takeme_es.Profile;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.takeme_es.Data.Trip;
import com.android.takeme_es.R;

import java.util.ArrayList;

public class VerticalRecyclerViewAdapter extends RecyclerView.Adapter<VerticalRecyclerViewAdapter.VerticalViewHolder> {

    private static final String TAG = "VerticalRecyclerViewAda";
    private Context mContext;
    private ArrayList<Trip> mURL;

    public VerticalRecyclerViewAdapter(Context context, ArrayList<Trip> listURL) {
        mContext = context;
        mURL = listURL;
    }

    public class VerticalViewHolder extends RecyclerView.ViewHolder {

        protected RecyclerView mRecyclerView;
        protected TextView mDateCreated;

        public VerticalViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "VerticalViewHolder: verticl  view holder create ");
            this.mDateCreated = (TextView) itemView.findViewById(R.id.dateCreated);
            this.mRecyclerView = (RecyclerView) itemView.findViewById(R.id.hori_recycler_view);
        }
    }

    @NonNull
    @Override
    public VerticalViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Log.d(TAG, "onCreateViewHolder: vertucak  i   " + i);
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_add_main_horizantal,viewGroup,false);

        return new VerticalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VerticalViewHolder verticalViewHolder, int i) {

        Log.d(TAG, "onBindViewHolder: vertical i " + i + "getitemcount()  " + getItemCount());

        Trip mSingleURL = mURL.get(i);

        //verticalViewHolder.mDateCreated.setText(mSingleURL.getmDate());

       // HorizontalRecyclerViewAdapter itemHorizontalAdapter = new HorizontalRecyclerViewAdapter(mContext, mSingleURL.getPicture_url());

      //  verticalViewHolder.mRecyclerView.setHasFixedSize(true);
       // verticalViewHolder.mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
     //   verticalViewHolder.mRecyclerView.setAdapter(itemHorizontalAdapter);
     //   verticalViewHolder.mRecyclerView.setNestedScrollingEnabled(false);

    }

    @Override
    public int getItemCount() {
        return mURL.size();
    }
}
