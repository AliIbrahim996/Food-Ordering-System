<?php
require_once "../RestaurantManagerController/RateInitializer.php";
$data = RateInitializer::getData();
if (DataManagement::checkRatingEmptyData($data)) {
    $flag = RateInitializer::getRate()->createRate($data);
    if ($flag == 1) {
        http_response_code(201);
        echo json_encode(array(
            "message" => "Rating restaurant done!",
            "flag" => 1
        ));
    } else {
        http_response_code(403);
        echo json_encode(array(
            "message" => "Error: something went wrong! " . $flag,
            "flag" => -1
        ));
    }
} else {
    http_response_code(401);
    echo json_encode(array(
        "message" => "Something went wrong! Check your data!",
        "flag" => 0
    ));
}