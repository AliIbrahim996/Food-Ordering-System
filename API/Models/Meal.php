<?php

class Meal
{
    /**
     *
     * `id` int(11) NOT NULL,
     * `name` varchar(255) DEFAULT NULL,
     * `calories` varchar(255) DEFAULT NULL,
     * `price` varchar(255) DEFAULT NULL,
     * `img1` varchar(255) DEFAULT NULL,
     * `category_id` int(11) NOT NULL
     */
    private  $conn;
    private  $table = 'meal';
    private  $dir;
    private  $server_ip;
    private  $server_dir;
    //Meal Prop
    private  $id;
    private  $name;
    private  $calories;
    private  $price;
    private  $img;
    private  $category_id;

    public function __construct(PDO $db)
    {
        $this->conn = $db;
        $this->server_ip = getHostByName(getHostName());
        $this->dir = "http://" . $this->server_ip . "/HI-Food/API/Controller/RestaurantManagerController/imageAsset/";
        $this->server_dir = "/HI-Food/API/Controller/RestaurantManagerController/imageAsset/";
    }

    public function getDir()
    {
        return $this->dir;

    }

    public function addMeal($data)
    {

        $query = "Insert into meal
        SET
        name = ?,
        calories = ?,
        price = ?,
        img1 = ?, 
        category_id = ? ";
        $imageName = $data->meal_image . ".jpg";
        $destination_folder = $_SERVER['DOCUMENT_ROOT'];
        $imagePath = $destination_folder . $this->server_dir . $imageName;
        $stmt = $this->conn->prepare($query);
        $stmt->bindParam(1, $data->meal_name);
        $stmt->bindParam(2, $data->calories);
        $stmt->bindParam(3, $data->price);
        $stmt->bindParam(4, $imageName);
        $stmt->bindParam(5, $data->category_id);
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

    public function updateMeal($data)
    {
        $query = "Update $this->table 
        SET name = ?,calories = ?, price = ?, img1 = ?, category_id = ? 
             where id = ?";
        $imageName = $data->meal_image . ".jpg";
        $destination_folder = $_SERVER['DOCUMENT_ROOT'];
        $imagePath = $destination_folder . $this->server_dir . $imageName;
        $stmt = $this->conn->prepare($query);
        $stmt->bindParam(1, $data->meal_name);
        $stmt->bindParam(2, $data->calories);
        $stmt->bindParam(3, $data->price);
        $stmt->bindParam(4, $imageName);
        $stmt->bindParam(5, $data->category_id);
        $stmt->bindParam(6, $data->meal_id);
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

    public function deleteMeal($meal_id)
    {
        $query = "Delete from $this->table 
        where id = ?";
        $stmt = $this->conn->prepare($query);
        $stmt->bindParam(1, $meal_id);
        try {
            $stmt->execute();
            return true;
        } catch (Exception $e) {
            return $e->getMessage();;
        }
    }

    /**
     * @param $category_id
     * @return false|PDOStatement
     */
    public function getAllMeals($category_id)
    {
        $query = "select * from $this->table where category_id = ? ";
        $stmt = $this->conn->prepare($query);
        $stmt->bindParam(1, $category_id);
        $stmt->execute();
        return $stmt;
    }

    public function getMealName($meal_id)
    {
        $query = "select name from $this->table where id = ? ";
        $stmt = $this->conn->prepare($query);
        $stmt->bindParam(1, $meal_id);
        $stmt->execute();
        $row = $stmt->fetch(PDO::FETCH_ASSOC);
        return $row['name'];
    }

    public function getMealPrice($meal_id)
    {
        $query = "select price from $this->table where id = ? ";
        $stmt = $this->conn->prepare($query);
        $stmt->bindParam(1, $meal_id);
        $stmt->execute();
        $row = $stmt->fetch(PDO::FETCH_ASSOC);
        return $row['price'];
    }

}