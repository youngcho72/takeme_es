
package com.android.takeme_es.Add;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.takeme_es.R;

import java.util.ArrayList;

public class PlaceListRecyclerAdapter extends RecyclerView.Adapter<PlaceListRecyclerAdapter.ViewHolder>{

    private static final String TAG = "PlaceListRecyclerAdapte";
    private ArrayList<String> mPlaces = new ArrayList<>();
    private UserListRecyclerClickListener mClickListener;
    private Context mContext;

    public interface OnEditPlaceSelectedLinstner{
        void OnEditPlaceSelected(int position);
    }

    OnEditPlaceSelectedLinstner mOnEditPlaceSelectedLinstner;

    public PlaceListRecyclerAdapter(Context context, ArrayList<String> places, UserListRecyclerClickListener clickListener) {
        mPlaces = places;
        mClickListener = clickListener;
        mContext = context;
        mOnEditPlaceSelectedLinstner = (OnEditPlaceSelectedLinstner) context;
    }

    public PlaceListRecyclerAdapter(Context context, ArrayList<String> places) {
        mPlaces = places;
        mClickListener = null;
        mContext = context;
        mOnEditPlaceSelectedLinstner = (OnEditPlaceSelectedLinstner) context;
    }
    @NonNull
    @Override
    public PlaceListRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_place_item_list, parent, false);
        final ViewHolder holder = new PlaceListRecyclerAdapter.ViewHolder(view, mClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceListRecyclerAdapter.ViewHolder holder, int position) {

        holder.place_name.setText(mPlaces.get(position));
    }

    @Override
    public int getItemCount() {
        return mPlaces.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView place_name;
        ImageView edit_place;
        UserListRecyclerClickListener mClickListener;

        public ViewHolder(View itemView, UserListRecyclerClickListener clickListener) {
            super(itemView);
            place_name = itemView.findViewById(R.id.place_name);
            edit_place = itemView.findViewById(R.id.edit_place);

            edit_place.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e(TAG, "onClick: " + getAdapterPosition());
                    mOnEditPlaceSelectedLinstner.OnEditPlaceSelected(getAdapterPosition());
                }
            });

        }

    }

    public interface UserListRecyclerClickListener{
        void onUserClicked(int position);
    }
}
