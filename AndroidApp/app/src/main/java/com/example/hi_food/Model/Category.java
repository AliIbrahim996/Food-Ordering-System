package com.example.hi_food.Model;

public class Category {
    private String name;
    private String id;
    private String imageURL;
    int menu_id;

    public Category(String name, String id, String imageURL, int menu_id) {
        this.name = name;
        this.id = id;
        this.imageURL = imageURL;
        this.menu_id = menu_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getMenu_id() {
        return menu_id;
    }

    public void setMenu_id(int menu_id) {
        this.menu_id = menu_id;
    }
}

