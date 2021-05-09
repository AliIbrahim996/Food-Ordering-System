<?php

class Customer
{
    private  $conn;
    private  $table = 'customer';
    private  $server_ip;
    private  $dir;
    private  $server_dir;
    //   private string $dir = "API/Controller/CustomerController/imageAsset";
    //   private string $server_dir = "C:/xampp/htdocs/CustomerController/imageAsset";
    //Customer Prop

    /**
     * id             INTEGER NOT NULL,
     * full_name      NVARCHAR(50),
     * gender         NVARCHAR(5),
     * email          NVARCHAR(50),
     * password       NVARCHAR(50),
     * phone_number   NVARCHAR(10),
     * address        NVARCHAR(50),
     * cimg           NVARCHAR(50)
     */

    private  $id;
    private  $full_name;
    private  $gender;
    private  $address;
    private  $phoneNum;
    private  $email;
    private  $password;
    private  $imgSrc;

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
        $this->dir = "http://" . $this->server_ip . "/HI-Food/API/Controller/CustomerController/imageAsset/";
        $this->server_dir = "/HI-Food/API/Controller/CustomerController/imageAsset/";
    }

    function registerCustomer($data)
    {
        // insert query
        $query = "INSERT INTO " . $this->table . "
            SET
                full_name = ?,
                gender = ?,
                email = ?,
                password = ?,
                phone_number = ?,
                address = ?,
                cimg = ?
               ";
        $imageName = $data->image_name;
        $destination_folder = $_SERVER['DOCUMENT_ROOT'];
        $imagePath = $destination_folder . $this->server_dir . $imageName;
        $handle = fopen($imagePath, "w");
        $stmt = $this->conn->prepare($query);
        // bind the values
        $password = password_hash($data->password, PASSWORD_BCRYPT);
        $stmt->bindParam(1, $data->full_name);
        $stmt->bindParam(2, $data->gender);
        $stmt->bindParam(3, $data->email);
        $stmt->bindParam(4, $password);
        $stmt->bindParam(5, $data->phoneNum);
        $stmt->bindParam(6, $data->address);
        $stmt->bindParam(7, $imageName);
        // execute the query, also check if query was successful
        try {
            $stmt->execute();
            fwrite($handle, base64_decode($data->ImageData));
            fclose($handle);
            //201 created
            http_response_code(201);
            return json_encode(array(
                "message" => "Customer registered successful",
                "flag" => 1));
        } catch (Exception $e) {
            http_response_code(400);
            return json_encode(array(
                "message" => "error: " . $e->getMessage()
            ));
        }

    }

    function getCustomers()
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

    private function setCustomerInfo($email)
    {
        $query = "select * from " . $this->table .
            " where email = ?";
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
            $this->address = $row['address'];
            $this->phoneNum = $row['phone_number'];
            $this->imgSrc = $row['cimg'];
            return true;
        } else {
            return false;
        }
    }

    function updateCustomer($data)
    {
        // insert query
        $query = "UPDATE  $this->table 
        SET full_name = ?, gender = ?, email = ?, password = ?, phone_number = ?,
        address = ?, cimg = ?
        WHERE email = ? ";

        $stmt = $this->conn->prepare($query);

        $password = $this->emailExists($data->email) ? $this->password : password_hash($data->password, PASSWORD_BCRYPT);
        // bind the values
        $stmt->bindParam(1, $data->full_name);
        $stmt->bindParam(2, $data->gender);
        $stmt->bindParam(3, $data->email);
        $stmt->bindParam(4, $password);
        $stmt->bindParam(5, $data->phoneNum);
        $stmt->bindParam(6, $data->address);
        $stmt->bindParam(7, $data->imgSrc);
        $stmt->bindParam(8, $data->email);
        // execute the query, also check if query was successful
        try {
            $stmt->execute();
            return 1;
        } catch (Exception $e) {
            return $e->getMessage();
        }
    }

    public function getCustomerInfo($email)
    {
        if ($this->setCustomerInfo($email)) {
            http_response_code(200);
            return json_encode(array(
                    "id" => $this->id,
                    "email" => $this->email,
                    "password" => $this->password,
                    "full_name" => $this->full_name,
                    "address" => $this->address,
                    "phone_number" => $this->phoneNum,
                    "c_image" => "http://" . getHostByName(getHostName()) . "/$this->dir/$this->imgSrc"

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

    public function customerLogIn($email, $password)
    {
        if ($this->emailExists($email)) {
            //check for password
            if (password_verify($password, $this->password)) {
                //
                http_response_code(200);
                return json_encode(array(
                    "message" => "successfully logged in",
                    "full_name" => $this->full_name,
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
                    "message" => "Customer not found! check your email",
                    "flag" => 0
                )
            );
        }
    }

    private function emailExists($email)
    {

        // query to check if email exists
        $query = "SELECT password,full_name
            FROM " . $this->table . "
            WHERE email = ?
            LIMIT 0,1";

        // prepare the query
        $stmt = $this->conn->prepare($query);
        // bind value
        $stmt->bindParam(1, $email);
        // execute the query
        $stmt->execute();
        // get number of rows
        $num = $stmt->rowCount();
        if ($num > 0) {
            //set password
            $row = $stmt->fetch(PDO::FETCH_ASSOC);
            $this->password = $row['password'];
            $this->full_name = $row['full_name'];
            // return true because email exists in the database

            return true;
        }
        // return false if email does not exist in the database
        return false;
    }

    public function getCustomerInfoById($id)
    {
        $query = "select * from   $this->table where email = ?";
        $stmt = $this->conn->prepare($query);
        $stmt->bindParam(1, $id);
        $stmt->execute();
        $num = $stmt->rowCount();
        if ($num > 0) {
            $row = $stmt->fetch(PDO::FETCH_ASSOC);
            http_response_code(200);
            return json_encode(array(
                    "id" => $row['id'],
                    "email" => $row['email'],
                    "password" => $row['password'],
                    "full_name" => $row['full_name'],
                    "address" => $row['address'],
                    "phone_number" => $row['phone_number'],
                    "c_image" => "http://" . getHostByName(getHostName()) . "/$this->dir/" . $row['cimg']
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

    public function getCustomerImage($email)
    {
        $query = "select cimg  from customer where email = ?";

        $stmt = $this->conn->prepare($query);
        $stmt->bindParam(1, $email);
        try {
            $stmt->execute();
            $num = $stmt->rowCount();
            if ($num > 0) {
                $row = $stmt->fetch(PDO::FETCH_ASSOC);
                http_response_code(200);
                return json_encode(array(
                    "imageUrl" => $this->dir . $row['cimg']
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

    public function getId( $customer_id)
    {
        $query = "select id  from customer where email = ?";
        $stmt = $this->conn->prepare($query);
        $stmt->bindParam(1, $customer_id);
        $stmt->execute();
        if($stmt->rowCount()>0){
            return $stmt->fetch(PDO::FETCH_ASSOC)['id'];
        }
        else{
            return  0;
        }

    }
}