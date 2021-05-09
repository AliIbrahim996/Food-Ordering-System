<?php

require_once "TableReservationInitializer.php";
header("Access-Control-Allow-Methods: POST");
$data = TableReservationInitializer::getData();
//check if data empty or not
if (DataManagement::checkTableReservationEmptyData($data)) {
    //reserve table
    echo TableReservationInitializer::getTableReservation()->createTableReservation($data);

} else {
    http_response_code(404);
    echo json_encode(array(
        "message" => "error: check your data! ",
        "flag" => 0
    ));
}