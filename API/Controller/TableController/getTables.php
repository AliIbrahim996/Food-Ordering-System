<?php
require_once "TableInitialize.php";
$server_ip = getHostByName(getHostName());
$table = TableInitialize::getTable();
$data = TableInitialize::getData();
try {
    $result = $table->getTables($data->rest_id);
    $num = $result->rowCount();
    if ($num > 0) {
        $tableArr = array();
        $tableArr['data'] = array();
        while ($row = $result->fetch(PDO::FETCH_ASSOC)) {
            $table_item = array(
                "id" => $row['id'],
                "restaurant_id" => $row['restaurant_id'],
                "table_number" => $row['table_number'],
                "table_location" => $row['table_location'],
                "table_number_seats" => $row['table_number_seats'],
                "imageUrl" => $table->getDir() . $row['table_img'],
                "table_status" => $row['table_status']
            );
            array_push($tableArr['data'], $table_item);
        }
        echo json_encode(array(
            "Tables_Info" => $tableArr,
            "flag" => 1
        ));
    } else {
        http_response_code(404);
        // tell the user login failed
        echo json_encode(array(
            "message" => "No data found.",
            "flag" => -1
        ));
    }
} catch (Exception $e) {
    http_response_code(400);
    echo json_encode(array(
        "message" => "Error: " . $e->getMessage(),
        "flag" => -1
    ));
}