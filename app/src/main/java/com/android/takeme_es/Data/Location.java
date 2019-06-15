package com.android.takeme_es.Data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

public class Location implements Parcelable {
    private String location_short_name;
    private int rating;
    private String caption;
    private String trip_id;
    private String user_id;
    private String location_id;
    private GeoPoint coordinates;
    private ArrayList<Photo> mPhotos;

    public Location() {

        mPhotos = new ArrayList<>();
    }

    public Location(String location_short_name, int rating, String caption, String trip_id, String user_id, String location_id, GeoPoint coordinates, ArrayList<Photo> mPhotos) {
        this.location_short_name = location_short_name;
        this.rating = rating;
        this.caption = caption;
        this.trip_id = trip_id;
        this.user_id = user_id;
        this.location_id = location_id;
        this.coordinates = coordinates;
        this.mPhotos = mPhotos;
    }

    public String getLocation_short_name() {
        return location_short_name;
    }

    public void setLocation_short_name(String location_short_name) {
        this.location_short_name = location_short_name;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getLocation_id() {
        return location_id;
    }

    public void setLocation_id(String location_id) {
        this.location_id = location_id;
    }

    public GeoPoint getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(GeoPoint coordinates) {
        this.coordinates = coordinates;
    }

    public ArrayList<Photo> getmPhotos() {
        return mPhotos;
    }

    public void setmPhotos(ArrayList<Photo> mPhotos) {
        this.mPhotos = mPhotos;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(location_short_name);
        parcel.writeInt(rating);
        parcel.writeString(caption);
        parcel.writeString(trip_id);
        parcel.writeString(user_id);
        parcel.writeString(location_id);
        parcel.writeDouble(coordinates.getLatitude());
        parcel.writeDouble(coordinates.getLongitude());
        parcel.writeTypedList(mPhotos);
    }

    protected Location(Parcel in) {
        double lat,lng;

        location_short_name = in.readString();
        rating = in.readInt();
        caption = in.readString();
        trip_id = in.readString();
        user_id = in.readString();
        location_id = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
        coordinates = new GeoPoint(lat,lng);
        in.readTypedList(mPhotos, Photo.CREATOR);
    }

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    @Override
    public String toString() {
        return "Location{" +
                "location_short_name='" + location_short_name + '\'' +
                ", rating=" + rating +
                ", caption='" + caption + '\'' +
                ", trip_id='" + trip_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", location_id='" + location_id + '\'' +
                ", mPhotos=" + mPhotos +
                '}';
    }

}
