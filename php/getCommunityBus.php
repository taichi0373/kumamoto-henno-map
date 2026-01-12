<?php
// DB接続ファイルを読み込む
include 'db_connection.php';

function GetAgencyName($conn, $route_id)
{
    // データベースからユーザー情報を取得
    $sql = "SELECT * FROM community_bus WHERE route_id = '$route_id'";
    $result = $conn->query($sql);

    if ($result->num_rows > 0) {
        $row = $result->fetch_assoc();
        return array("agency_id"  => $row['agency_id'], "business_name" => $row['business_name'], "trip_name" => $row['trip_name']);
    } else {
        return null;
    }
}


// POSTリクエストの場合
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $conn = connectToDatabase($servername, $username, $password, $dbname);
    header('Content-Type: application/json');
    $route_id = $_POST['route_id'];

    $agency_name = GetAgencyName($conn, $route_id);
    if ($agency_name != null) {
        echo json_encode(array("success" => true, "data" => $agency_name));
    } else {
        echo json_encode(array("success" => false, "message" => "コミュニティバスが見つかりません"));
    }

    // 接続を閉じる
    $conn->close();
}
