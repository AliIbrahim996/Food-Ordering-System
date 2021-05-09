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
$rest = new Restaurant(MealReservationInitializer::getConn());
$mealReservation = MealReservationInitializer::getMealReservation();
if (!empty($data->email)) {
    $reservationArr = array();
    $reservationArr['data'] = array();
    $result = $rest->getResturantsByAdminId($data->email);
    if ($result->rowCount() > 0) {
        while ($row = $result->fetch(PDO::FETCH_ASSOC)) {
            $reservations = $mealReservation->getAllRestReservations($row['manager_email']);
            if ($reservations->rowCount() > 0) {
                while ($row2 = $reservations->fetch(PDO::FETCH_ASSOC)) {
                    $d = json_decode($c->getCustomerInfoById($row2['customer_id']));
                    $customer_name = $d->full_name;
                    $restaurant_name = $rest->getRestaurantNameByMgrEmail($row2['restaurant_id']);
                    $meal_name = $m->getMealName($row2['meal_id']);
                    $table_info = "empty";
                    if (!empty($row2['restaurent_tables_id']) && $row2['restaurent_tables_id'] != null) {
                        $table_info = $t->getTableInfo($row2['restaurent_tables_id']);
                    }
                    $reservation_item = array(
                        "id" => $row2['id'],
                        "restaurant_id" => $row2['restaurant_id'],
                        "restaurant_name" => $restaurant_name,
                        "customer_id" => $row2['customer_id'],
                        "customer_name" => $customer_name,
                        "meal_id" => $row2['meal_id'],
                        "meal_name" => $meal_name,
                        "quantity" => $row2['quantity'],
                        "unit_price" => $m->getMealPrice($row2['meal_id']),
                        "order_status" => $row2['order_status'],
                        "data_time_booking" => $row2['data_time_booking'],
                        "data_time_delivary" => $row2['data_time_delivary'],
                        "is_in_door" => $row2['is_in_door'],
                        "table_info" => json_decode($table_info)

                    );
                    array_push($reservationArr['data'], $reservation_item);
                }

            } else {
                array_push($reservationArr['data'], "no_data_found!");
            }
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
    /*
    try {

            ->getAllCustomerReservations($data->customer_id);
        $num = $mealReservation->rowCount();
        if ($num > 0) {
            $reservationArr = array();
            $reservationArr['data'] = array();

            while ($row = $mealReservation->fetch(PDO::FETCH_ASSOC)) {
                $d = json_decode($c->getCustomerInfoById($row['customer_id']));
                $customer_name = $d->full_name;
                $restaurant_name = $r->getRestaurantNameByMgrEmail($row['restaurant_id']);
                $meal_name = $m->getMealName($row['meal_id']);
                $table_info = "empty";
                if (!empty($row['restaurent_tables_id']) && $row['restaurent_tables_id'] != null) {
                    $table_info = $t->getTableInfo($row['restaurent_tables_id']);
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
                    "table_info" => json_decode($table_info)

                );
                array_push($reservationArr['data'], $reservation_item);
            }

        } else {
            http_response_code(404);

        }
    } catch (Exception $e) {
        http_response_code(401);
        echo json_encode(array(
            "message" => "Something went wrong! " . $e->getMessage(),
            "flag" => -2
        ));
    }*/
} else {
    http_response_code(204);
    echo json_encode(array(
        "message" => "Something went wrong! Check your data!",
        "flag" => 0
    ));
}