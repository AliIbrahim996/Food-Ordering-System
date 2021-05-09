<?php
include "../../config/headers.php";
include "../../config/Database.php";
header("Access-Control-Allow-Methods: DELETE");
$data = json_decode(file_get_contents("php://input"));
$dataBase = new Database();
$conn = $dataBase->connect();
if (!empty($data->order_id)) {
    $q = "Delete from customer_meal_booking where id = ?";
    $q2 = "Select * from customer_meal_booking where restaurent_tables_id = ?";
    $stm1 = $conn->prepare($q);
    $stm1->bindParam(1, $data->order_id);
    $stmt2 = $conn->prepare($q2);
    try {
        $stm1->execute();
        http_response_code(200);
        $message = "meal reservation cancel!";
        $stmt2->bindParam(1, $data->table_id);
        if (!empty($data->table_id))
            if ($stmt2->rowCount() == 0) {
                $stm1 = $conn->prepare("delete from customer_table_booking where restaurent_tables_id = ?");
                $stm1->bindParam(1, $data->table_id);
                $stm1->execute();
                echo json_encode(
                    array("message" => "all information cancelled", "flag" => 1)
                );
            } else {
                echo json_encode(
                    array("message" => $message, "flag" => 1)
                );
            }


    } catch (Exception $e) {
        http_response_code(401);
        echo json_encode(array(
            "message" => "Something went wrong! " . $e->getMessage(),
            "flag" => 0
        ));
    }

} else {
    http_response_code(204);
    echo json_encode(array(
        "message" => "Something went wrong! Check your data!",
        "flag" => 0
    ));
}