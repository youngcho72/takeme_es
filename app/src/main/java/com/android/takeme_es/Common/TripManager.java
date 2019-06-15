package com.android.takeme_es.Common;

import com.android.takeme_es.Data.Location;
import com.android.takeme_es.Data.Photo;
import com.android.takeme_es.Data.Trip;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Date;

public class TripManager {
    private static TripManager mTripManager;
    private Trip mTrip;

    private TripManager(){
        mTrip = new Trip();
    }

    public static TripManager getInstance(){
        if(mTripManager == null){
            synchronized (TripManager.class){
                if(mTripManager == null){
                    mTripManager = new TripManager();
                }
            }
        }
        return mTripManager;
    }

    public Trip GetTrip(){
        return mTrip;
    }

    public void ResetTrip(){
        mTrip = null;

        mTrip = new Trip();
    }

    public void AddPlace(Location location){
        mTrip.getmLocations().add(location);
    }

    public ArrayList<Location> GetPlaces(){
        return mTrip.getmLocations();
    }

    public void SetTripName(String name){
        mTrip.setTrip_title(name);
    }

    public String GetTripName(){
        return mTrip.getTrip_title();
    }

    public void SetDate(Date date){
        mTrip.setDate(date);
    }

    public void SetCity(String city){
        mTrip.setTrip_city(city);
    }

    public ArrayList<Photo> GetPhotos(int placeId) { return mTrip.getmLocations().get(placeId).getmPhotos();}

    public void SetPhotos(int placeId, Photo photo) { mTrip.getmLocations().get(placeId).getmPhotos().add(photo);}

    public void SetCoordinates(LatLng latlng){
        GeoPoint mGeopoint = new GeoPoint(latlng.latitude,latlng.longitude);

        mTrip.setCoordinates(mGeopoint);
    }

    public LatLng GetCoordinates(){
        LatLng mLatLng = new LatLng(mTrip.getCoordinates().getLatitude(),mTrip.getCoordinates().getLongitude());

        return mLatLng;
    }

    public void SetTripID(String tripID){
        mTrip.setTrip_id(tripID);
    }

    public void SetTripPrivacy(int privacy){
        mTrip.setPrivate_post(privacy);
    }

    public void SetTripUserID(String userID){
        mTrip.setUser_id(userID);
    }

    public String GetTripUserID(){
        return mTrip.getUser_id();
    }

    public String GetTripID(){
        return mTrip.getTrip_id();
    }

    public void SetProfileImage(String profileImage){
        mTrip.setProfile_image(profileImage);
    }

    public void SetUserName(String userName){
        mTrip.setUser_name(userName);
    }

}
