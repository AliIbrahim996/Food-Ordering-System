<?php


class Table
{
    private  $conn;
    private  $table = 'restaurent_tables';
    private  $server_ip;
    private  $dir;
    private  $server_dir;
    //   private string $dir = "API/Controller/TableController/imageAsset";
    //private string $server_dir = "C:/xampp/htdocs/TableController/imageAsset";
    /**
     *id                   INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
     * restaurant_id        INTEGER NOT NULL,
     * table_number         INTEGER,
     * table_status         INTEGER,
     * table_number_seats   INTEGER,
     * table_location       VARCHAR(50)
     *
     */

    private $id;
    private $restaurant_id;
    private $table_number;
    private $table_status;
    private $table_number_seats;
    private $table_location;


    public function getDir(): string
    {
        return $this->dir;

    }

    public function __construct($db)
    {
        $this->conn = $db;
        $this->server_ip = getHostByName(getHostName());
        $this->dir = "http://" . $this->server_ip . "/HI-Food/API/Controller/TableController/imageAsset/";
        $this->server_dir = "/HI-Food/API/Controller/TableController/imageAsset/";
    }

    function createTable($data)
    {
        $imageName = $data->restaurant_id . "_" . $data->table_number . ".jpg";
        // insert query
        $query = "INSERT INTO restaurent_tables
            SET
                restaurant_id = ?,
                table_number = ?, 
                table_status = ?,
                table_number_seats = ?,
                table_location = ?, 
                table_img = ?";


        $destination_folder = $_SERVER['DOCUMENT_ROOT'];
        $imagePath = $destination_folder . $this->server_dir . $imageName;
        $stmt = $this->conn->prepare($query);
        $stmt->bindParam(1, $data->restaurant_id);
        $stmt->bindParam(2, $data->table_number);
        $stmt->bindParam(3, $data->table_status);
        $stmt->bindParam(4, $data->table_number_seats);
        $stmt->bindParam(5, $data->table_location);
        $stmt->bindParam(6, $imageName);
        // execute the query, also check if query was successful
        try {
            if ($stmt->execute()) {
                $handle = fopen($imagePath, "w");
                fwrite($handle, base64_decode($data->ImageData));
                fclose($handle);
                //201 created
                http_response_code(201);
                return json_encode(array(
                    "message" => "Table created successful",
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

    function getTables($rest_id)
    {
        $query = "Select * from  $this->table where restaurant_id = ? ";
        $stmt = $this->conn->prepare($query);
        $stmt->bindParam(1, $rest_id);
        $stmt->execute();
        return $stmt;
    }

    function delete($table_id)
    {

        $query = "Delete from " . $this->table
            . "  where id = ?";

        $stmt = $this->conn->prepare($query);
        $stmt->bindParam(1,$table_id);
        try {
            $stmt->execute();
            return true;
        } catch (Exception $e) {
            return $e->getMessage();
        }
    }

    private function setTableInfo($table_id)
    {
        $query = "select * from " . $this->table .
            " where id = ?";
        $stmt = $this->conn->prepare($query);
        $stmt->bindParam(1, $table_id);
        $stmt->execute();
        $num = $stmt->rowCount();
        if ($num > 0) {
            $row = $stmt->fetch(PDO::FETCH_ASSOC);
            $this->id = $row['id'];
            $this->restaurant_id = $row['restaurant_id'];
            $this->table_number = $row['table_number'];
            $this->table_status = $row['table_status'];
            $this->table_location = $row['table_location'];
            $this->table_number_seats = $row['table_number_seats'];
            return true;
        } else {
            return false;
        }
    }

    public function updateTableStatus($data)
    {

        $query = "UPDATE $this->table 
        SET  table_status = ?
        WHERE id = ?";

        $stmt = $this->conn->prepare($query);
        $stmt->bindParam(1, $data->table_status);
        $stmt->bindParam(2, $data->restaurent_tables_id);
        try {
            $stmt->execute();
            return 1;
        } catch (Exception $e) {
            return $e->getMessage();
        }
    }

    function updateTableContent($data)
    {
        $imageName = $data->restaurant_id . "_" . $data->table_number . ".jpg";
        $destination_folder = $_SERVER['DOCUMENT_ROOT'];
        $imagePath = $destination_folder . $this->server_dir . $imageName;

        $query = "UPDATE $this->table 
        SET   table_number_seats = ?,table_location = ?,table_img = ?
        WHERE id = ? ";
        // prepare the query
        $stmt = $this->conn->prepare($query);

        $stmt->bindParam(1, $data->table_number_seats);
        $stmt->bindParam(2, $data->table_location);
        $stmt->bindParam(3, $imageName);
        $stmt->bindParam(4, $data->table_id);
        // execute the query, also check if query was successful
        try {
            $stmt->execute();
            $handle = fopen($imagePath, "w");
            fwrite($handle, base64_decode($data->ImageData));
            fclose($handle);
            return 1;
        } catch (Exception $e) {
            return $e->getMessage();
        }
    }


    public function getTableInfo($table_id)
    {
        if ($this->setTableInfo($table_id)) {
            http_response_code(200);
            return json_encode(array(
                    "id" => $this->id,
                    "restaurant_id" => $this->restaurant_id,
                    "table_number" => $this->table_number,
                    "table_location" => $this->table_location,
                    "table_number_seats" => $this->table_number_seats,
                    "table_status" => $this->table_status,
                )
            );
        } else {
            http_response_code(404);
            return json_encode(array(
                "message" => "No data found",
                "flag" => 0
            ));
        }
    }

    public function getTableStatus($table_id)
    {

        if ($this->setTableInfo($table_id)) {
            return json_encode(array(
                "table_status" => $this->table_status,
                "flag" => 1
            ));
        } else {
            return json_encode(array(
                "message" => "No data found",
                "flag" => -1
            ));
        }
    }

    public function getTableNumer($id){
        $q = "Select table_number from $this->table where id = ?";
        $stmt = $this->conn->prepare($q);
        $stmt->bindParam(1,$id);
        $stmt->execute();
        if($stmt->rowCount()>0){
            return $stmt->fetch(PDO::FETCH_ASSOC)['table_number'];
        }
        else{
            return 0;
        }
    }
}
