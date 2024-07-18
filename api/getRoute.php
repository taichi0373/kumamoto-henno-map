<?php
header('Content-Type: application/json');

// パラメータの取得とデフォルト値の設定
$from = isset($_GET['from']) ? urlencode($_GET['from']) : '';
$to = isset($_GET['to']) ? urlencode($_GET['to']) : '';
$time = isset($_GET['time']) ? urlencode($_GET['time']) : '8:00am';
$date = isset($_GET['date']) ? urlencode($_GET['date']) : date('m-d-Y');
$mode = isset($_GET['mode']) ? urlencode($_GET['mode']) : 'TRANSIT,WALK';
$maxWalkDistance = isset($_GET['maxWalkDistance']) ? urlencode($_GET['maxWalkDistance']) : '1000';
$arriveBy = isset($_GET['arriveBy']) ? urlencode($_GET['arriveBy']) : 'false';

// デバッグメッセージ（オプション、確認後削除）
error_log("from: $from");
error_log("to: $to");
error_log("time: $time");
error_log("date: $date");
error_log("mode: $mode");
error_log("maxWalkDistance: $maxWalkDistance");
error_log("arriveBy: $arriveBy");

// OTP API URLの構築
$otp_url = "http://localhost:8080/otp/routers/default/plan?fromPlace=$from&toPlace=$to&time=$time&date=$date&mode=$mode&maxWalkDistance=$maxWalkDistance&arriveBy=$arriveBy";

// OTPにリクエストを送信
$response = file_get_contents($otp_url);
if ($response === FALSE) {
    http_response_code(500);
    echo json_encode(["error" => "Failed to fetch route data from OTP"]);
    exit();
}

// 結果を返す
echo $response;
