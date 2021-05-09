<?php
require_once "../CustomerController/CustomerInitializer.php";
header("Access-Control-Allow-Methods: GET");
include "../../Models/Rate.php";
include "../../Models/Restaurant.php";
$data = CustomerInitializer::getData();
$rest = new Restaurant(CustomerInitializer::getConn());
if (!empty($data->rest_email)) {
    $restaurant_id = $rest->getId($data->rest_email);
    $rate = new Rate(CustomerInitializer::getConn());
    $result = $rate->browseRate($restaurant_id);
    if ($result->rowCount() > 0) {
        $customer = CustomerInitializer::getCustomer();

        $rateArr = array();
        $rateArr['data'] = array();
        while ($row = $result->fetch(PDO::FETCH_ASSOC)) {
            $c_id = $customer->getId($row['customer_id']);
            if(!empty($c_id))
            $rate_item = array(
                "id" => $row['id'],
                "customer_id" => $c_id,
                "customer_name" => json_decode($customer->
                getCustomerInfoById($row['customer_id']))->full_name,
                "customer_rate" => $row['customer_rate'],
                "feedBack" => $row['feed_back'],
                "restaurant_id" => $restaurant_id
            );
            else
                $rate_item = "no_data_found!";
            array_push($rateArr['data'], $rate_item);
        }
        http_response_code(200);
        echo json_encode(array(
            "Rating" => $rateArr,
            "flag" => 1
        ));
    } else {
        http_response_code(404);
        echo json_encode(array(
            "message" => "no data found",
            "flag" => -1
        ));
    }

} else {
    http_response_code(204);
    echo json_encode(array(
        "message" => "Check if your data empty!",
        "flag" => 0
    ));
}
