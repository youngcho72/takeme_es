package com.android.takeme_es.Add;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.takeme_es.Common.PlaceAutocompleteAdapter;
import com.android.takeme_es.Data.Location;
import com.android.takeme_es.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AddRecyclerViewLocationAdapter extends RecyclerView.Adapter <AddRecyclerViewLocationAdapter.ViewHolder> {

    private static final String TAG = "AddRecyclerViewLocation";
    private Context mContext;
    private ArrayList<Location> mLocation = new ArrayList<>();

    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    // private PlaceArrayAdapter mPlaceArrayAdapter;
    private PlacesClient placesClient;


    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));

    private FirebaseFirestore mDB;

    public AddRecyclerViewLocationAdapter(Context context, ArrayList<Location> location) {
        this.mContext = context;
        this.mLocation = location;

        mDB = FirebaseFirestore.getInstance();

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private RecyclerView mRecyclerViewPhotos;
        private AutoCompleteTextView mLocationName;
        private TextView mCaption;
        private ImageView mAddPhoto;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mLocationName = (AutoCompleteTextView) itemView.findViewById(R.id.input_search);
            //mCaption = (TextView) itemView.findViewById(R.id.locationcaption);
            mAddPhoto = (ImageView) itemView.findViewById(R.id.imageView_add);
            mRecyclerViewPhotos = (RecyclerView) itemView.findViewById(R.id.recyclerviewphotos);
        }
    }

    @NonNull
    @Override
    public AddRecyclerViewLocationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_recyclerview_location_add,viewGroup,false);
        return new AddRecyclerViewLocationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AddRecyclerViewLocationAdapter.ViewHolder viewHolder, int i) {
        //viewHolder.mLocationName.setText(mLocation.get(i).getLocation_short_name());
        //viewHolder.mCaption.setText(mLocation.get(i).getCaption());

        if(mLocation.get(i).getLocation_short_name() == null) {

            Places.initialize(mContext.getApplicationContext(), "AIzaSyCDCI-UhpEHh924yuxDkjPIcDE5xf9B1H0");
            // Create a new Places client instance.
            placesClient = Places.createClient(mContext);

            viewHolder.mLocationName.setOnItemClickListener(mAutocompleteClickListener);

            mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(mContext, placesClient,
                    LAT_LNG_BOUNDS, null);

            viewHolder.mLocationName.setAdapter(mPlaceAutocompleteAdapter);

            viewHolder.mLocationName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH
                            || actionId == EditorInfo.IME_ACTION_DONE
                            || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                            || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {

                        //execute our method for searching
                        // geoLocate();
                        Log.e(TAG, "onEditorAction: oooo");
                    }

                    return false;
                }
            });
        }
        else
            viewHolder.mLocationName.setText(mLocation.get(i).getLocation_short_name());

        hideSoftKeyboard();


        final int loc_num = i;
        viewHolder.mAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AddActivity.class);
                intent.putExtra("location_num", loc_num);
                Log.e(TAG, "onClick: location_num " + loc_num);

                mContext.startActivity(intent);
            }
        });


        AddRecyclerViewPhotoAdapter mRecyclerViewPhotoAdapter = new AddRecyclerViewPhotoAdapter(mContext, mLocation.get(i).getmPhotos());
        viewHolder.mRecyclerViewPhotos.setHasFixedSize(true);
        viewHolder.mRecyclerViewPhotos.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        viewHolder.mRecyclerViewPhotos.setAdapter(mRecyclerViewPhotoAdapter);
        viewHolder.mRecyclerViewPhotos.setNestedScrollingEnabled(false);

    }

    @Override
    public int getItemCount() {
        return mLocation.size();
    }

    private void hideSoftKeyboard(){
        //mContext.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            hideSoftKeyboard();

            final AutocompletePrediction item = mPlaceAutocompleteAdapter.getItem(i);
            final String placeId = item.getPlaceId();

            Log.e(TAG, "onItemClick: autocomplet ");

        }
    };

}
