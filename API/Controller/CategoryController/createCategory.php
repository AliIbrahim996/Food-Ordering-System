<?php
require_once "CategoryInitializer.php";
header("Access-Control-Allow-Methods: POST");
include "../../Models/Menu.php";
$data = CategoryInitializer::getData();
$m = new Menu(CategoryInitializer::getConn());
if (DataManagement::checkCategoryEmptyData($data)) {
    $menu = json_decode($m->getLastMenu($data->email));
    if ($menu->flag == 1) {
        $menu_id = $menu->id;
        $flag = CategoryInitializer::getCategory()->createCategory($data, $menu_id);
        if ($flag == 1) {
            http_response_code(201);
            echo json_encode(array(
                "message" => "Category created successful",
                "flag" => 1
            ));

        } else {
            http_response_code(400);
            echo json_encode(array(
                "message" => "error: " . $flag,
                "flag" => -1

            ));
        }
    }
    else{
        http_response_code(404);
        echo json_encode(array(
            "message : " => "No menu found! " . $menu->error,
            "flag" => -2
        ));
    }
} else {
    http_response_code(401);
    echo json_encode(array(
        "message : " => "Error : Check your data! ",
        "flag" => 0
    ));
}
