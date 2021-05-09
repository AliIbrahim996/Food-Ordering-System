<?php
require_once "../../config/headers.php";
require_once "../../config/Database.php";
header("Access-Control-Allow-Methods: GET");
include "../../Models/Admin.php";

$database = new Database();
$db = $database->connect();
$admin = new Admin($db);
$data = json_decode(file_get_contents("php://input"));
if (!empty($data->email)) {
    echo $admin->getAdminInfo($data->email);
} else {
    http_response_code(404);
    echo json_encode(array(
        "message" => "Check your email!.",
        "flag" => 0
    ));
}

