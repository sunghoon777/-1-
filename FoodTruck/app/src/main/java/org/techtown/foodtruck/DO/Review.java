package org.techtown.foodtruck.DO;

import java.util.ArrayList;

public class Review {

    private String userIdToken;
    private String date;
    private String comment;
    private String image;
    private String rate;
    private String userName;
    private String truckId;


    public Review() {
    }

    public Review(String user_idToken, String date, String comment, String image, String rate, String userName,String truckId) {
        this.userIdToken = user_idToken;
        this.date = date;
        this.comment = comment;
        this.image = image;
        this.rate = rate;
        this.userName = userName;
        this.truckId = truckId;
    }

    public String getUserIdToken() {
        return userIdToken;
    }

    public void setUserIdToken(String user_idToken) {
        this.userIdToken = user_idToken;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTruckId() {
        return truckId;
    }

    public void setTruckId(String truckId) {
        this.truckId = truckId;
    }
}
