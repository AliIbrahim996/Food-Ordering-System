<?php
require_once "../ReservationController/TableReservationInitializer.php";
require_once "../ReservationController/MealReservationInitializer.php";
require_once "../RestaurantController/RestaurantInitializer.php";
require_once "../TableController/TableInitialize.php";
require_once "../RestaurantManagerController/MealInitializer.php";
require_once "../CustomerController/CustomerInitializer.php";
try {
    //getAllCustomers
    $Customers_info = CustomerInitializer::getCustomer()->getCustomers();
    $num = $Customers_info->rowCount();
    if ($num > 0) {
        $tableReservationArr = array();
        $tableReservationArr['data'] = array();
        $mealReservationArr = array();
        $mealReservationArr['data'] = array();

        while ($row = $Customers_info->fetch(PDO::FETCH_ASSOC)) {
            $customer_name = json_decode(CustomerInitializer::getCustomer()
                ->getCustomerInfoById($row['id']))->full_name;
            $tableReservations_info = TableReservationInitializer::getTableReservation()
                ->getCustomerReservation($row['id']);
            $num = $tableReservations_info->rowCount();
            //get Table Reservations
            if ($num > 0) {
                while ($row2 = $tableReservations_info->fetch(PDO::FETCH_ASSOC)) {
                    $table_info = TableInitialize::getTable()->getTableInfo($row2['restaurent_tables_id']);
                    $restaurant_name = RestaurantInitializer::getRestaurant()
                        ->getRestaurantNameByMgrEmail($row['restaurant_id']);
                    $reservation_item = array(
                        "id" => $row2['id'],
                        "restaurant_id" => $row2['restaurant_id'],
                        "restaurant_name" => $restaurant_name,
                        "customer_id" => $row2['customer_id'],
                        "customer_name" => $customer_name,
                        "table_info" => $table_info,
                        "reservation_info" => $row2['reservation_info']
                    );
                    array_push($tableReservationArr['data'], $reservation_item);
                }
            } else {
                http_response_code(404);
                $reservation_item = array(
                    "message" => "No Table reservation for Customer " . $customer_name
                );
                array_push($tableReservationArr['data'], $reservation_item);
            }
            //get Meal Reservations
            $mealReservation_info = MealReservationInitializer::getMealReservation()
                ->getAllCustomerReservations($row['id']);
            $num = $mealReservation_info->rowCount();
            if ($num > 0) {
                while ($row2 = $mealReservation_info->fetch(PDO::FETCH_ASSOC)) {
                    $restaurant_name = RestaurantInitializer::getRestaurant()
                        ->getRestaurantNameByMgrEmail($row['restaurant_id']);
                    $meal_name = MealInitializer::getMeal()->getMealName($row['meal_id']);
                    $table_info = "empty";
                    if (!empty($row['restaurent_tables_id']) && $row['restaurent_tables_id'] != null) {
                        $table_info = TableInitialize::getTable()->getTableInfo($row['restaurent_tables_id']);
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
                        "unit_price" => MealInitializer::getMeal()->getMealPrice($row['meal_id']),
                        "order_status" => $row['order_status'],
                        "data_time_booking" => $row['data_time_booking'],
                        "data_time_delivary" => $row['data_time_delivary'],
                        "is_in_door" => $row['is_in_door'],
                        "table_info" => $table_info

                    );
                    array_push($mealReservationArr['data'], $reservation_item);
                }
            } else {
                $reservation_item = array(
                    "message" => "No meal Reservation data found for customer ".$customer_name
                );
                array_push($mealReservationArr['data'], $reservation_item);
            }
        }
        http_response_code(200);
        echo json_encode(array(
                "table_reservations" => $tableReservationArr,
                "meal_reservations" => $mealReservationArr,
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
    http_response_code(401);
    echo json_encode(array(
        "message" => "Error: something went wrong! " . $e->getMessage(),
        "flag" => 0
    ));
}
