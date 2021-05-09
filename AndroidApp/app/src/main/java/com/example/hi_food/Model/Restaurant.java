package com.example.hi_food.Model;

import java.io.Serializable;

public class Restaurant implements Serializable {
    private String r_name;
    private String r_location;
    private String r_phoneNum;
    private String openAt, closeAt;
    private String imageURL;
    private String status;
    private double rate;
    private String id;
    private String manager_name;
    private String manger_email;
    private String mgrName;
    private String is_available;

    public String getMgrName() {
        return mgrName;
    }


    public void setMgrName(String mgrName) {
        this.mgrName = mgrName;
    }

    public Restaurant(String r_name, String r_location, String r_phoneNum, String openAt, String closeAt) {
        this.r_name = r_name;
        this.r_location = r_location;
        this.r_phoneNum = r_phoneNum;
        this.openAt = openAt;
        this.closeAt = closeAt;
    }

    public Restaurant(String r_name, String r_location, String r_phoneNum, String openAt, String closeAt,
                      String imageURL, String status, String id) {
        this.r_name = r_name;
        this.r_location = r_location;
        this.r_phoneNum = r_phoneNum;
        this.openAt = openAt;
        this.closeAt = closeAt;
        this.imageURL = imageURL;
        this.status = status;
        this.id = id;
    }

    public Restaurant(String r_name, String r_location,
                      String r_phoneNum, String openAt,
                      String closeAt, String imageURL,
                      String status, double rate, String id,
                      String manager_name, String manger_email) {
        this.r_name = r_name;
        this.r_location = r_location;
        this.r_phoneNum = r_phoneNum;
        this.openAt = openAt;
        this.closeAt = closeAt;
        this.imageURL = imageURL;
        this.status = status;
        this.rate = rate;
        this.id = id;
        this.manager_name = manager_name;
        this.manger_email = manger_email;
    }

    public String getManager_name() {
        return manager_name;
    }

    public void setManager_name(String manager_name) {
        this.manager_name = manager_name;
    }

    public String getManger_email() {
        return manger_email;
    }

    public void setManger_email(String manger_email) {
        this.manger_email = manger_email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRate() {
        return "" + Math.rint(rate);
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getR_name() {
        return r_name;
    }

    public void setR_name(String r_name) {
        this.r_name = r_name;
    }

    public String getR_location() {
        return r_location;
    }

    public void setR_location(String r_location) {
        this.r_location = r_location;
    }

    public String getR_phoneNum() {
        return r_phoneNum;
    }

    public void setR_phoneNum(String r_phoneNum) {
        this.r_phoneNum = r_phoneNum;
    }

    public String getOpenAt() {
        return openAt;
    }

    public void setOpenAt(String openAt) {
        this.openAt = openAt;
    }

    public String getCloseAt() {
        return this.closeAt;
    }

    public void setCloseAt(String closeAt) {
        this.closeAt = closeAt;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
