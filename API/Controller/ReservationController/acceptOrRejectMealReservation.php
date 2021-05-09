<?php
require_once "MealReservationInitializer.php";
header("Access-Control-Allow-Methods: PUT");
$data = MealReservationInitializer::getData();

if (!empty($data->order_status) && $data->order_id) {
    $meal_reservation = MealReservationInitializer::getMealReservation();
    $flag = $meal_reservation->updateOrderStatus($data);
    if ($flag) {
        if ($data->order_status == "accepted") {
            http_response_code(201);
            echo json_encode(array(
                "message" => "Meal Reservation accepted!",
                "flag" => 1
            ));

        } else {
            http_response_code(201);
            echo json_encode(array(
                "message" => "Meal reservation rejected!",
                "flag" => -1
            ));
        }
    } else {
        http_response_code(403);
        echo json_encode(array(
            "message" => "error: " . $flag,
            "flag" => -2
        ));
    }
} else {
    http_response_code(204);
    echo json_encode(array(
        "message" => "Check if your data empty! ".$php_errormsg,
        "flag" => 0
    ));
}
