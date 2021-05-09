<?php


class DataManagement
{
    public static function checkMealEmptyData($data): bool
    {
        if (!empty($data->meal_name) && !empty($data->calories) && !empty($data->category_id)
            && !empty($data->price) && !empty($data->meal_image)) {
            return true;
        } else {
            return false;
        }
    }

    public static function checkTableEmptyData($data): bool
    {
        if (!empty($data->restaurant_id) && !empty($data->table_number) &&
            !empty($data->table_status) && !empty($data->table_number_seats) &&
            !empty($data->table_location)) {
            return true;
        } else {
            return false;
        }
    }

    public static function checkCustomerEmptyData($data): bool
    {
        if (!empty($data->email) && !empty($data->password) &&
            !empty($data->address) && !empty($data->phoneNum) &&
            !empty($data->image_name) && !empty($data->full_name) && !empty($data->gender)
        ) {
            return true;
        } else {
            return false;
        }
    }

    public static function checkAdminEmptyData($data): bool
    {
        if (!empty($data->email) && !empty($data->password) &&
            !empty($data->age) && !empty($data->image_name)
            && !empty($data->full_name) && !empty($data->gender)) {
            return true;
        } else {
            return false;
        }
    }

    public static function checkManagerEmptyData($data): bool
    {
        if (!empty($data->manager_name) && !empty($data->manager_email) &&
            !empty($data->password) && !empty($data->manager_img)) {
            return true;
        } else {
            return false;
        }
    }

    public static function checkTableReservationEmptyData($data): bool
    {
        if (!empty($data->restaurant_id) && !empty($data->restaurent_tables_id) &&
            !empty($data->customer_id)) {
            return true;
        } else {
            return false;
        }
    }

    public static function checkMealReservationEmptyData($data): bool
    {
        if (!empty($data->restaurant_id) && !empty($data->meal_id) &&
            !empty($data->customer_id) && !empty($data->data_time_booking) && !empty($data->order_status) &&
            !empty($data->quantity)) {
            return true;
        } else {
            return false;
        }
    }

    public static function checkRatingEmptyData($data): bool
    {
        if (!empty($data->customer_id) && !empty($data->restaurant_id) &&
            !empty($data->customer_rate)) {
            return true;
        } else {
            return false;
        }
    }

    public static function checkCategoryEmptyData($data): bool
    {
        if (!empty($data->name) && !empty($data->cat_image)) {
            return true;
        } else {
            return false;
        }
    }

    public static function checkRestauranEmptyData($data)
    {
        if (!empty($data->restaurant_name) && !empty($data->restaurant_address) &&
            !empty($data->restaurant_phone_no) && !empty($data->admin_id)  && !empty($data->restaurent_img) &&
            !empty($data->open_at) && !empty($data->close_at)) {
            return true;
        } else {
            return false;
        }
    }

    public static function checkRestauranUEmptyData($data)
    {
        if (!empty($data->restaurant_name) &&
            !empty($data->restaurant_address) &&
            !empty($data->restaurant_phone_no) &&
            !empty($data->id)  &&
            !empty($data->restaurent_img) &&
            !empty($data->open_at)
            && !empty($data->close_at)) {
            return true;
        } else {
            return false;
        }
    }

}