<?php

require_once "RestaurantInitializer.php";
$rest = RestaurantInitializer::getRestaurant();
$data = RestaurantInitializer::getData();

if (DataManagement::checkRestauranEmptyData($data)) {
    $flag = $rest->createRestaurant($data);
    if ($flag == 1) {
        http_response_code(201);
        echo json_encode(array(
            "message" => "Restaurant registered successful",
            "flag" => 1
        ));
    } else {
        http_response_code(400);
        echo json_encode(array(
            "message" => "error: " . $flag,
            "flag" => -1
        ));
    }
} else {
    http_response_code(403);
    echo json_encode(array(
        "message" => "error: check your data",
        "flag" => 0
    ));
}
