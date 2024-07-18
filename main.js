// 地図背景のタイルレイヤー
// const osm_1 = L.tileLayer(
//     "https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}.png",
//     {
//         // 右下にクレジットを表示(規約)
//         attribution: "&copy; OpenStreetMap contributors",
//     }
// );
const osm_1 = L.tileLayer("https://{s}.tile.osm.org/{z}/{x}/{y}.png", {
    // 右下にクレジットを表示(規約)
    attribution: "&copy; OpenStreetMap contributors",
});

// const osm_3 = L.tileLayer(
//     "https://cyberjapandata.gsi.go.jp/xyz/seamlessphoto/{z}/{x}/{y}.jpg",
//     {
//         // 右下にクレジットを表示(規約)
//         attribution:
//             // attributionにURLを入れると、クレジットの文字をクリックしたときにリンク先に飛ぶ
//             '<a href="https://maps.gsi.go.jp/development/ichiran.html#seamlessphoto" target="_blank">地理院タイル（写真）</a>',
//     }
// );

var baseLayers = {
    "OSM": osm_1,
};

var map = L.map("map", {
    center: [32.7898, 130.741584],
    zoom: 12,
    maxZoom: 18,
    layers: [osm_1],
    preferCanvas: true, //trueとし、Canvasレンダラーを選択
    zoomControl: false
});

function searchRoute(from, to, time, date, mode, maxWalkDistance, arriveBy) {
    fetch(`http://localhost/navi_project/api/getRoute.php?from=${from}&to=${to}&time=${time}&date=${date}&mode=${mode}&maxWalkDistance=${maxWalkDistance}&arriveBy=${arriveBy}`)
        .then(response => response.json())
        .then(data => {
            console.log(data);
            if (data.plan && data.plan.itineraries && data.plan.itineraries.length > 0) {
                data.plan.itineraries.forEach((itinerary, i) => {
                    console.log('経路' + (i + 1));
                    displayRoute(itinerary);
                });
                // displayRoute(data.plan.itineraries[0]);
            } else {
                console.error('レスポンスに経路が見つかりません');
            }
        })
        .catch(error => console.error('エラー:', error));
}

function displayRoute(itinerary) {
    const legs = itinerary.legs;

    // 各料金をチェック
    if (itinerary.fare.details.regular) {
        itinerary.fare.details.regular.forEach((fare, i) => {
            console.log(`道順${i + 1}：${fare.price.cents} 円`);
        });
    } else {
        console.log(`道順${i + 1}：料金情報が見つかりません`);
    }

    // 合計料金をチェック
    if (itinerary.fare.fare.regular) {
        const fare = itinerary.fare.fare.regular.cents;
        const currency = itinerary.fare.fare.regular.currency.currency;
        console.log(`合計料金: ${fare} ${currency}`);
    } else {
        console.log('合計料金の情報が見つかりません');
    }

    legs.forEach(leg => {
        if (leg.legGeometry && leg.legGeometry.points) {
            const latlngs = decodePolyline(leg.legGeometry.points);
            const mode = leg.mode;
            const distance = leg.distance;
            const from = leg.from.name;
            const to = leg.to.name;

            console.log(`移動手段: ${mode}, 距離: ${distance} メートル`);
            console.log(`出発地点: ${from}, 目的地点: ${to}`);

            if (latlngs.length > 0) {
                const polyline = L.polyline(latlngs, { color: 'blue' }).addTo(map);
                map.fitBounds(polyline.getBounds());
            } else {
                console.error('経路ジオメトリのポイントが見つかりません');
            }
        } else {
            console.error('経路ジオメトリが見つかりません');
        }
    });
}

// ポリラインのデコード関数
function decodePolyline(encoded) {
    let points = [];
    let index = 0, len = encoded.length;
    let lat = 0, lng = 0;

    while (index < len) {
        let b, shift = 0, result = 0;
        do {
            b = encoded.charCodeAt(index++) - 63;
            result |= (b & 0x1f) << shift;
            shift += 5;
        } while (b >= 0x20);
        let dlat = ((result & 1) ? ~(result >> 1) : (result >> 1));
        lat += dlat;

        shift = 0;
        result = 0;
        do {
            b = encoded.charCodeAt(index++) - 63;
            result |= (b & 0x1f) << shift;
            shift += 5;
        } while (b >= 0x20);
        let dlng = ((result & 1) ? ~(result >> 1) : (result >> 1));
        lng += dlng;

        points.push([lat / 1e5, lng / 1e5]);
    }
    return points;
}

// 現在の時刻を取得
const now = new Date();
const time = `${now.getHours()}:${('0' + now.getMinutes()).slice(-2)}`;
const date = `${('0' + (now.getMonth() + 1)).slice(-2)}-${('0' + now.getDate()).slice(-2)}-${now.getFullYear()}`;

// 例: 経路検索の呼び出し
searchRoute('32.6939628, 130.6662343', '32.8333243, 130.6961432', time, date, 'TRANSIT,WALK', 1000, false);
