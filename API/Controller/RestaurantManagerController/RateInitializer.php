<?php
require_once "../../config/headers.php";
include_once "../../config/Database.php";
include "../../Models/Rate.php";
include "../DataManagement.php";

class RateInitializer
{


    /**
     * initialize constructor.
     */
    public function __construct()
    {

    }

    /**
     * @return mixed
     */
    public static function getDatabase(): Database
    {
        return new Database();
    }


    /**
     * @return mixed
     */
    public static function getConn()
    {
        return self::getDatabase()->connect();
    }


    /**
     * @return mixed
     */
    public static function getRate(): Rate
    {

        return new Rate(self::getConn());
    }

    /**
     * @return mixed
     */
    public static function getData()
    {

        return json_decode(file_get_contents("php://input"));
    }


}