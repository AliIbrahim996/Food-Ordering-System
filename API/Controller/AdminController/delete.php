<?php
require_once "config/Database.php";
require_once "headers.php";
header("Access-Control-Allow-Methods: DELETE");
include "Models/Admin.php";

$database = new Database();
$db = $database->connect();
$admin = new Admin($db);
$data = json_decode(file_get_contents("php://input"));

if (!empty($data->email)) {
    $flag = $admin->delete($data->email);
    if ($flag) {
        http_response_code(200);
        echo json_encode(array(
            "Message" => "Admin Deleted successful",
            "flag" => 1
        ));
    } else {
        http_response_code(400);
        echo json_encode(array(
            "message" => "error: " . $flag
        ));
    }
}
