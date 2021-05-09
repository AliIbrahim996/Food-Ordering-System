<?php
require_once "../TableController/TableInitialize.php";
require_once "../../config/headers.php";
include_once "../../config/Database.php";
header("Access-Control-Allow-Methods: GET");
include "../../Models/TableReservation.php";
include "../../Models/Customer.php";
include "../../Models/Restaurant.php";
$data = TableInitialize::getData();
$c = new Customer(TableInitialize::getConn());
$rest = new Restaurant(TableInitialize::getConn());
//check if customer id is empty or not

if (!empty($data->customer_email)) {
    $reservation = new TableReservation(TableInitialize::getConn());
    try {
        //get all reservations
        $result = $reservation->getCustomerReservation($data->customer_email);
        $num = $result->rowCount();
        if ($num > 0) {
            $reservationArr = array();
            $reservationArr['data'] = array();
            //fetching data
            while ($row = $result->fetch(PDO::FETCH_ASSOC)) {
                $table_info = TableInitialize::getTable()->getTableInfo($row['restaurent_tables_id']);
                $customer_name = json_decode($c->getCustomerInfoById($row['customer_id']));
                $restaurant_name = $rest->getRestaurantNameByMgrEmail($row['restaurant_id']);
                $reservation_item = array(
                    "id" => $row['id'],
                    "restaurant_id" => $row['restaurant_id'],
                    "restaurant_name" => $restaurant_name,
                    "customer_id" => $row['customer_id'],
                    "customer_name" => $customer_name,
                    "table_info" => json_decode($table_info),
                    "reservation_info" => json_decode($row['reservation_info'])
                );
                array_push($reservationArr['data'], $reservation_item);
            }
            http_response_code(200);
            echo json_encode(array(
                "Reservations" => $reservationArr,
                "flag" => 1
            ));
        } else {
            http_response_code(404);
            echo json_encode(
                array(
                    "message" => "No data found!",
                    "flag" => -1
                )
            );
        }

    } catch (Exception $e) {
        http_response_code(401);
        echo json_encode(array(
            "message" => "Error: something went wrong! " . $e->getMessage(),
            "flag" => -2
        ));
    }

} else {
    http_response_code(204);
    echo json_encode(array(
        "message" => "Error! check if you filled customer_id",
        "flag" => 0
    ));
}
