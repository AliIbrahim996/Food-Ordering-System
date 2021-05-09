<?php
require_once "../../config/headers.php";
include_once "../../config/Database.php";
require_once "../../Controller/DataManagement.php";
include "../../Models/Admin.php";

class AdminInitializer
{


    public static function getDatabase()
    {
        return new Database();
    }

    public static function getConn()
    {
        return self::getDatabase()->connect();
    }


    public static function getAdmin()
    {

        return new Admin(self::getConn());
    }

    public static function getData()
    {

        return json_decode(file_get_contents("php://input"));
    }


}