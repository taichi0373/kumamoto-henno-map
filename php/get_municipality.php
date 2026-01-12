<?php
// DB接続ファイルを読み込む
include 'db_connection.php';

function GetMunicipality($conn)
{
    $sql = "SELECT * FROM municipality WHERE LENGTH(id) = 6;";
    // データベースからユーザー情報を取得
    $result = $conn->query($sql);

    if ($result->num_rows > 0) {
        $municipalities = array();
        while ($row = $result->fetch_assoc()) {
            $municipalities[] = array(
                "id" => $row['id'],
                "name" => $row['name'],
                "name_kana" => $row['name_kana']
            );
        }
        return $municipalities;
    } else {
        return null;
    }
}

// GETリクエストの場合
if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    $conn = connectToDatabase($servername, $username, $password, $dbname);
    header('Content-Type: application/json');

    $municipalities = GetMunicipality($conn);
    if ($municipalities != null) {
        echo json_encode(array("success" => true, "municipalities" => $municipalities));
    } else {
        echo json_encode(array("success" => false, "message" => "自治体が見つかりません"));
    }

    // 接続を閉じる
    $conn->close();
}
