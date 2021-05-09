<?php
require_once "CategoryInitializer.php";
header("Access-Control-Allow-Methods: GET");
include "../../Models/Menu.php";
$data = CategoryInitializer::getData();
$cat = CategoryInitializer::getCategory();
$m = new Menu(CategoryInitializer::getConn());
if (!empty($data->email)) {
    $menu = json_decode($m->getLastMenu($data->email));
    if ($menu->flag == 1) {
        $menu_id = $menu->id;
        $result = $cat->getCategories($menu_id);
        $num = $result->rowCount();
        if ($num > 0) {
            $CategoryArr = array();
            $CategoryArr['data'] = array();
            while ($row = $result->fetch(PDO::FETCH_ASSOC)) {
                $Category_item = array(
                    'cat_id' => $row['id'],
                    'cat_name' => $row['name'],
                    'cat_imgUrl' => $cat->getDir() . $row['cat_img'],
                    'menu_id' => $row['menu_id']
                );
                array_push($CategoryArr['data'], $Category_item);
            }
            echo json_encode(array(
                "Categories_Info" => $CategoryArr,
                "flag" => 1
            ));
        } else {
            http_response_code(404);
            // tell the user login failed
            echo json_encode(array(
                "message" => "No data found.",
                "flag" => -1
            ));
        }
    }



} else {
    http_response_code(404);
    // tell the user login failed
    echo json_encode(array(
            "message" => "No menu selected!.",
            "flag" => 0
        )
    );
}
