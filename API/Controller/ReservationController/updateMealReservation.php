<?php
require_once "../RestaurantManagerController/MealReservationInitializer.php";
header("Access-Control-Allow-Methods: PUT");
$data = MealReservationInitializer::getData();
if (DataManagement::checkMealReservationEmptyData($data) && !empty($data->order_id)) {
    $order_status = MealReservationInitializer::getMealReservation()->getOrderStatus($data->order_id);
    if ($order_status != "accepted" || $order_status != "rejected")
    {
        echo MealReservationInitializer::getMealReservation()->updateMealReservation($data);

    } else {
        http_response_code(403);
        echo json_encode(array(
            "message" => "Your reservation status has been set to " . $order_status,
            "flag" => -2
        ));
    }
}
else{
    http_response_code(401);
    echo json_encode(array(
        "message" => "Something went wrong! Check your data!",
        "flag" => 0
    ));
}