<?php
include "MealInitializer.php";
header("Access-Control-Allow-Methods: PUT");

$data = MealInitializer::getData();
$meal = MealInitializer::getMeal();

if (DataManagement::checkMealEmptyData($data) && !empty($data->meal_id)) {
    $updateMealFlag = $meal->updateMeal($data);
    if ($updateMealFlag) {
        http_response_code(201);
        echo json_encode(array(
            "message" => "Meal updated successfully",
            "flag" => 1
        ));
    } else {
        http_response_code(401);
        echo json_encode(array(
            "message" => "Error : " . $updateMealFlag,
            "flag" => -1
        ));
    }
} else {
    http_response_code(204);
    echo json_encode(array(
        "message" => "Check your data!",
        "flag" => 0
    ));
}