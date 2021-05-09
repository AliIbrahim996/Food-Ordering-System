<?php
require_once "../TableController/TableInitialize.php";
header("Access-Control-Allow-Methods: GET");
include "../../Models/TableReservation.php";
include "../../Models/Customer.php";
include "../../Models/Restaurant.php";
$data = TableInitialize::getData();
$c = new Customer(TableInitialize::getConn());
$rest = new Restaurant(TableInitialize::getConn());
$table = TableInitialize::getTable();
//check if customer id is empty or not
if (!empty($data->rest_email)) {
    $reservation = new TableReservation(TableInitialize::getConn());
    try {
        //get all reservations
        $result = $reservation->getReservations($data->rest_email);
        if ($result->rowCount() > 0) {
            $reservationArr = array();
            $reservationArr['data'] = array();
            //fetching data
            while ($row = $result->fetch(PDO::FETCH_ASSOC)) {
                $table_number = $table->getTableNumer($row['restaurent_tables_id']);
                $t_status = $table->getTableStatus($row['restaurent_tables_id']);
                $customer_name = json_decode($c->getCustomerInfoById($row['customer_id']))->full_name;
                $reservation_item = array(
                    "id" => $row['id'],
                    "restaurant_tables_id" => $row['restaurent_tables_id'],
                    "restaurant_id" => $row['restaurant_id'],
                    "customer_id" => $row['customer_id'],
                    "customer_name" => $customer_name,
                    "reservation_info" => json_decode($row['reservation_info']),
                    "table_number" => $table_number,
                    "t_status" => json_decode($t_status)->table_status
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
                    "message" => "no_data_found!",
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
    http_response_code(400);
    echo json_encode(array(
        "message" => "Error! check if you filled customer_id",
        "flag" => 0
    ));
}
