<?php
require_once "../RestaurantController/RestaurantInitializer.php";
header("Access-Control-Allow-Methods: POST");
$data = RestaurantInitializer::getData();
if (!empty($data->manager_email) && !empty($data->password)) {
    $email = $data->manager_email;
    $password = $data->password;
    echo RestaurantInitializer::getRestaurant()
        ->managerLogIn($email, $password);
} else {
    http_response_code(404);
    echo json_encode(array(
        "Message" => "check email and password!",
        "flag" => 0
    ));
}

