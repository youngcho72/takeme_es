package com.android.takeme_es.Home;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.takeme_es.Common.SquareImageView;
import com.android.takeme_es.Common.UniversalImageLoader;
import com.android.takeme_es.R;

import java.util.ArrayList;

public class PictureHorizontalListAdapter extends RecyclerView.Adapter<PictureHorizontalListAdapter.PictureViewHolder>{

    private static final String TAG = "PictureHorizontalListAd";

    private ArrayList<String> picture_url_list = new ArrayList<>();
    private Context mContext;

    public PictureHorizontalListAdapter(Context context, ArrayList<String> picture_url_list) {
        this.picture_url_list = picture_url_list;
        this.mContext = context;
    }

    @Override
    public PictureViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Log.d(TAG, "onCreateViewHolder: horizontal picture recycler view");

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.horizontal_picture_list,viewGroup,false);

        return new PictureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PictureViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: home horizon position  " + position);
        String picture_url = picture_url_list.get(position);

        UniversalImageLoader.setImage(picture_url,holder.mImageView, null, "");

    }

    @Override
    public int getItemCount() {
        return picture_url_list.size();
    }

    /*
    public void addPictureToList(PictureListData picture_url)
    {
        picture_url_list.add(picture_url);
    }
*/
    public class PictureViewHolder extends RecyclerView.ViewHolder{
        private SquareImageView mImageView;

        public PictureViewHolder(View view) {
            super(view);
            Log.d(TAG, "PictureViewHolder: horizaion view holder");
            this.mImageView = (SquareImageView) view.findViewById(R.id.horizontal_pic);

        }
    }

}
