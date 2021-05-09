<?php
require_once "../CustomerController/CustomerInitializer.php";
include "../../Models/Restaurant.php";
include "../../Models/MealReservation.php";
include "../../Models/Meal.php";
include "../../Models/Table.php";

$m = new Meal(CustomerInitializer::getConn());
$data = CustomerInitializer::getData();
$t = new Table(CustomerInitializer::getConn());
$c = new Customer(CustomerInitializer::getConn());
$r = new Restaurant(CustomerInitializer::getConn());
$customer = CustomerInitializer::getCustomer();
$meaReser = new MealReservation(CustomerInitializer::getConn());
try {
    $reservationArr = array();
    $reservationArr['data'] = array();
    $restaurants_id = $r->getAdminRestMGR($data->email);
    if ($restaurants_id->rowCount() > 0) {
        while ($row = $restaurants_id->fetch(PDO::FETCH_ASSOC)) {
            $delivery = $meaReser->getAllDeliveryOrders($row['manager_email']);
            if ($delivery->rowCount() > 0) {
                while ($row2 = $delivery->fetch(PDO::FETCH_ASSOC)) {
                    $d = json_decode($c->getCustomerInfoById($row2['customer_id']));
                    $customer_name = $d->full_name;
                    $restaurant_name = $r->getRestaurantNameByMgrEmail($row2['restaurant_id']);
                    $meal_name = $m->getMealName($row2['meal_id']);
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
                        "is_in_door" => $row2['is_in_door']

                    );
                    array_push($reservationArr['data'], $reservation_item);
                }
            }
            else{
                array_push($reservationArr['data'], "no data");
            }
        }
        http_response_code(200);
        echo json_encode(array(
            "reservation_info" => $reservationArr,
            "flag" => 1
        ));
    }
    else{
        http_response_code(404);
        echo json_encode(array(
            "reservation_info" => "no data found",
            "flag" => 1
        ));
    }
} catch (Exception $e) {
    http_response_code(401);
    echo json_encode(array(
        "message" => "Error: something went wrong! " . $e->getMessage(),
        "flag" => 0
    ));
}
/*
try {
    $Customers_info = CustomerInitializer::getCustomer()->getCustomers();
    $num = $Customers_info->rowCount();
    if ($num > 0) {
        $mealReservationArr = array();
        $mealReservationArr['data'] = array();
        while ($row = $Customers_info->fetch(PDO::FETCH_ASSOC)) {
            $customer_name = json_decode(CustomerInitializer::getCustomer()->getCustomerInfoById($row['email']))->full_name;
            $mealReservation_info = MealReservationInitializer::getMealReservation()->getAllCustomerDeliveryOrders($row['email']);
            $num = $mealReservation_info->rowCount();
            if ($num > 0) {
                while ($row2 = $mealReservation_info->fetch(PDO::FETCH_ASSOC)) {
                    $restaurant_name = RestaurantInitializer::getRestaurant()
                        ->getRestaurantNameByMgrEmail($row['restaurant_id']);
                    $meal_name = MealInitializer::getMeal()->getMealName($row['meal_id']);
                    $reservation_item = array(
                        "id" => $row['id'],
                        "restaurant_id" => $row['restaurant_id'],
                        "restaurant_name" => $restaurant_name,
                        "customer_id" => $row['customer_id'],
                        "customer_name" => $customer_name,
                        "meal_id" => $row['meal_id'],
                        "meal_name" => $meal_name,
                        "quantity" => $row['quantity'],
                        "unit_price" => MealInitializer::getMeal()->getMealPrice($row['meal_id']),
                        "order_status" => $row['order_status'],
                        "data_time_booking" => $row['data_time_booking'],
                        "data_time_delivary" => $row['data_time_delivary'],
                        "is_in_door" => $row['is_in_door'],
                    );
                    array_push($mealReservationArr['data'], $reservation_item);
                }
            } else {
                $reservation_item = array(
                    "message" => "No meal Reservation data found for customer " . $customer_name
                );
                array_push($mealReservationArr['data'], $reservation_item);
            }
        }
        http_response_code(200);
        echo json_encode(array(
                "delivery_orders" => $mealReservationArr,
                "flag" => 1
            )
        );
    } else {
        http_response_code(404);
        echo json_encode(array(
            "message" => "No Customers data found!",
            "flag" => -1));
    }
} catch (Exception $e) {

}

*/