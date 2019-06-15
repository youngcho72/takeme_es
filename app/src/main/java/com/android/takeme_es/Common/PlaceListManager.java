package com.android.takeme_es.Common;

import java.util.ArrayList;

public class PlaceListManager {
    private static PlaceListManager mPlaceListManager;
    private ArrayList<String> placeList;

    private PlaceListManager(){
        String default_place;

        default_place = "Place Name..";
        placeList = new ArrayList<>();
        placeList.add(default_place);
    }

    public static PlaceListManager getInstance(){
        if(mPlaceListManager == null ) {
            synchronized (PlaceListManager.class){
                if(mPlaceListManager == null )
                    mPlaceListManager = new PlaceListManager();
            }
        }
        return mPlaceListManager;
    }

    public void addPlace(String placeName){
        placeList.add(placeName);
    }

    public void setPlace(int position, String placeName){
        placeList.set(position, placeName);
    }

    public ArrayList<String> getPlaceList(){
        return placeList;
    }
    public String getPlace(int position){
        return placeList.get(position);
    }

    public void removePlace(int postion){
        placeList.remove(postion);
    }

    public void removeAllPlace(){
        String default_place;

        placeList.clear();
        default_place = "Place Name..";
        placeList.add(default_place);
    }

    public int getSize(){
        return placeList.size();
    }
}
