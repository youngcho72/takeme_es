package com.android.takeme_es.Data;

import java.util.ArrayList;

/**
 * Created by User on 6/26/2017.
 */

public class User{

    private String user_name;
    private String email;
    private String user_id;
    private String gender;
    private String profile_image;

    private ArrayList<String> following;
    private ArrayList<String> follower;
    private String phone_number;
    private int numOfphotos;
    private int numOfTrips;

    public User() {
    }

    public User(String user_name, String email, String user_id, String gender, String profile_image, ArrayList<String> following, ArrayList<String> follower, String phone_number, int numOfphotos, int numOfTrips) {
        this.user_name = user_name;
        this.email = email;
        this.user_id = user_id;
        this.gender = gender;
        this.profile_image = profile_image;
        this.following = following;
        this.follower = follower;
        this.phone_number = phone_number;
        this.numOfphotos = numOfphotos;
        this.numOfTrips = numOfTrips;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public ArrayList<String> getFollowing() {
        return following;
    }

    public void setFollowing(ArrayList<String> following) {
        this.following = following;
    }

    public ArrayList<String> getFollower() {
        return follower;
    }

    public void setFollower(ArrayList<String> follower) {
        this.follower = follower;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public int getNumOfphotos() {
        return numOfphotos;
    }

    public void setNumOfphotos(int numOfphotos) {
        this.numOfphotos = numOfphotos;
    }

    public int getNumOfTrips() {
        return numOfTrips;
    }

    public void setNumOfTrips(int numOfTrips) {
        this.numOfTrips = numOfTrips;
    }

    @Override
    public String toString() {
        return "User{" +
                "user_name='" + user_name + '\'' +
                ", email='" + email + '\'' +
                ", user_id='" + user_id + '\'' +
                ", gender='" + gender + '\'' +
                ", profile_image='" + profile_image + '\'' +
                ", following=" + following +
                ", follower=" + follower +
                ", phone_number='" + phone_number + '\'' +
                ", numOfphotos=" + numOfphotos +
                ", numOfTrips=" + numOfTrips +
                '}';
    }
}
