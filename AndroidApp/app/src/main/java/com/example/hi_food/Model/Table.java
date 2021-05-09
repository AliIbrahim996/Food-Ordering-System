package com.example.hi_food.Model;

public class Table {

    private int numberOfSeats;
    private String location;
    private int tableNumber;
    private String tableStatus;
    private String id;
    private String rest_id;
    private String imageURL;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRest_id() {
        return rest_id;
    }

    public void setRest_id(String rest_id) {
        this.rest_id = rest_id;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public Table(int numberOfSeats, String location, int tableNumber) {
        this.numberOfSeats = numberOfSeats;
        this.location = location;
        this.tableNumber = tableNumber;
    }

    public String getTableStatus() {
        return tableStatus;
    }

    public void setTableStatus(String tableStatus) {
        this.tableStatus = tableStatus;
    }

    public void setNumberOfSeats(int numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public String getNumberOfSeats() {
        return "" + numberOfSeats;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTableNumber() {
        return tableNumber + "";
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }
}

