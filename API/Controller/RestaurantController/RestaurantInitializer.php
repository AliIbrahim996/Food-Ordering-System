<?php
require_once "../../config/headers.php";
include_once "../../config/Database.php";
require_once "../DataManagement.php";
include "../../Models/Restaurant.php";

class RestaurantInitializer
{


    public static function getDatabase()
    {
        return new Database();
    }

    public static function getConn()
    {
        return self::getDatabase()->connect();
    }


    public static function getRestaurant(): Restaurant
    {

        return new Restaurant(self::getConn());
    }

    public static function getData()
    {

        return json_decode(file_get_contents("php://input"));
    }


}