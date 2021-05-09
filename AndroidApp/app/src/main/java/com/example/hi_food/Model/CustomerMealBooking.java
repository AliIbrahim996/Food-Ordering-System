package com.example.hi_food.Model;

import org.json.JSONObject;

public class CustomerMealBooking {
    /**
     * "id": "70",
     * "restaurant_id": "ad@gmail.com",
     * "restaurant_name": "Hi-food",
     * "customer_id": "ali@test.com",
     * "customer_name": "Ali Ibrahim",
     * "meal_id": "6",
     * "meal_name": "rise",
     * "quantity": "2",
     * "unit_price": "60",
     * "order_status": "waiting for approval",
     * "data_time_booking": "13-02-2021 22:23:47",
     * "data_time_delivary": null,
     * "is_in_door": "1",
     * "table_info": "empty"
     */

    private String id;
    private String restaurant_name;
    private String restaurant_id;
    private String customer_id;
    private String table_id;
    private String customer_name;
    private String meal_id;
    private String meal_name;
    private String quantity;
    private String unit_price;
    private String order_status;
    private String data_time_booking;
    private String data_time_delivary;
    private String is_in_door;
    private JSONObject table_info;

    public String getId() {
        return id;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRestaurant_name() {
        return restaurant_name;
    }

    public void setRestaurant_name(String restaurant_name) {
        this.restaurant_name = restaurant_name;
    }

    public String getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(String restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getMeal_id() {
        return meal_id;
    }

    public void setMeal_id(String meal_id) {
        this.meal_id = meal_id;
    }

    public String getMeal_name() {
        return meal_name;
    }

    public void setMeal_name(String meal_name) {
        this.meal_name = meal_name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(String unit_price) {
        this.unit_price = unit_price;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public String getData_time_booking() {
        return data_time_booking;
    }

    public void setData_time_booking(String data_time_booking) {
        this.data_time_booking = data_time_booking;
    }

    public String getData_time_delivary() {
        return data_time_delivary;
    }

    public String getTable_id() {
        return table_id;
    }

    public void setTable_id(String table_id) {
        this.table_id = table_id;
    }

    public void setData_time_delivary(String data_time_delivary) {
        this.data_time_delivary = data_time_delivary;
    }

    public String getIs_in_door() {
        return is_in_door;
    }

    public void setIs_in_door(String is_in_door) {
        this.is_in_door = is_in_door;
    }

    public JSONObject getTable_info() {
        return table_info;
    }

    public void setTable_info(JSONObject table_info) {
        this.table_info = table_info;
    }
}
