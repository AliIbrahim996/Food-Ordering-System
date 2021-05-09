<?php

require_once  "../../config/Database.php";
require_once "../../config/headers.php";
header("Access-Control-Allow-Methods: POST");
include "../../Models/Restaurant.php";

$database =new Database();
$db= $database->connect();
$rest = new Restaurant($db);

$data = json_decode(file_get_contents("php://input"));

if(!empty($data->email)) {
    $flag = $rest->delete($data->email);
    if($flag){
        http_response_code(200);
        echo json_encode(array(
            "Message" => "Restaurant Deleted successful",
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
else{
    http_response_code(404);
    echo json_encode(array(
        "message" => "Check your email!",
        "flag" => 0
    ));
}
