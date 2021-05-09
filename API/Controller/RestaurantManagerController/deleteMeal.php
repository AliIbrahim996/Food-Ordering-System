<?php
include "MealInitializer.php";
header("Access-Control-Allow-Methods: DELETE");

$data = MealInitializer::getData();
$meal = MealInitializer::getMeal();
if(!empty($data->meal_id)){
    $deleteMealFlag = $meal->deleteMeal($data->meal_id);
    if($deleteMealFlag){
        http_response_code(200);
        echo json_encode(array(
            "message" => "Meal Deleted successful",
            "flag" => 1
        ));
    } else {
        http_response_code(400);
        echo json_encode(array(
            "message" => "error: " . $deleteMealFlag,
            "flag" => -1
        ));
    }
} else {
    http_response_code(204);
    echo json_encode(array(
        "message" => "error: Check your data! ",
        "flag" => 0
    ));
}