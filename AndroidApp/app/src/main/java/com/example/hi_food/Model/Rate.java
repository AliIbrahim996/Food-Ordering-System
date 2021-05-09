package com.example.hi_food.Model;

public class Rate {
    private int id;
    String c_id;
    private int customer_id;
    private String resturant_name;
    private int restaurant_id;
    private String feedBack;
    private int value;
    String c_name;


    public String getResturant_name() {
        return resturant_name;
    }

    public void setResturant_name(String resturant_name) {
        this.resturant_name = resturant_name;
    }

    public String getC_name() {
        return c_name;
    }

    public void setC_name(String c_name) {
        this.c_name = c_name;
    }


    public Rate(String customer_id, int restaurant_id, String feedBack, int value) {
        this.c_id = customer_id;
        this.restaurant_id = restaurant_id;
        this.feedBack = feedBack;
        this.value = value;
    }

    public Rate(int customer_id, int restaurant_id, String feedBack, int value) {
        this.customer_id = customer_id;
        this.restaurant_id = restaurant_id;
        this.feedBack = feedBack;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public int getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(int restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public String getFeedBack() {
        return feedBack;
    }

    public void setFeedBack(String feedBack) {
        this.feedBack = feedBack;
    }


}
