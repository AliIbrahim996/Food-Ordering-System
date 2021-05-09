<?php
require_once "../CustomerController/CustomerInitializer.php";
$result = CustomerInitializer::getCustomer()->getCustomers();
$num = $result->rowCount();
if ($num > 0) {
    $customerArr = array();
    $customerArr['data'] = array();
    while ($row = $result->fetch(PDO::FETCH_ASSOC)) {
        $customer_item = array(
            "id" => $row['id'],
            "email" => $row['email'],
            "password" => $row['password'],
            "full_name" => $row['full_name'],
            "address" => $row['address'],
            "phone_number" => $row['phone_number'],
            "gender" => $row['gender'],
            "c_image" => CustomerInitializer::getCustomer()->getDir() . $row['cimg']
        );
        array_push($customerArr['data'], $customer_item);
    }
    echo json_encode(array(
        "Customers_Info" => $customerArr,
        "flag" => 1
    ));
} else {
    http_response_code(204);
    // tell the user login failed
    echo json_encode(
        array("message" => "No data found.",
            "flag" => 0)
    );
}