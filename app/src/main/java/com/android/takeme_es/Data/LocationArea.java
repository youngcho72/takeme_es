package com.android.takeme_es.Data;

public class LocationArea {
    private String trip_id;
    private String user_id;
    private String location_id;

    public LocationArea() {

    }

    public LocationArea(String trip_id, String user_id, String location_id) {
        this.trip_id = trip_id;
        this.user_id = user_id;
        this.location_id = location_id;
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
}
