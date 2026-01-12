<?php
include 'db_connection.php';


function fetchCoordinatesParallel($urls)
{
    $multiHandle = curl_multi_init();
    $handles = [];

    // cURLハンドルを初期化
    foreach ($urls as $id => $url) {
        $handles[$id] = curl_init($url);
        curl_setopt($handles[$id], CURLOPT_RETURNTRANSFER, true);
        curl_multi_add_handle($multiHandle, $handles[$id]);
    }

    // 並列処理を実行
    do {
        curl_multi_exec($multiHandle, $running);
    } while ($running);

    $responses = [];
    foreach ($handles as $id => $handle) {
        $responses[$id] = curl_multi_getcontent($handle);
        curl_multi_remove_handle($multiHandle, $handle);
        curl_close($handle);
    }

    curl_multi_close($multiHandle);
    return $responses;
}

function getFacilityInfo($conn, $apiUrl, $apiKey)
{
    $sql = "
    SELECT 
        store_benefit.id, 
        store_benefit.name,
        store_benefit.detail,
        store_benefit.tel_number,
        store_condition.license_status,
        store_condition.min_age,
        store_condition.max_age,
        store_condition.note,
        municipality.name AS municipality_name
    FROM 
        store_condition
    JOIN 
        store_benefit ON store_condition.store_benefit_id = store_benefit.id
    JOIN 
        municipality ON store_condition.address = municipality.id
    WHERE 
        location_fetched IS NULL OR location_fetched = TRUE
    ";
    $result = $conn->query($sql);
    if ($result->num_rows > 0) {
        $urls = [];
        $storeData = [];
        $stores = [];


        while ($row = $result->fetch_assoc()) {
            $id = $row['id'];
            $name = urlencode($row['name']);
            $urls[$id] = "$apiUrl?appid=$apiKey&query=$name&ac=43&output=json&results=1";
            $storeData[$id] = $row;
        }

        $responses = fetchCoordinatesParallel($urls);
        $conn->begin_transaction();

        try {
            foreach ($responses as $id => $response) {
                $data = json_decode($response, true);
                $store = $storeData[$id];

                if (isset($data['Feature'][0]) && isset($data['Feature'][0]['Geometry']['Coordinates'])) {
                    // 位置情報が取得できた場合
                    $feature = $data['Feature'][0];
                    $coordinates = explode(",", $feature['Geometry']['Coordinates']);
                    $store['longitude'] = $coordinates[0];
                    $store['latitude'] = $coordinates[1];
                    $store['address'] = $feature['Property']['Address'] ?? '';
                    $stores[] = $store;

                    $updateSql = "
                        UPDATE store_benefit
                        SET location_fetched = TRUE
                        WHERE id = $id
                    ";
                } else {
                    // 位置情報が取得できなかった場合
                    $updateSql = "
                        UPDATE store_benefit
                        SET location_fetched = FALSE
                        WHERE id = $id
                    ";
                }
                $conn->query($updateSql);
            }
            $conn->commit();
        } catch (Exception $e) {
            $conn->rollback();
            throw $e;
        }

        return $stores;
    } else {
        return [];
    }
}

if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    require __DIR__ . '/vendor/autoload.php';
    $dotenv = Dotenv\Dotenv::createImmutable(__DIR__);
    $dotenv->load();
    $apiKey = $_ENV['YAHOO_API_KEY'];
    if (!$apiKey) {
        die(json_encode(["success" => false, "message" => "APIキーが設定されていません"]));
    }
    // API URLを設定
    $apiUrl = "https://map.yahooapis.jp/search/local/V1/localSearch";
    
    // DB接続
    $conn = connectToDatabase($servername, $username, $password, $dbname);

    // キャッシュを無効にするためのヘッダーを設定
    header("Cache-Control: no-cache, must-revalidate");
    header("Pragma: no-cache");
    header("Expires: 0");

    // コンテンツタイプを設定
    header('Content-Type: application/json');

    $stores = getFacilityInfo($conn, $apiUrl, $apiKey);
    if (!empty($stores)) {
        echo json_encode(array("success" => true, "stores" => $stores));
    } else {
        echo json_encode(array("success" => false, "message" => "施設が見つかりません"));
    }
    $conn->close();
}
