<?php

class Menu
{
    private $conn;
    private $table = 'menu';
    //Customer Prop

    /**
     *  id              INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
     * restaurant_id   INTEGER NOT NULL
     */

    public $id;
    public $rest_id;

    public function __construct(PDO $db)
    {
        $this->conn = $db;
    }

    function registerMeal($data)
    {
        // insert query
        $query = "INSERT INTO " . $this->table . "
            SET
                restaurant_id = ?
               ";

        $stmt = $this->conn->prepare($query);
        // bind the values
        $stmt->bindParam(1, $data->restaurant_id);
        // execute the query, also check if query was successful
        try {
            $stmt->execute();
            return 1;
        } catch (Exception $e) {
            return $e->getMessage();;
        }

    }

    public function getLastMenu($restaurant_id)
    {
        $query = "Select id from $this->table 
                    where  restaurant_id = ? 
                    order by id desc LIMIT  1";

        $stmt = $this->conn->prepare($query);
        $stmt->bindParam(1, $restaurant_id);
        try {
            $stmt->execute();
            $num = $stmt->rowCount();
            if ($num > 0) {
                $row = $stmt->fetch(PDO::FETCH_ASSOC);
                $id = $row['id'];
                return json_encode(array(
                    "id" => $id,
                    "flag" => 1
                ));
            } else {
                return json_encode(array(
                    "error" => "No data found!",
                    "flag" => -1
                ));
            }
        } catch (Exception $e) {
            return json_encode(array(
                "error" => $e->getMessage(),
                "flag" => 0
            ));
        }
    }

    function getMenus($rest_id)
    {
        $query = "Select id from $this->table where restaurant_id = ?";
        $stmt = $this->conn->prepare($query);
        $stmt->bindParam(1, $rest_id);
        $stmt->execute();
        return $stmt;
    }

    function deleteMenu($id)
    {
        $query = "Delete from " . $this->table
            . " where id = $id";
        $stmt = $this->conn->prepare($query);
        try {
            $stmt->execute();
            return true;
        } catch (Exception $e) {
            return $e->getMessage();
        }
    }
}