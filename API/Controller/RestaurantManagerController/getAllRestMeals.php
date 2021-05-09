<?php
include "MealInitializer.php";
include "../../Models/Menu.php";
include "../../Models/Category.php";
header("Access-Control-Allow-Methods: GET");
$meal = MealInitializer::getMeal();
$data = MealInitializer::getData();
$menu = new Menu(MealInitializer::getConn());
$cat = new Category(MealInitializer::getConn());
$server_ip = getHostByName(getHostName());
try {
    $mealArr = array();
    $mealArr['data'] = array();
    //Todo get menu_id
    $menu_id = $menu->getMenus($data->rest_id)->fetch(PDO::FETCH_ASSOC)['id'];
    //Todo get Categories
    $result = $cat->getCategories($menu_id);
    //Todo getAll meals
    if($result->rowCount()>0){
        while ($cat_row = $result->fetch(PDO::FETCH_ASSOC)){
            $meal_result = $meal->getAllMeals($cat_row['id']);
            $cat_name = $cat_row['name'];
            if ($meal_result->rowCount() > 0) {
                while ($row = $meal_result->fetch(PDO::FETCH_ASSOC)) {
                    $meal_item = array(
                        "meal_id" => $row['id'],
                        "meal_name" => $row['name'],
                        "calories" => $row['calories'],
                        "price" => $row['price'],
                        "Image" => $meal->getDir() . $row['img1'],
                        "category_name" => $cat_name,
                        "category_Id" => $cat_row['id'],
                    );
                    array_push($mealArr['data'], $meal_item);
                }
            }else {
                $meal_item = "no_data_found!";
                array_push($mealArr['data'], $meal_item);
            }
        }
        echo json_encode(array(
            "Meals_info" => $mealArr,
            "flag" => 1
        ));
    }else{
        http_response_code(404);
        // tell the user login failed
        echo json_encode(array(
            "Meals_info" => "No data found.",
            "flag" => -1
        ));
    }
} catch (Exception $e) {
    http_response_code(400);
    echo json_encode(array(
        "message" => "Error: " . $e->getMessage(),
        "flag" => 0
    ));
}
