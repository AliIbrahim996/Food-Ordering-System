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
$reservation = new TableReservation(TableInitialize::getConn());
if (!empty($data->email)) {
    $reservationArr = array();
    $reservationArr['data'] = array();
    $result = $rest->getResturantsByAdminId($data->email);
    if ($result->rowCount() > 0) {
        while ($row = $result->fetch(PDO::FETCH_ASSOC)) {
            $resrvations = $reservation->getReservations($row['manager_email']);
            if ($resrvations->rowCount() > 0) {
                while ($row2 = $resrvations->fetch(PDO::FETCH_ASSOC)) {
                    $table_info = TableInitialize::getTable()->getTableInfo($row2['restaurent_tables_id']);
                    $customer_name = json_decode($c->getCustomerInfoById($row2['customer_id']))->full_name;
                    $restaurant_name = $rest->getRestaurantNameByMgrEmail($row2['restaurant_id']);
                    $reservation_item = array(
                        "id" => $row2['id'],
                        "restaurant_id" => $row2['restaurant_id'],
                        "restaurant_name" => $restaurant_name,
                        "customer_id" => $row2['customer_id'],
                        "customer_name" => $customer_name,
                        "table_info" => json_decode($table_info),
                        "reservation_info" => json_decode($row2['reservation_info'])
                    );
                    array_push($reservationArr['data'], $reservation_item);
                }
            } else {
                array_push($reservationArr['data'], "no_data_found!");
            }
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
} else {
    http_response_code(204);
    echo json_encode(array(
        "message" => "Error! check if you filled customer_id",
        "flag" => 0
    ));
}
