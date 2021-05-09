<?php

require_once "../../config/Database.php";
require_once "../../config/headers.php";
header("Access-Control-Allow-Methods: PUT");
include "../../Models/Restaurant.php";

$database = new Database();
$db = $database->connect();
$rest = new Restaurant($db);
$data = json_decode(file_get_contents("php://input"));
echo "hello";
echo json_encode($data);
if (!empty($data->restaurant_name) && !empty($data->restaurant_address)
    &&
    !empty($data->restaurant_phone_no) && !empty($data->id)
    && !empty($data->restaurent_img) &&
    !empty($data->open_at) && !empty($data->close_at)) {
    $flag = $rest->updateRestaurant($data);
    if ($flag == 1) {
        http_response_code(201);
        echo json_encode(array(
            "message" => "Restaurant updated successful",
            "flag" => 1
        ));
    } else {
        http_response_code(400);
        echo json_encode(array(
            "message" => "error: " . $flag
        ));
    }
} else {
    http_response_code(204);
    echo json_encode(array(
        "message" => "check your data!",
        "flag" => 0
    ));
}

