package com.example.hi_food.Model;

public class Meal {
    private String id;
    private String name;
    private double calories;
    private double price;
    private Category cat;
    private String cat_id;
    private String cat_name;

    public String getCat_name() {
        return cat_name;
    }

    public void setCat_name(String cat_name) {
        this.cat_name = cat_name;
    }

    private String imageURL;

    public Meal(String name, double calories, double price) {
        this.name = name;
        this.calories = calories;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCalories() {
        return "" + calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public String getPrice() {
        return "" + price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Category getCat() {
        return cat;
    }

    public void setCat(Category cat) {
        this.cat = cat;
    }
}
