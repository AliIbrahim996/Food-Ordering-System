<?php
include "MealInitializer.php";
header("Access-Control-Allow-Methods: GET");

$meal = MealInitializer::getMeal();
$data = MealInitializer::getData();
$server_ip = getHostByName(getHostName());
try {
    $result = $meal->getAllMeals($data->category_id);
    $num = $result->rowCount();
    if ($num > 0) {
        $mealArr = array();
        $mealArr['data'] = array();
        while ($row = $result->fetch(PDO::FETCH_ASSOC)) {
            $meal_item = array(
                "meal_id" => $row['id'],
                "meal_name" => $row['name'],
                "calories" => $row['calories'],
                "price" => $row['price'],
                "Image" => $meal->getDir() . $row['img1'],
                "category_id" => $row['category_id']
            );
            array_push($mealArr['data'], $meal_item);
        }
        echo json_encode(array(
            "Meals_info" => $mealArr,
            "flag" => 1
        ));
    } else {
        http_response_code(404);
        // tell the user login failed
        echo json_encode(array(
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
