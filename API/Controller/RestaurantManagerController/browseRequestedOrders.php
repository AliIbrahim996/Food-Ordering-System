<?php
require_once "MealInitializer.php";
header("Access-Control-Allow-Methods: GET");
include "../../Models/Restaurant.php";
include "../../Models/MealReservation.php";
include "../../Models/Table.php";
include "../../Models/Customer.php";
$data = MealInitializer::getData();
$rest = new Restaurant(MealInitializer::getConn());
if (!empty($data->rest_email)) {
    $restaurant_id = $rest->getId($data->rest_email);
    $mealReservation = new MealReservation(MealInitializer::getConn());
    try {
        $result = $mealReservation->getAllDeliveryOrders($data->rest_email);
        $num = $result->rowCount();
        if ($num > 0) {
            $reservationArr = array();
            $reservationArr ['data'] = array();
            while ($row = $result->fetch(PDO::FETCH_ASSOC)) {
                $customer = new Customer(MealInitializer::getConn());
                $meal = MealInitializer::getMeal();
                $reservation_item = array(
                    "reservation_id" => $row['id'],
                    "restaurant_id" => $row['restaurant_id'],
                    "customer_name" => json_decode($customer->getCustomerInfoById($row['customer_id']))->full_name,
                    "meal_name" => $meal->getMealName($row['meal_id']),
                    "quantity" => $row['quantity'],
                    "date_booking" => $row['data_time_booking'],
                    "date_delivered" => $row['data_time_delivary'],
                    "is_in_door" => $row['is_in_door'],
                    "order_status" => $row['order_status'],

                );
                array_push($reservationArr['data'], $reservation_item);
            }
            echo json_encode(array(
                "MealReservation_info" => $reservationArr,
                "flag" => 1
            ));
        } else {
            http_response_code(404);
            // tell the user login failed
            echo json_encode(array(
                "message" => "No data found.",
                "flag" => -1
            ));
        }
    } catch (Exception $e) {
        http_response_code(400);
        echo json_encode(array(
            "message" => "Error: " . $e->getMessage(),
            "flag" => -2
        ));
    }
} else {
    http_response_code(401);
    echo json_encode(array(
        "message" => "Check if your data empty!",
        "flag" => 0
    ));
}