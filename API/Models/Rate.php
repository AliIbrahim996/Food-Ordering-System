<?php


class Rate
{
    private  $conn;
    private  $table = 'rate';

    /**
     *
     * `id` int(11) NOT NULL,
     * `customer_id` int(11) NOT NULL,
     * `restaurant_id` int(11) NOT NULL,
     * `customer_rate` double NOT NULL,
     * `feed_back` varchar(255) DEFAULT NULL
     */

    /**
     * Rate constructor.
     * @param $db
     */
    public function __construct(PDO $db)
    {
        $this->conn = $db;
    }

    public function browseRate($restaurant_id)
    {
        $query = "Select * from rate
              where restaurant_id = $restaurant_id";
        $stmt = $this->conn->prepare($query);
        $stmt->execute();
        return $stmt;
    }

    public function browseCustomerRate($rest_id)
    {
        $query = "Select * from $this->table where restaurant_id";
        $stmt = $this->conn->prepare($query);
        $stmt->bindParam(1, $rest_id);
        try {
            $stmt->execute();
            $num = $stmt->rowCount();
            if ($num > 0) {
                return $stmt;
            } else {
                return
                    "No data found!";
            }
        } catch (Exception $e) {
            http_response_code(403);
            return json_encode(array(
                "message" => "Error: " . $e->getMessage(),
                "flag" => -2
            ));
        }

    }

    public function createRate($data)
    {
        $query = "Insert into $this->table
                     SET  customer_id = ?, restaurant_id = ? , customer_rate = ?,feed_back = ? ";
        $stmt = $this->conn->prepare($query);
        $stmt->bindParam(1, $data->customer_id);
        $stmt->bindParam(2, $data->restaurant_id);
        $feedBack = !empty($data->feed_back) ? $data->feed_back : null;
        $stmt->bindParam(3, $data->customer_rate);
        $stmt->bindParam(4, $feedBack);
        try {
            $stmt->execute();
            return 1;
        } catch (Exception $e) {
            return $e->getMessage();
        }
    }

    public function getRestAvgRate($rest_id)
    {
        $query = "Select AVG(customer_rate) rate from $this->table where restaurant_id = ?";
        $stmt = $this->conn->prepare($query);
        $stmt->bindParam(1, $rest_id);
        $stmt->execute();
        $num = $stmt->rowCount();
        if ($num > 0) {
            $row = $stmt->fetch(PDO::FETCH_ASSOC);
            if ($row['rate'] == null) {
                return 0;
            }
            $result = $row['rate'];
            return $result;
        } else {
            return 0;
        }
    }
}