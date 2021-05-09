<?php

include "MealInitializer.php";
header("Access-Control-Allow-Methods: POST");
$data = MealInitializer::getData();
$meal = MealInitializer::getMeal();
if (DataManagement::checkMealEmptyData($data)) {
    $addMealFlag = $meal->addMeal($data);
    if ($addMealFlag) {
        http_response_code(201);
        echo json_encode(array(
            "message" => "Meal created successfully",
            "flag" => 1
        ));
    } else {
        http_response_code(401);
        echo json_encode(array(
            "message" => "Error : " . $addMealFlag,
            "flag" => -1
        ));
    }
} else {
    http_response_code(403);
    echo json_encode(array(
        "message" => "Check your data!",
        "flag" => 0
    ));
}
