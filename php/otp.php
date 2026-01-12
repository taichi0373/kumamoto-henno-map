<?php
header('Content-Type: application/json');

// パラメータの取得とデフォルト値の設定
$from = isset($_GET['from']) ? $_GET['from'] : '';
$to = isset($_GET['to']) ? $_GET['to'] : '';
$time = isset($_GET['time']) ? $_GET['time'] : '';
$date = isset($_GET['date']) ? $_GET['date'] : '';
$mode = isset($_GET['mode']) ? $_GET['mode'] : 'WALK';
$arriveBy = isset($_GET['arriveBy']) ? $_GET['arriveBy'] : 'false'; // 出発がfalse
// パラメータ
$maxWalkDistance = 1000;
$numItineraries = 5;
$locale = 'ja';
$optimize = 'QUICK';

// OTP API URLの構築
$otp_url = "http://160.16.92.209:8080/otp/routers/default/plan?"
    . "fromPlace=" . urlencode($from)
    . "&toPlace=" . urlencode($to)
    . "&time=" . urlencode($time)
    . "&date=" . urlencode($date)
    . "&mode=" . urlencode($mode)
    . "&arriveBy=" . $arriveBy
    . "&maxWalkDistance=" . $maxWalkDistance
    . "&numItineraries=" . $numItineraries
    . "&locale=" . $locale
    . "&optimize=" . $optimize
    . "&walkSpeed=1.389"
    . "&useRealtime=true";

// cURLを使用してOTPにリクエストを送信
$ch = curl_init();
curl_setopt($ch, CURLOPT_URL, $otp_url);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_TIMEOUT, 30);

$response = curl_exec($ch);
$http_code = curl_getinfo($ch, CURLINFO_HTTP_CODE);

if (curl_errno($ch) || $http_code !== 200) {
    http_response_code(500);
    echo json_encode(["error" => "Failed to fetch route data from OTP"]);
    error_log("cURL error: " . curl_error($ch));
    curl_close($ch);
    exit();
}

curl_close($ch);

// $responseのplan.itinerariesを取得
$itineraries = json_decode($response, true)['plan']['itineraries'];
// itinerariesを加工
$processedItineraries = [];

// 到着時刻（endTime）で並べ替え
usort($itineraries, function ($a, $b) use ($arriveBy, $time) {
    // endTimeをミリ秒から秒に変換して比較
    $timeA = $a['endTime'] / 1000;
    $timeB = $b['endTime'] / 1000;
    $specifiedTime = strtotime($time);

    if ($arriveBy === 'true') {
        // 到着時刻が指定された時刻よりも早いかつ到着時刻の遅い順
        if ($timeA <= $specifiedTime && $timeB <= $specifiedTime) {
            return $timeB - $timeA;
        } elseif ($timeA <= $specifiedTime) {
            return -1;
        } elseif ($timeB <= $specifiedTime) {
            return 1;
        } else {
            return $timeA - $timeB;
        }
    } else {
        // 出発時は到着時刻が早い順
        return $timeA - $timeB;
    }
});
// 上位3つの結果のみを取得
$topItineraries = array_slice($itineraries, 0, 3);

foreach ($topItineraries as $routeNumber => $itinerary) {
    $totalFare = 0;
    $all_coordinates = [];
    $fare = $itinerary['fare'];
    $RouteData_html = [
        'legs' => [],
        'startTime' => '',
        'endTime' => '',
        'duration' => '',
        'totalFare' => null,
        'totalDiscountFare' => null,
        'transfers' => null
    ];

    // legsデータの正規化
    foreach ($itinerary['legs'] as $index => $leg) {
        $legData = createLegData($leg, $fare, $index);
        $totalFare += $legData['fare'];
        // 最初の要素は "Origin"
        if ($index === 0) {
            $legData['from'] = 'Origin';
        }
        // 最後の要素は "Destination"
        if ($index === count($itinerary['legs']) - 1) {
            $legData['to'] = 'Destination';
        }
        $RouteData_html['legs'][$index] = $legData;
    }

    // 合計のルート情報
    $RouteData_html['startTime'] = $itinerary['startTime'] ?? "";
    $RouteData_html['endTime'] = $itinerary['endTime'] ?? "";
    $RouteData_html['duration'] = $itinerary['duration'] ?? "";
    $RouteData_html['totalFare'] = $totalFare;
    $RouteData_html['transfers'] = $itinerary['transfers'] ?? "";

    // 処理したルート情報を追加
    $processedItineraries[] = $RouteData_html;
}
// legsデータの加工
function createLegData($leg, $fare, $index)
{
    $leg_agencyID = '';
    $leg_fare = 0;
    $iconTranslations = [
        'WALK' => '🚶',
        'BICYCLE' => '🚲',
        'RAIL' => '🚆',
        'BUS' => '🚌',
        'TRAM' => '🚋'
    ];
    if (isset($fare['legProducts'])) {
        foreach ($fare['legProducts'] as $fareData) {
            if ($fareData['legIndices'][0] === $index) {
                $leg_fare = $fareData['products'][0]['amount']['cents'];
            }
        }
    }
    $mode = $leg['mode'] ?? "";
    $icon = $iconTranslations[$mode] ?? $mode;
    $leg_agencyID = '';
    if (isset($leg['agencyId'])) {
        $agencyIdParts = explode(":", $leg['agencyId']);
        if (count($agencyIdParts) > 1) {
            $leg_agencyID = $agencyIdParts[1];
        }
    }
    return [
        'mode' => $mode,
        'icon' => $icon,
        'startTime' => $leg['startTime'] ?? "",
        'endTime' => $leg['endTime'] ?? "",
        'duration' => $leg['duration'] ?? "",
        'from' => $leg['from']['name'] ?? "",
        'fromLat' => $leg['from']['lat'] ?? "",
        'fromLon' => $leg['from']['lon'] ?? "",
        'to' => $leg['to']['name'] ?? "",
        'toLat' => $leg['to']['lat'] ?? "",
        'toLon' => $leg['to']['lon'] ?? "",
        'fare' => $leg_fare,
        'discountFare' => null,
        'agencyName' => $leg['agencyName'] ?? "",
        'agencyID' => $leg_agencyID,
        'communitybusID' => "",
        'agencyUrl' => $leg['agencyUrl'] ?? "",
        'routeId' => $leg['routeId'] ?? "",
        'legGeometry' => $leg['legGeometry'],
        'transitLeg' => $leg['transitLeg'] ?? false,
        'benefitURL' => "",
        'benefitID' => "",
        'free_pass' => ""
    ];
}

// JSON形式で出力
echo json_encode($processedItineraries, JSON_UNESCAPED_UNICODE);
