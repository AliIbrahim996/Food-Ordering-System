package com.example.hi_food.Model;

import org.json.JSONObject;

public class CustomerTableBooking {
    /**
     * "id": "24",
     * "restaurant_tables_id": "8",
     * "restaurant_id": "ad@gmail.com",
     * "customer_id": "ali@test.com",
     * "reservation_info": {
     * "number_of_seats": "5",
     * "occasion": "eid",
     * "number_of_balloons": "15",
     * "number_of_flowers": "55",
     * "number_of_persons": "5",
     * "other_detail's": "no"
     * },
     * "table_number": "44",
     * "t_status": "available"
     * }
     */
    private String id;
    private String restaurant_tables_id;
    private String restaurant_id;
    private String restaurant_name;
    private String customer_id;
    private String customer_name;
    private JSONObject reservation_info;
    private String table_number;
    private String t_status;

    public String getId() {
        return id;
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

    public String getRestaurant_tables_id() {
        return restaurant_tables_id;
    }

    public String getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(String restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public void setRestaurant_tables_id(String restaurant_tables_id) {
        this.restaurant_tables_id = restaurant_tables_id;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public JSONObject getReservation_info() {
        return reservation_info;
    }

    public void setReservation_info(JSONObject reservation_info) {
        this.reservation_info = reservation_info;
    }

    public String getTable_number() {
        return table_number;
    }

    public void setTable_number(String table_number) {
        this.table_number = table_number;
    }

    public String getT_status() {
        return t_status;
    }

    public void setT_status(String t_status) {
        this.t_status = t_status;
    }
}
