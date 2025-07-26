<?php
// 入力されたユーザ情報から特典を取得するAPI（特典を探すページ）

// DB接続ファイルを読み込む
include 'db_connection.php';

// 特典情報取得関数
function GetBenefits($conn, $age, $LicenseStatus, $addressCode)
{
    $sql = "
    SELECT b.*, c.*, m.name as municipality_name
    FROM benefits b
    LEFT JOIN conditions c ON b.id = c.benefit_id
    LEFT JOIN municipality m ON c.address = m.id
    WHERE 
        (c.min_age IS NULL OR c.min_age <= $age) 
        AND (c.max_age IS NULL OR c.max_age = '' OR c.max_age >= $age)
        AND (c.license_status IS NULL OR c.license_status = '' OR c.license_status = 'any' OR c.license_status = '$LicenseStatus')
        AND (c.address IS NULL OR c.address = '' OR c.address = '$addressCode')
    ORDER BY b.municipality = '$addressCode' DESC";
    $result = $conn->query($sql);

    $benefits = [];
    if ($result->num_rows > 0) {
        while ($row = $result->fetch_assoc()) {
            $benefits[] = $row;
        }
    }
    return $benefits;
}

// POSTリクエストの場合
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $conn = connectToDatabase($servername, $username, $password, $dbname);
    header('Content-Type: application/json');
    $age = $_POST['age'];
    $LicenseStatus = $_POST['license_status'];
    $addressCode = $_POST['address_code'];
    // 特典情報を取得
    $benefits = GetBenefits($conn, $age, $LicenseStatus, $addressCode);
    if (!empty($benefits)) {
        echo json_encode(array("success" => true, "benefits" => $benefits));
    } else {
        echo json_encode(array("success" => false, "message" => "特典が見つかりません"));
    }
    // 接続を閉じる
    $conn->close();
}
