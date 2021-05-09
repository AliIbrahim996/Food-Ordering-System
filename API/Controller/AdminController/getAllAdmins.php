<?php
include "../../config/headers.php";
header("Access-Control-Allow-Methods: GET");
include_once "../../config/Database.php";
include "../../Models/Admin.php";

$database = new Database();
$db = $database->connect();
$admin = new Admin($db);
$result = $admin->getAdmins();
$num = $result->rowCount();
if ($num > 0) {
    $customerArr = array();
    $customerArr['data'] = array();
    while ($row = $result->fetch(PDO::FETCH_ASSOC)) {
        ;
        $customer_item = array(
            "id" => $row['id'],
            "email" => $row['email'],
            "password" => $row['password'],
            "full_name" => $row['full_name'],
            "age" => $row['age'],
            "admin_image" => "http://" . getHostByName(getHostName()) . "/" . $admin->getDir() . "/" . $row['aimg']
        );
        array_push($customerArr['data'], $customer_item);
    }
    echo json_encode(array(
        "Admins_Info" => $customerArr
    ));
} else {
    http_response_code(404);
    // tell the user login failed
    echo json_encode(array("message" => "No data found."));
}