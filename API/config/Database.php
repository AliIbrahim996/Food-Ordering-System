<?php


class Database
{
    //DB parms
    private  $host;
    private  $db_name;
    private  $username;
    private  $password;

    //DB Conn

    /**
     * Database constructor.
     */
    public function __construct()
    {
        $this->host = 'localhost';
        $this->db_name = 'hi_food';
        $this->username = 'root';
        $this->password = '';
    }

    /**
     * @return PDO
     */

    public function connect(): PDO
    {
        $conn = null;
        try {
            $conn = new PDO('mysql:127.0.0.1=localhost;port=3306;dbname=' . $this->db_name,
                $this->username, $this->password);
            $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
        } catch (PDOException $e) {
            echo 'Connection Error: ' . $e->getMessage();
        }

        return $conn;
    }
}