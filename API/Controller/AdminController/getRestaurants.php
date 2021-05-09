<?php
$server_ip = getHostByName(getHostName());
require_once "../RestaurantController/RestaurantInitializer.php";
$rest = RestaurantInitializer::getRestaurant();
$data = RestaurantInitializer::getData();
try {
    $result = $rest->getResturantsByAdminId($data->email);
    $num = $result->rowCount();
    if ($num > 0) {
        $restaurantArr = array();
        $restaurantArr['data' ] = array();
        while ($row = $result->fetch(PDO::FETCH_ASSOC)) {
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
                "manager_img" => $row['managerimg']
            );
            array_push($restaurantArr['data'], $restaurant_item);
        }
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
