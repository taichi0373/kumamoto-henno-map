<?php
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "navi_project";
function connectToDatabase($servername, $username, $password, $dbname)
{
    $conn = new mysqli($servername, $username, $password, $dbname);
    if ($conn->connect_error) {
        die(json_encode(array("status" => 'error', "message" => "接続に失敗しました: " . $conn->connect_error)));
    }
    return $conn;
}
