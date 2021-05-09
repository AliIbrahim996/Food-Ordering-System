<?php
/*
include "../../config/headers.php";
include_once "../../config/Database.php";
header("Access-Control-Allow-Methods: POST");
include "../../Models/Admin.php";
$database = new Database();
$db = $database->connect();
$admin = new Admin($db);
$data = json_decode(file_get_contents("php://input"));*/
require_once "AdminInitializer.php";
header("Access-Control-Allow-Methods: POST");
$data = AdminInitializer::getData();
$admin = AdminInitializer::getAdmin();
if (!empty($data->email) && !empty($data->password)) {
    $email = $data->email;
    $password = $data->password;
    echo $admin->adminLogIn($email, $password);
} else {
    http_response_code(404);
    echo json_encode(array(
        "Message" => "check email and password!",
        "flag" => 0
    ));
}

