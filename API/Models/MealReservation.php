<?php


class MealReservation

{

    private  $conn;
    private  $table = 'customer_meal_booking';
    /**
     * id int(11) NOT NULL,
     * customer_id int(11) NOT NULL,
     * restaurant_id int(11) NOT NULL,
     * meal_id int(11) NOT NULL,
     * data_time_booking datetime DEFAULT NULL,
     * data_time_delivary datetime DEFAULT NULL,
     * is_in_door int(11) DEFAULT NULL,
     * restaurent_tables_id int(11) DEFAULT NULL,
     * order_status varchar(255) DEFAULT NULL,
     * quantity int(11) DEFAULT NULL
     */

    private  $id;
    private  $customer_id;
    private  $meal_id;
    private  $data_time_booking;
    private  $data_time_delivary;
    private  $is_in_door;
    private  $restaurent_tables_id;
    private  $order_status;
    private  $quantity;
    private  $restaurant_id;

    public function __construct(PDO $db)
    {
        $this->conn = $db;
    }

    function createMealReservation($data)
    {
        // insert query
        $query = "INSERT INTO $this->table 
            SET
            
                customer_id = ?, restaurant_id = ? ,
                meal_id = ?,data_time_booking = ?,
                is_in_door = ?, 
                order_status = ?,
                quantity = ?";
        //prepare conn
        $stmt = $this->conn->prepare($query);
        $stmt->bindParam(1, $data->customer_id);
        $stmt->bindParam(2, $data->restaurant_id);
        $stmt->bindParam(3, $data->meal_id);
        $stmt->bindParam(4, $data->data_time_booking);
        $stmt->bindParam(5, $data->is_in_door);
        $stmt->bindParam(6, $data->order_status);
        $stmt->bindParam(7, $data->quantity);
        try {
            if ($stmt->execute()) {
                //201 created
                http_response_code(201);
                return json_encode(array(
                    "message" => "Meal Reservation created successful",
                    "flag" => 1
                ));
            }
        } catch (Exception $e) {
            http_response_code(400);
            return json_encode(array(
                "message" => "error: " . $e->getMessage(),
                "flag" => -1
            ));
        }
    }

    function createMealOnReservation($data)
    {
        // insert query
        $query = "INSERT INTO $this->table 
            SET
            
                customer_id = ?, restaurant_id = ? ,
                meal_id = ?,
                data_time_booking = ?,
                is_in_door = ?, 
                order_status = ?,
                quantity = ?,
                restaurent_tables_id = ?";
        //prepare conn
        $stmt = $this->conn->prepare($query);
        $stmt->bindParam(1, $data->customer_id);
        $stmt->bindParam(2, $data->restaurant_id);
        $stmt->bindParam(3, $data->meal_id);
        $stmt->bindParam(4, $data->data_time_booking);
        $stmt->bindParam(5, $data->is_in_door);
        $stmt->bindParam(6, $data->order_status);
        $stmt->bindParam(7, $data->quantity);
        $stmt->bindParam(8, $data->table_id);
        try {
            if ($stmt->execute()) {
                //201 created
                http_response_code(201);
                return json_encode(array(
                    "message" => "Meal Reservation created successful",
                    "flag" => 1
                ));
            }
        } catch (Exception $e) {
            http_response_code(400);
            return json_encode(array(
                "message" => "error: " . $e->getMessage(),
                "flag" => -1
            ));
        }
    }

    public function updateOrderStatus($data)
    {
        $query = "UPDATE $this->table
        SET  order_status = ?
        WHERE id= ?";
        $stmt = $this->conn->prepare($query);
        $stmt->bindParam(1, $data->order_status);
        $stmt->bindParam(2, $data->order_id);

        try {
            $stmt->execute();
            return true;
        } catch (Exception $e) {
            return $e->getMessage();
        }
    }

    public function getAllDeliveryOrders($restaurant_id): PDOStatement
    {
        $query = "Select * from $this->table where restaurant_id = ? and  is_in_door <> 0 ";
        $stmt = $stmt = $this->conn->prepare($query);
        $stmt->bindParam(1, $restaurant_id);
        $stmt->execute();
        return $stmt;
    }

    public function getAllCustomerReservations($customer_id): PDOStatement
    {
        $query = "Select * from $this->table where customer_id = ?";
        $stmt = $stmt = $this->conn->prepare($query);
        $stmt->bindParam(1, $customer_id);
        $stmt->execute();
        return $stmt;
    }

    public function getAllCustomerDeliveryOrders($customer_id): PDOStatement
    {
        $query = "Select * from $this->table where customer_id = ? and  is_in_door = 1 ";
        $stmt = $stmt = $this->conn->prepare($query);
        $stmt->bindParam(1, $customer_id);
        $stmt->execute();
        return $stmt;
    }

    public function getOrderStatus($order_id)
    {
        $query = "Select order_status from $this->table where id = ?";
        $stmt = $stmt = $this->conn->prepare($query);
        $stmt->execute();
        $stmt->bindParam(1, $order_id);
        $row = $stmt->fetch(PDO::FETCH_ASSOC);
        return $row['order_status'];
    }

    public function delete($order_id)
    {
        $query = "Delete from $this->table where id = ?";
        $stmt = $stmt = $this->conn->prepare($query);
        $stmt->bindParam(1, $order_id);
        try {
            if ($stmt->execute()) {
                http_response_code(200);
                return json_encode(array(
                    "Message" => "Meal Reservation deleted successful",
                    "flag" => 1
                ));
            }
        } catch (Exception $e) {
            http_response_code(400);
            return json_encode(array(
                "message" => "error: " . $e->getMessage(),
                "flag" => -1
            ));
        }
    }

    public function updateMealReservation($data)
    {
        $query = "Update $this->table 
            SET
            
                customer_id = ?, restaurant_id = ? ,meal_id = ?,data_time_booking = ?,data_time_delivary = ?,
                is_in_door = ?, restaurent_tables_id = ?, order_status = ?,
                quantity = ?
                where id = ?";

        $stmt = $this->setStatementData($query, $data);
        $stmt->bindParam(10, $data->order_id);
        try {
            if ($stmt->execute()) {
                //200 ok
                http_response_code(200);
                return json_encode(array(
                    "Message" => "Meal Reservation updated successful",
                    "flag" => 1
                ));
            }
        } catch (Exception $e) {
            http_response_code(400);
            return json_encode(array(
                "message" => "error: " . $e->getMessage(),
                "flag" => -1
            ));
        }

    }

    private function setStatementData($query, $data)
    {

        $stmt = $this->conn->prepare($query);
        // bind the values
        $stmt->bindParam(1, $data->customer_id);
        $stmt->bindParam(2, $data->restaurant_id);
        $stmt->bindParam(3, $data->meal_id);
        $stmt->bindParam(4, $data->data_time_booking);
        $delivered = !empty($data->data_time_delivary) ? $data->data_time_delivary : null;
        $stmt->bindParam(5, $delivered);
        $stmt->bindParam(6, $data->is_in_door);
        $rest_id = !empty($data->restaurant_table_id) ? $data->restaurant_table_id : null;
        $stmt->bindParam(7, $rest_id);
        $stmt->bindParam(8, $data->order_status);
        $stmt->bindParam(9, $data->quantity);
        return $stmt;
    }

    public function getAllRestReservations($rest_email)
    {
        $query = "Select * from $this->table where  restaurant_id = ?";
        $stmt = $stmt = $this->conn->prepare($query);
        $stmt->bindParam(1, $rest_email);
        $stmt->execute();
        return $stmt;
    }
}