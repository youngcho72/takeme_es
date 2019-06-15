package com.android.takeme_es.Data;


public class PictureListData {
    private String mPicture_url;
    private String mDateCreated;

    public PictureListData() {
    }

    public PictureListData(String mPicture_url, String mDateCreated) {
        this.mPicture_url = mPicture_url;
        this.mDateCreated = mDateCreated;
    }

    public String getmPicture_url() {
        return mPicture_url;
    }

    public void setmPicture_url(String mPicture_url) {
        this.mPicture_url = mPicture_url;
    }

    public String getmDateCreated() {
        return mDateCreated;
    }

    public void setmDateCreated(String mDateCreated) {
        this.mDateCreated = mDateCreated;
    }
}
