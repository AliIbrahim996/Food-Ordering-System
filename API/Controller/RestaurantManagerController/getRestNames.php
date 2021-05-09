<?php
require_once "../RestaurantController/RestaurantInitializer.php";
header("Access-Control-Allow-Methods: GET");
$rest = RestaurantInitializer::getRestaurant();
$data = RestaurantInitializer::getData();
try {
    $result = $rest->getRestaurantNames();
    $num = $result->rowCount();
    if ($num > 0) {
        $restaurantArr = array();
        $restaurantArr['data'] = array();
        while ($row = $result->fetch(PDO::FETCH_ASSOC)) {
            array_push($restaurantArr['data'], array(
                'restaurant_name' => $row['restaurant_name']));
        }
        http_response_code(200);
        echo json_encode(array(
            "Restaurants_Info" => $restaurantArr,
            "flag" => 1
        ));
    }
    else {
        http_response_code(404);
        echo json_encode(
            array(
                "message" => "No data found.",
                "flag" => 0
            ));
    }
} catch (Exception $e) {
    http_response_code(400);
    echo json_encode(array(
        "message" => "Error: " . $e->getMessage(),
        "flag" => -1
    ));
}