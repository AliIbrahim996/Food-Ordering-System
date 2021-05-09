<?php


class TableReservation
{
    private  $conn;
    private  $table = 'customer_table_booking';
    /**
     *`id` int(11) NOT NULL,
     * `restaurant_id` int(11) NOT NULL,
     * `restaurent_tables_id` int(11) NOT NULL,
     * `customer_id` int(11) NOT NULL,
     * `reservation_info` varchar(255) DEFAULT NULL
     *
     */

    private $id;
    private $restaurant_id;
    private $restaurent_tables_id;
    private $customer_id;
    private $reservation_info;


    public function __construct(PDO $db)
    {
        $this->conn = $db;
    }

    function createTableReservation($data)
    {
        // insert query
        $query = "INSERT INTO $this->table 
            SET restaurant_id = ?,restaurent_tables_id = ?,customer_id = ?,reservation_info = ?";

        $stmt = $this->conn->prepare($query);
        $info = json_encode($data->reservation_info);
        echo $info;
        // bind the values
        $stmt->bindParam(1, $data->restaurant_id);
        $stmt->bindParam(2, $data->restaurent_tables_id);
        $stmt->bindParam(3, $data->customer_id);
        $stmt->bindParam(4, $info);
        // execute the query, also check if query was successful
        try {
            if ($stmt->execute()) {
                //201 created
                http_response_code(201);
                return json_encode(array(
                    "message" => "Table Reservation created successful",
                    "flag" => 1
                ));
            }

        } catch (Exception $e) {
            http_response_code(400);
            return json_encode(array(
                "message" => "error: " . $e->getMessage()
            ));
        }

    }

    public function getCustomerReservation($customer_id): PDOStatement
    {
        $query = "Select * from  $this->table where  customer_id = ? ";
        $stmt = $this->conn->prepare($query);
        $stmt->bindParam(1, $customer_id);
        $stmt->execute();
        return $stmt;
    }

    function getReservations($rest_id)
    {
        $query = "Select * from  $this->table where  restaurant_id = ? ";
        $stmt = $this->conn->prepare($query);
        $stmt->bindParam(1, $rest_id);
        $stmt->execute();
        return $stmt;
    }

    function deleteReservation($reservation_id)
    {
        $query = "Delete from $this->table where id = $reservation_id";
        $stmt = $this->conn->prepare($query);
        try {
            $stmt->execute();
            return 1;
        } catch (Exception $e) {
            return $e->getMessage();
        }
    }

    function updateReservationInfo($data)
    {
        $query = "UPDATE $this->table 
            SET
                reservation_info = ?
                where id = ? ";
        // prepare the query
        $stmt = $this->conn->prepare($query);
        $stmt->bindParam(1, $data->reservation_info);
        $stmt->bindParam(2, $data->reservation_id);
        // execute the query, also check if query was successful
        try {
            $stmt->execute();
            return 1;
        } catch (Exception $e) {
            return $e->getMessage();
        }
    }
}