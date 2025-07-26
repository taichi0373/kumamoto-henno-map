<?php
// DB接続ファイルを読み込む
include 'db_connection.php';

function GetStore($conn)
{
    $sql = "
    SELECT 
        store_condition.*, 
        store_benefit.*,
        municipality.name AS municipality_name
    FROM 
        store_condition
    JOIN 
        store_benefit ON store_condition.store_benefit_id = store_benefit.id
    JOIN 
        municipality ON store_condition.address = municipality.id
";
    // データベースからユーザー情報を取得
    $result = $conn->query($sql);

    if ($result->num_rows > 0) {
        $stores = array();
        while ($row = $result->fetch_assoc()) {
            $stores[] = array(
                "id" => $row['id'],
                "name" => $row['name'],
                "detail" => $row['detail'],
                "tel_number" => $row['tel_number'],
                "latitude" => $row['latitude'],
                "longitude" => $row['longitude'],
                "municipality_name" => $row['municipality_name'],
                "license_status" => $row['license_status'],
                "min_age" => $row['min_age'],
                "max_age" => $row['max_age'],
                "note" => $row['note']
            );
        }
        return $stores;
    } else {
        return null;
    }
}

// GETリクエストの場合
if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    $conn = connectToDatabase($servername, $username, $password, $dbname);
    header('Content-Type: application/json');

    $stores = GetStore($conn);
    if ($stores != null) {
        echo json_encode(array("success" => true, "stores" => $stores));
    } else {
        echo json_encode(array("success" => false, "message" => "店舗が見つかりません"));
    }

    // 接続を閉じる
    $conn->close();
}
