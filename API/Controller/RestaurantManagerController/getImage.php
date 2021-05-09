<?php
require_once "../RestaurantController/RestaurantInitializer.php";
$rest = RestaurantInitializer::getRestaurant();
$data = RestaurantInitializer::getData();

if(!empty($data->email)){
    echo $rest->getManagerImage($data->email);
}
else{
    http_response_code(402);
    echo json_encode(
        array("error" => "check your data!")
    );
}