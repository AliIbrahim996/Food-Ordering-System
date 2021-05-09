<?php

require_once "CustomerInitializer.php";
header("Access-Control-Allow-Methods: POST");
$data = CustomerInitializer::getData();
$c = CustomerInitializer::getCustomer();
if(!empty($data->email) && !empty($data->password)) {
    $email = $data->email;
    $password = $data->password;
    echo $c->customerLogIn($email,$password);
    }
else{
    http_response_code(404);
    echo json_encode(array(
        "message" => "check email and password!",
        "flag" => 0
    ));
}

