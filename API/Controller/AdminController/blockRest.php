<?php
require_once  "../RestaurantController/RestaurantInitializer.php";

$data = RestaurantInitializer::getData();
$rest = RestaurantInitializer::getRestaurant();

if(!empty($data->rest_id)){
    echo $rest->updateStatus($data);
}
else{
    http_response_code(404);
    echo json_encode(
        array("message" => "check your data!","flag" => 0)
    );
}