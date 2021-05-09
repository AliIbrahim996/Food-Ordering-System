<?php
require_once "../../config/Database.php";
require_once "../../config/headers.php";
header("Access-Control-Allow-Methods: POST");
include "../../Models/Menu.php";
$database = new Database();
$conn = $database->connect();
$menu = new Menu($conn);
$data = json_decode(file_get_contents("php://input"));
if (!empty($data->restaurant_id)) {
    $addMenuFlag = $menu->registerMeal($data);
    if ($addMenuFlag == 1) {
        http_response_code(201);
        echo json_encode(array(
            "message" => "Menu added successfully!",
            "flag" => 1
        ));
    } else {
        http_response_code(403);
        echo json_encode(array(
            "message : " => "Error : " . $addMenuFlag,
            "flag" => -1
        ));
    }
} else {
    http_response_code(401);
    echo json_encode(array(
        "message : " => "Error : Check your data! ",
        "flag" => 0
    ));
}