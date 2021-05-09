<?php
require_once "CustomerInitializer.php";
header("Access-Control-Allow-Methods: POST");
$data = CustomerInitializer::getData();
$c = CustomerInitializer::getCustomer();
if(!empty($data->email)){
    echo $c->getCustomerImage($data->email);
}
else{
    http_response_code(402);
    echo json_encode(
        array("error" => "check your data!")
    );
}