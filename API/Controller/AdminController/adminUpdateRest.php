<?php
require_once "../RestaurantController/RestaurantInitializer.php";
header("Access-Control-Allow-Methods: PUT");
require_once "../RestaurantController/RestaurantInitializer.php";

$data = RestaurantInitializer::getData();
$rest = RestaurantInitializer::getRestaurant();
if (!empty(DataManagement::checkRestauranUEmptyData($data))) {
    $flag = $rest->updateRestaurant($data);
    if ($flag == 1) {
        http_response_code(200);
        echo json_encode(array("message" => "updated succefully", "flag" => 1));
    }else {

        http_response_code(400);
        echo json_encode(array("message" => "something went wrong"
            . $flag, "flag" => -1));
    }
} else {
    http_response_code(404);
    echo json_encode(array("message" => "check your data", "flag" => 0));
}