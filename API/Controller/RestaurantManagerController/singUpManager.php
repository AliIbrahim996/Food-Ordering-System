<?php
require_once "../RestaurantController/RestaurantInitializer.php";
header("Access-Control-Allow-Methods: POST");
$data = RestaurantInitializer::getData();

if (DataManagement::checkManagerEmptyData($data)) {
    $flag = RestaurantInitializer::getRestaurant()->singupManager($data);
    if ($flag == 1) {
        http_response_code(201);
        echo json_encode(array(
            "message" => "Manager registered successful",
            "flag" => 1
        ));
    }else {
        http_response_code(401);
        echo json_encode(array(
            "message" => "error: " . $flag
        ));
    }
} else {
    http_response_code(400);
    echo json_encode(array(
        "message" => "check Your Data!",
        "flag" => 0
    ));
}
