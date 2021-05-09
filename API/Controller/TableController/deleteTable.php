<?php
require_once "TableInitialize.php";
header("Access-Control-Allow-Methods: Delete");
$table = TableInitialize::getTable();
$data = TableInitialize::getData();
if (!empty($data->table_id)) {
    $flag = $table->delete($data->table_id);
    if ($flag) {
        http_response_code(200);
        echo json_encode(array(
            "message" => "Table Deleted successful",
            "flag" => 1
        ));
    } else {
        http_response_code(400);
        echo json_encode(array(
            "message" => "error: " . $flag,
            "flag" => -1
        ));
    }
} else {
    http_response_code(204);
    echo json_encode(array(
        "message" => "Check your table_id!",
        "flag" => 0
    ));
}
