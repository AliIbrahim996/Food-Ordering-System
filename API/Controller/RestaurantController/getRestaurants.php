<?php
$server_ip = getHostByName(getHostName());
require_once "RestaurantInitializer.php";
include "../../Models/Rate.php";
$rest = RestaurantInitializer::getRestaurant();
$rate = new Rate(RestaurantInitializer::getConn());
try {
    $result = $rest->getRestaurants();
    $num = $result->rowCount();
    if ($num > 0) {
        $restaurantArr = array();
        $restaurantArr['data'] = array();
        while ($row = $result->fetch(PDO::FETCH_ASSOC)) {
            $r = $rate->getRestAvgRate($row['id']);
            $restaurant_item = array(
                'rest_id' => $row['id'],
                'restaurant_name' => $row['restaurant_name'],
                'restaurant_address' => $row['restaurant_address'],
                'restaurant_phone_no' => $row['restaurant_phone_no'],
                'restaurant_imgUrl' => $rest->getDir() . $row['restaurent_img'],
                'admin_id' => $row['admin_id'],
                'is_accepted' => $row['is_accepted'],
                'open_at' => $row['open_at'],
                'close_at' => $row['close_at'],
                "manager_name" => $row['manager_name'],
                "manger_email" => $row['manager_email'],
                "manger_password" => $row['manager_password'],
                "manager_img" => $row['managerimg'],
                "rate" => $r
            );
            array_push($restaurantArr['data'], $restaurant_item);
        }
        http_response_code(200);
        echo json_encode(array(
            "Restaurants_Info" => $restaurantArr,
            "flag" => 1
        ));
    } else {
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
