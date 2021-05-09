<?php
require_once "../TableController/TableInitialize.php";
require_once "../../config/headers.php";
include_once "../../config/Database.php";
header("Access-Control-Allow-Methods: DELETE");
include "../../Models/TableReservation.php";
$data = TableInitialize::getData();
//check if table_status is still waiting for approval
$table = TableInitialize::getTable();
try {
    $result = json_decode($table->getTableStatus($data->table_id));
    if ($result->flag == 1) {
        if (($result->table_status != "accepted" || $result->table_status != "rejected") &&
        !empty($data->reservation_id)){
            $reservation = new TableReservation(TableInitialize::getConn());
            $deleteFlag = $reservation->deleteReservation($data->reservation_id);
            if ($deleteFlag) {
                http_response_code(200);
                echo json_encode(array(
                    "message" => "Reservation Deleted Successfully ",
                    "flag" => 1
                ));
            } else {
                http_response_code(401);
                echo json_encode(array(
                    "message" => "Error: something went wrong! ".$deleteFlag,
                    "flag" => -3
                ));
            }
        } else {
            http_response_code(403);
            echo json_encode(array(
                "message" => "Your reservation status has been set to " . $result->table_status,
                "flag" => -2
            ));
        }
    } else {
        http_response_code(404);
        echo json_encode($result);
    }
} catch (Exception $e) {
    http_response_code(403);
    echo json_encode(array(
        "message" => "Error: " . $e->getMessage(),
        "flag" => 0
    ));
}

