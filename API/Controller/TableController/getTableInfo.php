<?php
require_once "TableInitialize.php";
header("Access-Control-Allow-Methods: PUT");
$table = TableInitialize::getTable();
$data = TableInitialize::getData();
if (!empty($data->table_id)) {
    echo $table->getTableInfo($data->table_id);
} else {
    http_response_code(404);
    echo json_encode(array(
        "message" => "Check your table_id!",
        "flag" => 0
    ));
}