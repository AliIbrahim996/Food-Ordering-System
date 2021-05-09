<?php
include "../../config/Database.php";
include_once "../../config/headers.php";
header("Access-Control-Allow-Methods: DELETE");
include "Models/Customer.php";

$database =new Database();
$db= $database->connect();
$c = new Customer($db);
$data = json_decode(file_get_contents("php://input"));

if(!empty($data->email)) {
    $flag = $c->delete($data->email);
    if($flag){
        http_response_code(200);
        echo json_encode(array(
            "Message" => "Customer Deleted successful",
            "flag" => 1
        ));
    }
    else{
        http_response_code(400);
        echo json_encode(array(
            "message" => "error: ".$flag
        ));
    }
}
