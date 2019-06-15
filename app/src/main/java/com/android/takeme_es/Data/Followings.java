package com.android.takeme_es.Data;

import java.util.ArrayList;

public class Followings {
    private ArrayList<String> tripid_list;

    public Followings() {
    }

    public Followings(ArrayList<String> tripid_list) {
        this.tripid_list = tripid_list;
    }

    public ArrayList<String> getTripid_list() {
        return tripid_list;
    }

    public void setTripid_list(ArrayList<String> tripid_list) {
        this.tripid_list = tripid_list;
    }
}
