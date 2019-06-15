package com.android.takeme_es.Common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.takeme_es.Data.ClusterMarker;
import com.android.takeme_es.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.min;


public class MyClusterManagerRenderer extends DefaultClusterRenderer<ClusterMarker>
{
    private static final String TAG = "MyClusterManagerRender";

    private final IconGenerator iconGenerator;
    private final IconGenerator mClusterIconGenerator;
    private final ImageView imageView;
    private final int markerWidth;
    private final int markerHeight;
    private Context mContext;
    Bitmap icon;

    private LayoutInflater mInflater;
    private View multiProfile;
    private final ImageView mClusterImageView;
    private int count_pic;

    public MyClusterManagerRenderer(Context context, GoogleMap googleMap,
                                    ClusterManager<ClusterMarker> clusterManager) {

        super(context, googleMap, clusterManager);

        mContext = context;
        // initialize cluster item icon generator
        iconGenerator = new IconGenerator(context.getApplicationContext());
        mClusterIconGenerator = new IconGenerator(context.getApplicationContext());

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        multiProfile = mInflater.inflate(R.layout.multi_profile, null);
        mClusterIconGenerator.setContentView(multiProfile);
        mClusterImageView = (ImageView) multiProfile.findViewById(R.id.image);

        imageView = new ImageView(context.getApplicationContext());
        markerWidth = (int) context.getResources().getDimension(R.dimen.custom_marker_image);
        markerHeight = (int) context.getResources().getDimension(R.dimen.custom_marker_image);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(markerWidth, markerHeight));
        int padding = (int) context.getResources().getDimension(R.dimen.custom_marker_padding);
        imageView.setPadding(padding, padding, padding, padding);
        iconGenerator.setContentView(imageView);

    }

    /**
     * Rendering of the individual ClusterItems
     * @param item
     * @param markerOptions
     */
    @Override
    protected void onBeforeClusterItemRendered(ClusterMarker item, MarkerOptions markerOptions) {

       // imageView.setImageResource(item.getIconPicture());
        icon = iconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.getTitle());
    }

    @Override
    protected void onClusterItemRendered(ClusterMarker clusterItem, final Marker marker) {
        Glide.with(mContext)
                .load(clusterItem.getPhoto_img())
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .thumbnail(0.1f)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable drawable, Transition<? super Drawable> transition) {
                        Log.e(TAG, "Single pic");
                        imageView.setImageDrawable(drawable);
                        icon = iconGenerator.makeIcon();
                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
                    }
                });
    }


    @Override
    protected void onClusterRendered(final Cluster<ClusterMarker> cluster, final Marker marker) {
        final List<Drawable> profilePhotos = new ArrayList<>(min(4, cluster.getSize()));
        final int width = markerWidth;
        final int height = markerHeight;
        Bitmap dummyBitmap = null;
        Drawable drawable;
        final int clusterSize = min(cluster.getSize(), 4);
        final int[] count = {0};


        for (final ClusterMarker p : cluster.getItems()) {
            // Draw 4 at most.
            if (profilePhotos.size() == 4)
            {
                profilePhotos.clear();
                break;
            }
            try {
                //count_pic++;
                //final int finalCount_pic = count_pic;
                Glide.with(mContext.getApplicationContext())
                        .load(p.getPhoto_img())
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(new SimpleTarget<Drawable>(){
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                resource.setBounds(0, 0, width, height);
                                profilePhotos.add(resource);


                                MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
                                multiDrawable.setBounds(0, 0, width, height);

                                Log.e(TAG, "multi pictures:  " + profilePhotos);
                                mClusterImageView.setImageDrawable(multiDrawable);
                                Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
                                marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));

                               // count_pic++;
                               // if(count_pic == clusterSize){
                               //     count_pic = 0;
                                //    profilePhotos.clear();
                                //}

                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
    }

    @Override
    protected void onBeforeClusterRendered(final Cluster<ClusterMarker> cluster, final MarkerOptions markerOptions) {
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        // Always render clusters.
        return cluster.getSize() > 1;
    }


    /**
     * Update the GPS coordinate of a ClusterItem
     * @param clusterMarker
     */
   // public void setUpdateMarker(ClusterMarker clusterMarker) {
     //   Marker marker = getMarker(clusterMarker);
     //   if (marker != null) {
    //        marker.setPosition(clusterMarker.getPosition());
     //   }
   // }
}
