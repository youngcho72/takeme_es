package com.android.takeme_es.Data;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;


public class Trip implements Parcelable {
    private static final String TAG = "Trip data model";
    private ArrayList<String> likes;
    private @ServerTimestamp Date date;
    private int private_post;
    private String trip_city;
    private String trip_title;
    private String tag;
    private String user_id;
    private String trip_id;
    private GeoPoint coordinates;
    private ArrayList<Location> mLocations;
    private String user_name;
    private String profile_image;



    public Trip() {
        mLocations = new ArrayList<>();
    }

    public Trip(ArrayList<String> likes, Date date, int private_post, String trip_city, String trip_title, String tag, String user_id, String trip_id, GeoPoint coordinates, ArrayList<Location> mLocations, String user_name, String profile_image) {
        this.likes = likes;
        this.date = date;
        this.private_post = private_post;
        this.trip_city = trip_city;
        this.trip_title = trip_title;
        this.tag = tag;
        this.user_id = user_id;
        this.trip_id = trip_id;
        this.coordinates = coordinates;
        this.mLocations = mLocations;
        this.user_name = user_name;
        this.profile_image = profile_image;
    }

    public ArrayList<String> getLikes() {
        return likes;
    }

    public void setLikes(ArrayList<String> likes) {
        this.likes = likes;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getPrivate_post() {
        return private_post;
    }

    public void setPrivate_post(int private_post) {
        this.private_post = private_post;
    }

    public String getTrip_city() {
        return trip_city;
    }

    public void setTrip_city(String trip_city) {
        this.trip_city = trip_city;
    }

    public String getTrip_title() {
        return trip_title;
    }

    public void setTrip_title(String trip_title) {
        this.trip_title = trip_title;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }

    public GeoPoint getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(GeoPoint coordinates) {
        this.coordinates = coordinates;
    }

    public ArrayList<Location> getmLocations() {
        return mLocations;
    }

    public void setmLocations(ArrayList<Location> mLocations) {
        this.mLocations = mLocations;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {


        parcel.writeList(likes);
        parcel.writeLong(date.getTime());
        parcel.writeInt(private_post);
        parcel.writeString(trip_city);
        parcel.writeString(trip_title);
        parcel.writeString(tag);
        parcel.writeString(user_id);
        parcel.writeString(trip_id);
        parcel.writeDouble(coordinates.getLatitude());
        parcel.writeDouble(coordinates.getLongitude());
        parcel.writeTypedList(mLocations);

        parcel.writeString(user_name);
        parcel.writeString(profile_image);

        Log.e(TAG, "writeToParcel: " + parcel );
    }

    protected Trip(Parcel in) {
        double lat,lng;

        likes = in.readArrayList(null);
        date = new Date(in.readLong());
        private_post = in.readInt();
        trip_city = in.readString();
        trip_title = in.readString();
        tag = in.readString();
        user_id = in.readString();
        trip_id = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
        coordinates = new GeoPoint(lat,lng);
        in.readTypedList(this.mLocations, Location.CREATOR);
        user_name = in.readString();
        profile_image = in.readString();
    }

    public static final Creator<Trip> CREATOR = new Creator<Trip>() {
        @Override
        public Trip createFromParcel(Parcel in) {
            return new Trip(in);
        }

        @Override
        public Trip[] newArray(int size) {
            return new Trip[size];
        }
    };

    @Override
    public String toString() {
        return "Trip{" +
                "likes=" + likes +
                ", date=" + date +
                ", private_post=" + private_post +
                ", trip_city='" + trip_city + '\'' +
                ", trip_title='" + trip_title + '\'' +
                ", tag='" + tag + '\'' +
                ", user_id='" + user_id + '\'' +
                ", trip_id='" + trip_id + '\'' +
                ", position=" + coordinates +
                ", mLocations=" + mLocations +
                ", user_name='" + user_name + '\'' +
                ", profile_image='" + profile_image + '\'' +
                '}';
    }

}
