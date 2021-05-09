<?php

class Restaurant
{
    private $conn;
    private  $table = 'restaurant';
    private  $server_ip;
    private  $dir;
    private  $server_dir;
    /**
     * id                    INTEGER NOT NULL,
     * restaurant_name       NVARCHAR(50),
     * restaurant_address    NVARCHAR(50),
     * restaurant_phone_no   NVARCHAR(50),
     * manager_name          NVARCHAR(50),
     * manager_email         NVARCHAR(50),
     * manager_password      NVARCHAR(50),
     * managerimg            NVARCHAR(50),
     * admin_id              INTEGER NOT NULL,
     * is_accepted           INTEGER,
     * restaurent_img        NVARCHAR(50),
     * open_at               NVARCHAR(50),
     * close_at              NVARCHAR(50)
     */
    //Restaurant Prop
    private $id;
    private  $restaurant_name;
    private  $restaurant_address;
    private  $restaurant_phone_no;
    private  $manager_name;
    private  $manager_email;
    private  $manager_password;
    private  $managerimg;
    private $admin_id;
    private $is_accepted;
    private  $restaurent_img;
    private  $open_at;
    private  $close_at;

    /**
     * @return string
     */
    public function getDir()
    {
        return $this->dir;
    }

    public function __construct(PDO $db)
    {
        $this->conn = $db;
        $this->server_ip = getHostByName(getHostName());
        $this->dir = "http://" . $this->server_ip . "/HI-Food/API/Controller/RestaurantController/imageAsset/";
        $this->server_dir = "/HI-Food/API/Controller/RestaurantController/imageAsset/";
    }

    function createRestaurant($data)
    {
        // insert query
        $query = "INSERT INTO " . $this->table . "
            SET
               restaurant_name = ?,
               restaurant_address = ?,
               restaurant_phone_no = ?,
               admin_id = ?,is_accepted = ?,restaurent_img = ?,open_at = ?,close_at = ?
               ";

        $imageName = $data->restaurent_img;
        $stmt = $this->conn->prepare($query);
        $destination_folder = $_SERVER['DOCUMENT_ROOT'];
        $imagePath = $destination_folder . $this->server_dir . $imageName;
        // bind the values
        $stmt->bindParam(1, $data->restaurant_name);
        $stmt->bindParam(2, $data->restaurant_address);
        $stmt->bindParam(3, $data->restaurant_phone_no);
        $stmt->bindParam(4, $data->admin_id);
        $stmt->bindParam(5, $data->is_accepted);
        $stmt->bindParam(6, $imageName);
        $stmt->bindParam(7, $data->open_at);
        $stmt->bindParam(8, $data->close_at);
        try {

            $stmt->execute();
            $handle = fopen($imagePath, "w");
            fwrite($handle, base64_decode($data->ImageData));
            fclose($handle);
            //201 created
            return 1;
        } catch (Exception $e) {
            return $e->getMessage();;
        }

    }

    function getRestaurants()
    {
        $query = "Select * from " . $this->table;
        $stmt = $this->conn->prepare($query);
        $stmt->execute();
        return $stmt;
    }

    public function setManagerEmail($manager_email)
    {
        $this->manager_email = $manager_email;
        $this->id = htmlspecialchars(strip_tags($this->manager_email));
    }

    function delete($email)
    {
        $this->setManagerEmail($email);
        $query = "Delete from " . $this->table
            . " where email = $this->manager_email";
        $stmt = $this->conn->prepare($query);
        try {
            $stmt->execute();
            return true;
        } catch (Exception $e) {
            return $e->getMessage();
        }
    }

    function updateStatus($data)
    {
        $q = "update $this->table set  is_accepted = ? where id = ? ";
        $stmt = $this->conn->prepare($q);
        $stmt->bindParam(1, $data->status);
        $stmt->bindParam(2, $data->rest_id);
        try {
            $stmt->execute();
            http_response_code(200);
            echo json_encode(array("message" => "restaurant status updated", "flag" => 1));
        } catch (Exception $e) {
            http_response_code(401);
            echo json_encode(array("message" => "something went wrong! " . $e->getMessage(), "flag" => 0));
        }
    }

    private function setRestaurantInfo($rest_name)
    {
        $query = "select * from " . $this->table .
            " where restaurant_name = ?";
        $stmt = $this->conn->prepare($query);
        $stmt->bindParam(1, $rest_name);
        $stmt->execute();
        $num = $stmt->rowCount();
        if ($num > 0) {
            $row = $stmt->fetch(PDO::FETCH_ASSOC);
            $this->restaurant_name = $row['restaurant_name'];
            $this->restaurant_address = $row['restaurant_address'];
            $this->restaurant_phone_no = $row['restaurant_phone_no'];
            $this->admin_id = $row['admin_id'];
            $this->is_accepted = $row['is_accepted'];
            $this->restaurent_img = $row['restaurent_img'];
            $this->open_at = $row['restaurant_name'];
            $this->close_at = $row['restaurant_name'];
            $this->manager_email = $row['manager_email'];
            $this->manager_password = $row['manager_password '];
            $this->manager_name = $row['manager_name '];
            $this->managerimg = $row['managerimg'];
            return true;
        } else {
            return false;
        }
    }

    function updateRestaurant($data)
    {
        // insert query
        $query = "UPDATE $this->table
            SET    restaurant_name = ?,restaurant_address = ?,restaurant_phone_no = ?,
            is_accepted = ?,restaurent_img = ?,open_at = ?,close_at = ?
             WHERE  id=? ";
        // prepare the query
        $imageName = $data->restaurent_img;
        $destination_folder = $_SERVER['DOCUMENT_ROOT'];
        $stmt = $this->conn->prepare($query);
        $imagePath = $destination_folder . $this->server_dir . $imageName;
        // bind the values
        $stmt->bindParam(1, $data->restaurant_name);
        $stmt->bindParam(2, $data->restaurant_address);
        $stmt->bindParam(3, $data->restaurant_phone_no);

        $stmt->bindParam(4, $data->is_accepted);
        $stmt->bindParam(5, $imageName);
        $stmt->bindParam(6, $data->open_at);
        $stmt->bindParam(7, $data->close_at);
        $stmt->bindParam(8, $data->id);
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

    public function getRestaurantInfo($rest_name)
    {
        if ($this->setRestaurantInfo($rest_name)) {
            http_response_code(200);
            return json_encode(array(
                    "Restaurant" => array(
                        'restaurant_name' => $this->restaurant_name,
                        'restaurant_address' => $this->restaurant_address,
                        'restaurant_phone_no' => $this->restaurant_phone_no,
                        'restaurant_imgUrl' => "http://" . getHostByName(getHostName()) .
                            "/" . $this->dir . "/$this->restaurent_img",
                        'admin_id' => $this->admin_id,
                        'is_accepted' => $this->is_accepted,
                        'open_at' => $this->open_at,
                        'cloe_at' => $this->close_at,
                        'manager_info' => array(
                            "manager_name" => $this->manager_name,
                            "manger_email" => $this->manager_email,
                            "manger_password" => $this->manager_password,
                            "manager_img" => $this->managerimg
                        ),
                        "flag" => 1
                    )
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

    public function managerLogIn($email, $password)
    {
        if ($this->emailExists($email)) {
            //check for password
            if (password_verify($password, $this->manager_password)) {
                //
                http_response_code(200);
                return json_encode(array(
                    "message" => "successfully logged in",
                    "full_name" => $this->manager_name,
                    "is_accepted" => $this->is_accepted,
                    "flag" => 1
                ));
            } else {
                http_response_code(401);
                return json_encode(
                    array(
                        "message" => "Unauthorized! password error",
                        "flag" => -1
                    )
                );
            }
        } else {
            http_response_code(404);
            return json_encode(
                array(
                    "message" => "manager not found! check your email",
                    "flag" => 0
                )
            );
        }
    }

    private function emailExists($email)
    {

        // query to check if email exists
        $query = "SELECT manager_password,manager_name,is_accepted,admin_id
            FROM " . $this->table . "
            WHERE manager_email = ?
            LIMIT 0,1";

        // prepare the query
        $stmt = $this->conn->prepare($query);
        $this->manager_email = $email;
        $this->manager_email = htmlspecialchars(strip_tags($this->manager_email));
        // bind value
        $stmt->bindParam(1, $this->manager_email);
        // execute the query
        $stmt->execute();
        // get number of rows
        $num = $stmt->rowCount();
        if ($num > 0) {
            //set password
            $row = $stmt->fetch(PDO::FETCH_ASSOC);
            $this->manager_password = $row['manager_password'];
            $this->manager_name = $row['manager_name'];
            $this->is_accepted = $row['is_accepted'];
            // return true because email exists in the database

            return true;
        }
        // return false if email does not exist in the database
        return false;
    }

    public function singupManager($data)
    {
        $query = "UPDATE restaurant 
        SET    manager_name = ?, manager_email = ?, manager_password = ?, managerimg = ?   
        WHERE  restaurant_name  = ? ";

        $this->manager_name = $data->manager_name;
        $this->manager_email = $data->manager_email;
        $manager_password = password_hash($data->password, PASSWORD_BCRYPT);
        $imageName = $data->restaurant_name . "_" . $data->manager_img;
        $destination_folder = $_SERVER['DOCUMENT_ROOT'];
        $imagePath = $destination_folder . $this->server_dir . $imageName;

        $stmt = $this->conn->prepare($query);

        $stmt->bindParam(1, $data->manager_name);
        $stmt->bindParam(2, $data->manager_email);
        $stmt->bindParam(3, $manager_password);
        $stmt->bindParam(4, $imageName);
        $stmt->bindParam(5, $data->restaurant_name);

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

    public function getRestaurantNameByMgrEmail($id)
    {
        $query = "select restaurant_name  from " . $this->table .
            " where manager_email = ?";
        $stmt = $this->conn->prepare($query);
        $stmt->bindParam(1, $id);
        $stmt->execute();
        $num = $stmt->rowCount();
        if ($num > 0) {
            $row = $stmt->fetch(PDO::FETCH_ASSOC);
            return $row['restaurant_name'];
        }
    }

    public function getRestaurantNameById($id)
    {
        $query = "select restaurant_name  from  restaurant  where id = ?";
        $stmt = $this->conn->prepare($query);
        $stmt->bindParam(1, $id);
        $stmt->execute();
        $num = $stmt->rowCount();
        if ($num > 0) {
            $row = $stmt->fetch(PDO::FETCH_ASSOC);
            return $row['restaurant_name'];
        }
    }

    public function getRestaurantNames()
    {
        $query = "select restaurant_name  from  $this->table where manager_name is null ";
        $stmt = $this->conn->prepare($query);
        $stmt->execute();
        return $stmt;
    }

    public function getResturantsByAdminId($admin_id)
    {
        $query = "select *  from restaurant where admin_id  = ?";
        $stmt = $this->conn->prepare($query);
        $stmt->bindParam(1, $admin_id);
        $stmt->execute();
        return $stmt;
    }

    public function getManagerImage($email)
    {
        $query = "select managerimg  from " . $this->table .
            " where manager_email = ?";
        $stmt = $this->conn->prepare($query);
        $stmt->bindParam(1, $email);
        try {
            $stmt->execute();
            $num = $stmt->rowCount();
            if ($num > 0) {
                $row = $stmt->fetch(PDO::FETCH_ASSOC);
                http_response_code(200);
                return json_encode(array(
                    "imageUrl" => $this->dir . $row['managerimg']
                ));
            } else {
                http_response_code(404);
                return json_encode(array(
                    "imageUrl" => "No data found"
                ));
            }
        } catch (Exception $e) {
            http_response_code(400);
            return json_encode(array(
                "imageUrl" => "error!  " . $e->getMessage()
            ));
        }

    }

    public function getId($email)
    {
        $query = "select id  from " . $this->table .
            " where manager_email = ?";
        $stmt = $this->conn->prepare($query);
        $stmt->bindParam(1, $email);
        try {
            $stmt->execute();
            $num = $stmt->rowCount();
            if ($num > 0) {
                $row = $stmt->fetch(PDO::FETCH_ASSOC);
                return $row['id'];
            } else {
                return 0;
            }
        } catch (Exception $e) {
            return $e->getMessage();
        }
    }

    public function getAdminRest($email)
    {
        $query = "select id  from " . $this->table .
            " where admin_id = ?";
        $stmt = $this->conn->prepare($query);
        $stmt->bindParam(1, $email);
        $stmt->execute();
        return $stmt;
    }

    public function getAdminRestMGR($email)
    {
        $query = "select manager_email  from " . $this->table .
            " where admin_id = ?";
        $stmt = $this->conn->prepare($query);
        $stmt->bindParam(1, $email);
        $stmt->execute();
        return $stmt;
    }
}