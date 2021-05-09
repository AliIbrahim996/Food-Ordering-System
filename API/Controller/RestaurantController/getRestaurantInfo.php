<?php

require_once  "../../config/Database.php";
require_once "../../config/headers.php";
header("Access-Control-Allow-Methods: POST");
include "../../Models/Restaurant.php";

$database =new Database();
$db= $database->connect();
$rest =new Restaurant($db);
$data = json_decode(file_get_contents("php://input"));
if(!empty($data->rest_name)){
   echo $rest->getRestaurantInfo($data->rest_name);
}