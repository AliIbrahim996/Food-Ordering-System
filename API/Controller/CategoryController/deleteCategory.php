<?php
require_once "CategoryInitializer.php";
header("Access-Control-Allow-Methods: DELETE");
$data = CategoryInitializer::getData();
if (!empty($data->id)) {
    $flag = CategoryInitializer::getCategory()->delete($data->id);
    if ($flag) {
        http_response_code(200);
        echo json_encode(array(
            "Message" => "Category Deleted successful",
            "flag" => 1
        ));
    } else {
        http_response_code(400);
        echo json_encode(array(
            "message" => "error: " . $flag,
            "flag" => -1
        ));
    }
} else {
    http_response_code(403);
    echo json_encode(array(
        "message" => "error: Check your data! ",
        "flag" => 0
    ));
}
