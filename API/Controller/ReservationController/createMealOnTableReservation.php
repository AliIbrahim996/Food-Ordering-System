<?php
require_once "MealReservationInitializer.php";
header("Access-Control-Allow-Methods: POST");
if (DataManagement::checkMealReservationEmptyData(MealReservationInitializer::getData())) {
    $mealReservation = MealReservationInitializer::getMealReservation();
    echo $mealReservation->createMealOnReservation(MealReservationInitializer::getData());
} else {
    http_response_code(404);
    echo json_encode(array(
        "message" => "error: check your data! ",
        "flag" => 0
    ));

}

