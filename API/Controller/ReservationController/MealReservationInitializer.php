<?php

require_once "../../config/headers.php";
include_once "../../config/Database.php";
include "../../Models/MealReservation.php";
require_once "../../Controller/DataManagement.php";

class MealReservationInitializer
{

    public static function getDatabase()
    {
        return new Database();
    }


    public static function getConn()
    {
        return self::getDatabase()->connect();
    }


    public static function getData()
    {
        return json_decode(file_get_contents("php://input"));
    }


    public static function getMealReservation(): MealReservation
    {
        return new MealReservation(self::getConn());
    }

}