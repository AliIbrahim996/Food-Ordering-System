<?php
require_once "../../config/headers.php";
include_once "../../config/Database.php";
require_once "../DataManagement.php";
include "../../Models/Meal.php";

class MealInitializer
{

    public static function getDatabase(): Database
    {
        return new Database();
    }

    public static function getConn()
    {
        return self::getDatabase()->connect();
    }

    public static function getMeal(): Meal
    {

        return new Meal(self::getConn());
    }

    public static function getData()
    {

        return json_decode(file_get_contents("php://input"));
    }


}