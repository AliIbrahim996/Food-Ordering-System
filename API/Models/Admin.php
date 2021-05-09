<?php

class Admin
{
    private $conn;
    private $table = 'admin';
    private $server_ip;
    private $dir;
    private $server_dir;
    /**
     * id          INTEGER NOT NULL,
     * full_name   NVARCHAR(50),
     * email       VARCHAR(50),
     * password    NVARCHAR(50),
     * gender      NVARCHAR(50),
     * age         NVARCHAR(1),
     * aimg        NVARCHAR(50)
     */
    public $id;
    public $full_name;
    public $gender;
    public $age;
    public $email;
    public $password;
    public $aimg;

    /**
     * @return string
     */
    public function getDir(): string
    {
        return $this->dir;
    }


    public function __construct(PDO $db)
    {
        $this->conn = $db;
        $this->server_ip = getHostByName(getHostName());
        $this->dir = "http://" . $this->server_ip . "/HI-Food/API/Controller/AdminController/imageAsset/";
        $this->server_dir = "/HI-Food/API/Controller/AdminController/imageAsset/";

    }

    function registerAdmin($data)
    {
        // insert query
        $query = "INSERT INTO admin 
            SET
                full_name = ?,
                gender = ?,
                email = ?,
                password = ?,
                aimg = ?,
                age = ?
               ";

        $imageName = $data->image_name;
        $stmt = $this->conn->prepare($query);
        $destination_folder = $_SERVER['DOCUMENT_ROOT'];
        $imagePath = $destination_folder . $this->server_dir . $imageName;
        $handle = fopen($imagePath, "w");
        $password = password_hash($data->password, PASSWORD_BCRYPT);
        // bind the values
        $stmt->bindParam(1, $data->full_name);
        $stmt->bindParam(2, $data->gender);
        $stmt->bindParam(3, $data->email);
        $stmt->bindParam(4, $password);
        $stmt->bindParam(5, $imageName);
        $stmt->bindParam(6, $data->age);
        // execute the query, also check if query was successful
        try {
            fwrite($handle, base64_decode($data->ImageData));
            fclose($handle);
            // file_put_contents($imagePath,);
            if ($stmt->execute()) {

                //201 created
                http_response_code(201);
                return json_encode(array(
                    "message" => "Admin registered successful",
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

    function getAdmins()
    {
        $query = "Select * from " . $this->table;
        $stmt = $this->conn->prepare($query);
        $stmt->execute();
        return $stmt;
    }

    public function setEmail($email)
    {
        $this->email = $email;
        $this->id = htmlspecialchars(strip_tags($this->email));
    }

    function delete($email)
    {
        $this->setEmail($email);
        $query = "Delete from " . $this->table
            . " where email = $this->email";
        $stmt = $this->conn->prepare($query);
        try {
            $stmt->execute();
            return true;
        } catch (Exception $e) {
            return $e->getMessage();
        }
    }

    private function setAdminInfo($email)
    {
        $query = "select * from " . $this->table .
            " where email = ?";
        $this->email = $email;
        $this->email = htmlspecialchars(strip_tags($this->email));
        $stmt = $this->conn->prepare($query);
        $stmt->bindParam(1, $email);
        $stmt->execute();
        $num = $stmt->rowCount();
        if ($num > 0) {
            $row = $stmt->fetch(PDO::FETCH_ASSOC);
            $this->id = $row['id'];
            $this->full_name = $row['full_name'];
            $this->email = $row['email'];
            $this->password = $row['password'];
            $this->aimg = $row['aimg'];
            $this->age = $row['age'];
            return true;
        } else {
            return false;
        }
    }

    function updateAdmin($data)
    {
        // insert query
        $query = "UPDATE admin 
        SET full_name = ?, gender = ?, email = ?, password = ?, aimg = ?, age = ?
        WHERE email = ? ";
        $stmt = $this->conn->prepare($query);
        $password = $this->emailExists($data->email) ? $this->password : password_hash($data->password, PASSWORD_BCRYPT);
        // bind the values
        $stmt->bindParam(1, $data->full_name);
        $stmt->bindParam(2, $data->gender);
        $stmt->bindParam(3, $data->email);
        $stmt->bindParam(4, $password);
        $stmt->bindParam(5, $data->aimg);
        $stmt->bindParam(6, $data->age);
        $stmt->bindParam(7, $data->email);
        // execute the query, also check if query was successful
        try {
            $stmt->execute();
            return 1;
        } catch (Exception $e) {
            return $e->getMessage();
        }
    }


    public function getAdminInfo($email)
    {
        if ($this->setAdminInfo($email)) {
            http_response_code(200);
            return json_encode(array(
                    "Admin" => array(
                        "id" => $this->id,
                        "email" => $this->email,
                        "password" => $this->password,
                        "full_name" => $this->full_name,
                        "age" => $this->age,
                        "admin_image" => "$this->dir/$this->aimg",
                    ),
                    "flag" => 1
                )
            );
        } else {
            http_response_code(404);
            return json_encode(array(
                "message" => "No data found",
                "flag" => -1
            ));
        }
    }


    public function adminLogIn($email, $password)
    {
        if ($this->emailExists($email)) {
            //check for password
            if (password_verify($password, $this->password)) {
                //
                http_response_code(200);
                return json_encode(array(
                    "message" => "successfully logged in",
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
                    "message" => "Admin not found! check your email",
                    "flag" => 0
                )
            );
        }
    }

    private function emailExists($email)
    {

        // query to check if email exists
        $query = "SELECT password
            FROM admin
            WHERE email = ?
            LIMIT 0,1";

        // prepare the query
        $stmt = $this->conn->prepare($query);
        $this->email = $email;
        $this->email = htmlspecialchars(strip_tags($this->email));
        // bind value
        $stmt->bindParam(1, $this->email);
        // execute the query
        $stmt->execute();
        // get number of rows
        $num = $stmt->rowCount();
        if ($num > 0) {
            //set password
            $row = $stmt->fetch(PDO::FETCH_ASSOC);
            $this->password = $row['password'];
            // return true because email exists in the database
            return true;
        }
        // return false if email does not exist in the database
        return false;
    }
}