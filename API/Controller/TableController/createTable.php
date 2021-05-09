<?php

require_once "TableInitialize.php";
header("Access-Control-Allow-Methods: POST");
$table = TableInitialize::getTable();
$data = TableInitialize::getData();
if (DataManagement::checkTableEmptyData($data)) {
    echo $table->createTable($data);
} else {
    http_response_code(404);
    echo json_encode(array(
        "message" => "Check your Data!",
        "flag" => 0
    ));
}
