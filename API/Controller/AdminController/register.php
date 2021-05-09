<?php
/*
include "../../config/headers.php";
header("Access-Control-Allow-Methods: POST");
include_once "../../config/Database.php";
include "../../Models/Admin.php";

$database = new Database();
$db = $database->connect();
$admin = new Admin($db);
$data = json_decode(file_get_contents("php://input"));
*/
require_once "AdminInitializer.php";
header("Access-Control-Allow-Methods: POST");
$data = AdminInitializer::getData();
$admin = AdminInitializer::getAdmin();
if (DataManagement::checkAdminEmptyData($data)) {
    echo $admin->registerAdmin($data);

} else {
    http_response_code(400);
    echo json_encode(array(
        "message" => "check your data",
        "flag" => 0
    ));
}
