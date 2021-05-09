<?php
require_once "CustomerInitializer.php";
header("Access-Control-Allow-Methods: POST");
$data = CustomerInitializer::getData();
$c = CustomerInitializer::getCustomer();
if (DataManagement::checkCustomerEmptyData($data)) {
    echo $c->registerCustomer($data);
}
else{
    http_response_code(401);
    echo  json_encode(array(
        "message" => "Customer registered faild",
        "flag" => 0
    ));
}
