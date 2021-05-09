package com.example.hi_food.Model;

import java.io.Serializable;

public class Order implements Serializable {

    String meal_id;
    private String mealName;
    private int qty;
    private double price;

    public Order(String meal_id, int qty, double price) {
        this.meal_id = meal_id;
        this.qty = qty;
        this.price = price;
    }

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getMeal_id() {
        System.out.println(meal_id);
        return meal_id;
    }

    public void setMeal_id(String meal_id) {
        this.meal_id = meal_id;
    }
}
