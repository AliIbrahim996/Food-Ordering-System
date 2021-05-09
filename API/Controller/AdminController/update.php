<?php
/*
include "../../config/headers.php";
header("Access-Control-Allow-Methods: PUT");
include_once "../../config/Database.php";
include "../../Models/Admin.php";
$database = new Database();
$db = $database->connect();
$admin = new Admin($db);
$data = json_decode(file_get_contents("php://input"));*/
require_once "AdminInitializer.php";
header("Access-Control-Allow-Methods: PUT");
$data = AdminInitializer::getData();
$admin = AdminInitializer::getAdmin();
if (DataManagement::checkAdminEmptyData($data)) {
    $flag = $admin->updateAdmin($data);
    if ($flag == 1) {
        http_response_code(200);
        echo json_encode(array(
            "Message" => "Admin updated successful",
            "flag" => 1
        ));
    } else {
        http_response_code(400);
        echo json_encode(array(
            "message" => "error: " . $flag
        ));
    }
} else {
    http_response_code(400);
    echo json_encode(array(
        "message" => "check your data",
        "flag" => 0
    ));
}
