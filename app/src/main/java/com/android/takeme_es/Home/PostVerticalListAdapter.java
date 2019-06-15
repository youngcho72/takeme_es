package com.android.takeme_es.Home;

import android.content.Context;
import android.support.annotation.NonNull;
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

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostVerticalListAdapter extends RecyclerView.Adapter<PostVerticalListAdapter.PostViewHolder>{

    private static final String TAG = "PostVerticalListAdapter";

    private Context mContext;

    ArrayList<Trip> mPostUnit = new ArrayList<>();


    public PostVerticalListAdapter(Context mContext, ArrayList<Trip> mPostUnit) {
        this.mContext = mContext;
        this.mPostUnit = mPostUnit;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Log.d(TAG, "onCreateViewHolder: vert picture recycler view");

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.vertical_post_list,viewGroup,false);

        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PostViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: vert position  " + position);

        Trip mPicture= mPostUnit.get(position);

        FirebaseFirestore mDB = FirebaseFirestore.getInstance();
/* temp
        mDB.collection("users").whereEqualTo("user_id", mPicture.getmUserID())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                            ImageLoader imageLoader = ImageLoader.getInstance();

                            imageLoader.displayImage(doc.toObject(User.class).getProfile_url(), holder.image);
                            holder.username.setText(doc.toObject(User.class).getEmail());

                        }
                    }
                });

        holder.mDate.setText(mPicture.getmDate());

        PictureHorizontalListAdapter itemListDataAdapter = new PictureHorizontalListAdapter(mContext, mPicture.getPicture_url());

        holder.picktureHorizontalList.setHasFixedSize(true);
        holder.picktureHorizontalList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        holder.picktureHorizontalList.setAdapter(itemListDataAdapter);
        holder.picktureHorizontalList.setNestedScrollingEnabled(false);
*/
    }

    @Override
    public int getItemCount() {
        return mPostUnit.size();
    }

    /*
    public void addPictureToList(PostUnit mSingleUnit)
    {
        mPostUnit.add(mSingleUnit);
    }
    */

    public class PostViewHolder extends RecyclerView.ViewHolder{
        private RecyclerView picktureHorizontalList;
        private TextView mDate,username;
        private ImageView mHeart;
        private CircleImageView image;

        public PostViewHolder(@NonNull View itemView) {
            
            super(itemView);
            Log.d(TAG, "PostViewHolder: vert");
            this.picktureHorizontalList = (RecyclerView) itemView.findViewById(R.id.recycler_view_horizontal_list);
            this.mDate = (TextView) itemView.findViewById(R.id.dateCreated);
            this.username = (TextView) itemView.findViewById(R.id.username);
            this.image = (CircleImageView) itemView.findViewById(R.id.profile_image);
        }

    }

}
