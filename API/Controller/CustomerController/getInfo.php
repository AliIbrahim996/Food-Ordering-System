<?php
include "../../config/Database.php";
include_once "../../config/headers.php";
header("Access-Control-Allow-Methods: GET");
include "Models/Customer.php";

$database =new Database();
$db= $database->connect();
$c = new Customer($db);
if(!empty($data->email)){
    echo $c->getCustomerInfo($this->data->email);
}

