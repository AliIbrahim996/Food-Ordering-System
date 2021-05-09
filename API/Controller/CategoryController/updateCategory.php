<?php
require_once "CategoryInitializer.php";
$data = CategoryInitializer::getData();
header("Access-Control-Allow-Methods: PUT");

if(!empty($data->id)&& DataManagement::checkCategoryEmptyData($data)) {
    $flag = CategoryInitializer::getCategory()->updateCategory($data);
    if($flag){
        http_response_code(201);
        echo json_encode(array(
            "Message" => "Category updated successful",
            "flag" => 1
        ));
    }
    else{
        http_response_code(400);
        echo json_encode(array(
            "message" => "error: ".$flag,
            "flag" => -1
        ));
    }
}
else {
    echo json_encode(array(
        "Message" => "Failed to Update! Check your data",
        "flag" => 0
    ));
}
