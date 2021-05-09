<?php
require_once "../ReservationController/MealReservationInitializer.php";
include "../../Models/Restaurant.php";
include "../../Models/Customer.php";
include "../../Models/Meal.php";
include "../../Models/Table.php";
header("Access-Control-Allow-Methods: GET");
$m = new Meal(MealReservationInitializer::getConn());
$data = MealReservationInitializer::getData();
$t = new Table(MealReservationInitializer::getConn());
$c = new Customer(MealReservationInitializer::getConn());
$r = new Restaurant(MealReservationInitializer::getConn());
if (!empty($data->rest_email)) {
    try {
        $mealReservation = MealReservationInitializer::getMealReservation()
            ->getAllRestReservations($data->rest_email);
        if ($mealReservation->rowCount() > 0) {
            $reservationArr = array();
            $reservationArr['data'] = array();
            while ($row = $mealReservation->fetch(PDO::FETCH_ASSOC)) {
                $d = json_decode($c->getCustomerInfoById($row['customer_id']));
                $customer_name = $d->full_name;
                $restaurant_name = $r->getRestaurantNameByMgrEmail($row['restaurant_id']);
                $meal_name = $m->getMealName($row['meal_id']);
                $table_info = "empty";
                if (!empty($row['restaurent_tables_id']) && $row['restaurent_tables_id'] != null) {
                    $table_info = json_decode($t->getTableInfo($row['restaurent_tables_id']));
                }
                $reservation_item = array(
                    "id" => $row['id'],
                    "restaurant_id" => $row['restaurant_id'],
                    "restaurant_name" => $restaurant_name,
                    "customer_id" => $row['customer_id'],
                    "customer_name" => $customer_name,
                    "meal_id" => $row['meal_id'],
                    "meal_name" => $meal_name,
                    "quantity" => $row['quantity'],
                    "unit_price" => $m->getMealPrice($row['meal_id']),
                    "order_status" => $row['order_status'],
                    "data_time_booking" => $row['data_time_booking'],
                    "data_time_delivary" => $row['data_time_delivary'],
                    "is_in_door" => $row['is_in_door'],
                    "table_info" => $table_info

                );
                array_push($reservationArr['data'], $reservation_item);
            }
            http_response_code(200);
            echo json_encode(array(
                "reservation_info" => $reservationArr,
                "flag" => 1
            ));
        } else {
            http_response_code(404);
            echo json_encode(array(
                "message" => "No data found!",
                "flag" => -1
            ));
        }
    } catch (Exception $e) {
        http_response_code(401);
        echo json_encode(array(
            "message" => "Something went wrong! " . $e->getMessage(),
            "flag" => -2
        ));
    }
} else {
    http_response_code(403);
    echo json_encode(array(
        "message" => "Something went wrong! Check your data!",
        "flag" => 0
    ));
}