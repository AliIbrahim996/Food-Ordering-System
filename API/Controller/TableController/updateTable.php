<?php
require_once "TableInitialize.php";
header("Access-Control-Allow-Methods: PUT");
$table = TableInitialize::getTable();
$data = TableInitialize::getData();
if (DataManagement::checkTableEmptyData($data) && !empty($data->table_id)) {
    $flag = $table->updateTableContent($data);
    if ($flag == 1) {
        http_response_code(201);
        echo json_encode(array(
            "message" => "Table updated successful",
            "flag" => 1
        ));
    } else {
        http_response_code(400);
        echo json_encode(array(
            "message" => "error: " . $flag,
            "flag" => -1
        ));
    }
}
else{
    http_response_code(204);
    echo json_encode(array(
        "message" => "check your data! ",
        "flag" => 0
    ));
}