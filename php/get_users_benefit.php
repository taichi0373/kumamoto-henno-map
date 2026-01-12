<?php
// ユーザ情報から特典を取得するAPI（利用できる特典ページ）

// DB接続ファイルを読み込む
include 'db_connection.php';

// 年齢計算関数
function calculateAge($birthDate)
{
    $birthDate = new DateTime($birthDate);
    $currentDate = new DateTime();
    $interval = $birthDate->diff($currentDate);
    return $interval->y;
}

// ユーザ情報取得関数
function GetUser($conn, $user_id)
{
    // データベースからユーザー情報を取得
    $sql = "SELECT * FROM users WHERE id = '$user_id'";
    $result = $conn->query($sql);

    // ユーザのusername, birth_date, address, license_statusを取得
    if ($result->num_rows > 0) {
        $row = $result->fetch_assoc();
        return array(
            "username" => $row['username'],
            "birth_date" => $row['birth_date'],
            "address" => $row['address'],
            "license_status" => $row['license_status']
        );
    } else {
        return null;
    }
}

// 特典情報取得関数
function GetBenefits($conn, $user)
{
    // ユーザの年齢を計算
    $age = calculateAge($user['birth_date']);
    $addressCode = $user['address'];
    $LicenseStatus = $user['license_status'];
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
    $user_id = $_POST['user_id'];

    // ユーザ情報を取得
    $user = GetUser($conn, $user_id);
    if ($user != null) {
        // 特典情報を取得
        $benefits = GetBenefits($conn, $user);
        if (!empty($benefits)) {
            echo json_encode(array("success" => true, "benefits" => $benefits));
        } else {
            echo json_encode(array("success" => false, "message" => "特典が見つかりません", "user" => $user));
        }
    } else {
        echo json_encode(array("success" => false, "message" => "ユーザーが見つかりません"));
    }

    // 接続を閉じる
    $conn->close();
}
