<?php

class Category
{
    private  $conn;
    private  $table = 'category';
    private  $dir ;
    private  $server_ip;
    private  $server_dir;
    //category Prop

    /**
     *id        INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
     * name      VARCHAR(50),
     * menu_id   INTEGER NOT NULL,
     *  cat_img   VARCHAR(50)
     */

    private  $id;
    private  $name;
    private  $menu_id;
    private  $cat_image;

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
        $this->dir = "http://" . $this->server_ip . "/HI-Food/API/Controller/CategoryController/imageAsset/";
        $this->server_dir = "/HI-Food/API/Controller/CategoryController/imageAsset/";
    }

    function createCategory($data, $menu_id)
    {
        $imageName = $data->name . "_" . $data->cat_image . "_" . $menu_id . ".jpg";
        // insert query
        $query = "INSERT INTO " . $this->table . "
            SET
                 name = ?,menu_id = ?, cat_img = ?";

        $stmt = $this->conn->prepare($query);
        $destination_folder = $_SERVER['DOCUMENT_ROOT'];
        $imagePath = $destination_folder . $this->server_dir . $imageName;
        // bind the values
        $stmt->bindParam(1, $data->name);
        $stmt->bindParam(2, $menu_id);
        $stmt->bindParam(3, $imageName);

        // execute the query, also check if query was successful
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

    public function getLastCategory($menu_id)
    {
        $query = "Select id from $this->table 
                    where  menu_id = $menu_id
                    order by id desc LIMIT  1";
        $stmt = $this->conn->prepare($query);
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
            }
        } catch (Exception $e) {
            return json_encode(array(
                "error" => $e->getMessage(),
                "flag" => 0
            ));
        }
    }

    function getCategories($menu_id)
    {
        $query = "Select * from $this->table
        where menu_id  = ?";
        $stmt = $this->conn->prepare($query);
        $stmt->bindParam(1, $menu_id);
        $stmt->execute();
        return $stmt;
    }

    function delete($cat_id)
    {
        $query = "Delete from " . $this->table
            . " where id = $cat_id";
        $stmt = $this->conn->prepare($query);
        try {
            $stmt->execute();
            return true;
        } catch (Exception $e) {
            return $e->getMessage();
        }
    }

    function updateCategory($data)
    {
        $query = "UPDATE  $this->table 
        SET name = ?, cat_img = ?
        WHERE id = $data->cat_id";

        // prepare the query
        $stmt = $this->conn->prepare($query);

        $stmt->bindParam(1, $data->name);
        $stmt->bindParam(2, $data->cat_img);
        $stmt->bindParam(3, $data->cat_id);

        // execute the query, also check if query was successful
        try {
            $stmt->execute();
            return true;
        } catch (Exception $e) {
            return $e->getMessage();
        }
    }
}