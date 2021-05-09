package com.example.hi_food.Model;

import java.io.Serializable;

public class Customer implements Serializable {
    private String c_name;
    private  String gender;
    private String c_phoneNum;
    private  String email, address;
    private int c_id;
    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Customer(String r_name, String gender,
                    String c_phoneNum,
                    String email,
                    String address) {
        this.c_name = r_name;
        this.gender = gender;
        this.c_phoneNum = c_phoneNum;
        this.email = email;
        this.address = address;

    }

    public Customer(){

    }

    public int getC_id() {
        return c_id;
    }

    public void setC_id(int c_id) {
        this.c_id = c_id;
    }

    public String getC_name() {
        return c_name;
    }

    public void setC_name(String c_name) {
        this.c_name = c_name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getC_phoneNum() {
        return c_phoneNum;
    }

    public void setC_phoneNum(String c_phoneNum) {
        this.c_phoneNum = c_phoneNum;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
