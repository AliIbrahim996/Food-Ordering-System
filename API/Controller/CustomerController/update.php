<?php
include "../../config/Database.php";
include_once "../../config/headers.php";
header("Access-Control-Allow-Methods: PUT");
include "Models/Customer.php";

$database =new Database();
$db= $database->connect();
$c = new Customer($db);
$data = json_decode(file_get_contents("php://input"));

if(!empty($data->email) && !empty($data->password)&&
    !empty($data->address) && !empty($data->phoneNum)&&
    !empty($data->imgSrc) && !empty($data->full_name)) {
    $flag = $c->updateCustomer($data);
    if($flag){
        http_response_code(201);
        echo json_encode(array(
            "Message" => "Customer updated successful",
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
