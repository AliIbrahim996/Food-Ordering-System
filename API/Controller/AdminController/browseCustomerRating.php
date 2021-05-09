<?php
require_once "../CustomerController/CustomerInitializer.php";
header("Access-Control-Allow-Methods: GET");
include "../../Models/Rate.php";
include "../../Models/Restaurant.php";
$rest = new Restaurant(CustomerInitializer::getConn());
$data = CustomerInitializer::getData();
try {
    $rate = new Rate(CustomerInitializer::getConn());
    $customers_info = CustomerInitializer::getCustomer()->getCustomers();
    $num = $customers_info->rowCount();
    $restaurants_id = $rest->getAdminRest($data->email);
    if ($restaurants_id->rowCount() > 0) {
        $rateArr = array();
        $rateArr['data'] = array();
        while ($row = $restaurants_id->fetch(PDO::FETCH_ASSOC)) {
            $ratingInfo = $rate->browseCustomerRate($row['id']);
            if ($ratingInfo != "No data found!") {
                while ($row2 = $ratingInfo->fetch(PDO::FETCH_ASSOC)) {
                    $rate_item = array(
                        "id" => $row2['id'],
                        "restaurant_id" => $row['id'],
                        "restaurant_name" => $rest->getRestaurantNameById($row['id']),
                        "customer_id" => $row2['customer_id'],
                        "customer_name" => json_decode(CustomerInitializer::getCustomer()->getCustomerInfoById($row2['customer_id']))->full_name,
                        "customer_rate" => $row2['customer_rate'],
                        "feedBack" => $row2['feed_back'],
                    );
                    array_push($rateArr['data'], $rate_item);
                }
            } else {
                $rate_item = array(
                    "message" => $ratingInfo->message,
                    "flag" => $ratingInfo->flag
                );
                array_push($rateArr['data'], $rate_item);
            }
        }
        http_response_code(200);
        echo json_encode(array(
                "rating_info" => $rateArr,
                "flag" => 1
            )
        );
    } else {
        http_response_code(404);
        echo json_encode(array(
            "message" => "no_data_found",
            "flag" => -2
        ));
    }

} catch
(Exception $e) {
    http_response_code(403);
    echo json_encode(array(
        "message" => "Error! something went wrong " . $e->getMessage(),
        "flag" => 0
    ));
}
