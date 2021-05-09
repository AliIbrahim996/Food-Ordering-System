<?php
require_once "../TableController/TableInitialize.php";
header("Access-Control-Allow-Methods: PUT");
$data = TableInitialize::getData();
if (!empty($data->table_status) && $data->restaurent_tables_id) {
    $table = TableInitialize::getTable();
    $flag = $table->updateTableStatus($data);
    if ($flag==1) {
        if ($data->table_status == "unavailable") {
            http_response_code(201);
            echo json_encode(array(
                "message" => "Table Reservation accepted!",
                "flag" => 1
            ));

        } else {
            http_response_code(201);
            echo json_encode(array(
                "message" => "Table reservation rejected!",
                "flag" => -1
            ));
        }
    }else{
        http_response_code(403);
        echo json_encode(array(
            "message" => "error: " . $flag,
            "flag" => -2
        ));
    }
} else {
    http_response_code(204);
    echo json_encode(array(
        "message" => "Check if your data empty!",
        "flag" => 0
    ));
}