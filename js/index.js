let startMarker = null; // 出発地点のマーカー
let endMarker = null;   // 目的地点のマーカー
let crossMarker = null; // 十字マーカー（スマホUIでの地図上から入力のマーカー）
let stopMarker = null;  // 店舗のマーカー
const storeMarker = []; // 店舗マーカーを格納する配列

// 状態管理
let debounceTimer;
const debounceTime = 300;
let latestRequestId = 0;

let isWaitingForInput_S = false; // 出発地点の入力待ちフラグ
let isWaitingForInput_E = false; // 目的地点の入力待ちフラグ

// ページ読み込み時の処理
document.addEventListener("DOMContentLoaded", function () {
    const start_location = document.getElementById('start-location');
    const start_location_div = document.getElementById('start-location-div');
    const start_suggestions = document.getElementById('start-suggestions');
    const start_suggestions_ex = document.getElementById('start-suggestions-ex');
    const end_location = document.getElementById('end-location');
    const end_location_div = document.getElementById('end-location-div');
    const end_suggestions = document.getElementById('end-suggestions');
    const end_suggestions_ex = document.getElementById('end-suggestions-ex');

    const sidebar = document.querySelector(".sidebar");
    const toggleBtn = document.getElementById('sidebar-toggle');

    const routeList = document.getElementById('route-list');

    // 現在地の候補表示
    SetCurrentLocation();

    // モーダルを閉じるイベントを設定
    const modal = document.getElementById("modal");
    const closeModal = document.getElementById("closeModal");
    closeModal.addEventListener("click", () => {
        modal.style.display = "none";
    });
    window.addEventListener("click", (event) => {
        if (event.target === modal) {
            modal.style.display = "none";
        }
    });
    // サイドバーの開閉
    toggleBtn.addEventListener('click', function () {
        sidebar.classList.toggle("collapsed");
        // ボタンアイコンの切り替え
        const icon = toggleBtn.querySelector("i");
        if (sidebar.classList.contains("collapsed")) {
            icon.classList.remove("fa-caret-left");
            icon.classList.add("fa-caret-right");
        } else {
            icon.classList.remove("fa-caret-right");
            icon.classList.add("fa-caret-left");
        }
    });
    // サイドバー内のタブ切り替え
    const btn_tab = document.querySelectorAll('.tab-button');
    btn_tab.forEach(button => {
        button.addEventListener('click', () => {
            document.querySelectorAll('.tab-button').forEach((btn) => {
                btn.classList.remove('active');
            });
            document.querySelectorAll('.sidebar-page').forEach(page => {
                page.style.display = 'none';
            });
            button.classList.add('active');
            // 対応するページを表示
            const targetPageId = button.getAttribute('data-tab');
            const targetPage = document.getElementById(targetPageId);
            if (targetPage) {
                targetPage.style.display = 'block';
            }
        });
    });
    // 条件指定の表示・非表示の切り替え
    const trigger = document.querySelector(".expand-trigger");
    const formContainer = document.querySelector(".expand-trigger-container");
    trigger.addEventListener("click", function () {
        if (formContainer.style.display === "none" || formContainer.style.display === "") {
            formContainer.style.display = "block";
            trigger.innerHTML = '条件を閉じる <i class="bi bi-chevron-up icon-expand-trigger"></i>';
        } else {
            formContainer.style.display = "none";
            trigger.innerHTML = '条件指定 <i class="bi bi-chevron-down icon-expand-trigger"></i>';
        }
    });
    // 地図の設定
    const defaultCenter = [130.741584, 32.7898];
    const map = new maplibregl.Map({
        container: 'map',
        style: 'https://tile.openstreetmap.jp/styles/osm-bright-ja/style.json',
        center: defaultCenter,
        zoom: 14,
        minZoom: 8,
        maxZoom: 18,
        pitch: 0,
        bearing: 0,
        maxPitch: 0,
    });

    map.on('load', function () {
        // コントロールを右下に配置
        const navControl = new maplibregl.NavigationControl();
        map.addControl(navControl, 'bottom-right');
        // 現在位置
        const geolocateControl = new maplibregl.GeolocateControl({
            positionOptions: {
                enableHighAccuracy: false
            },
            fitBoundsOptions: { maxZoom: 14 },
            trackUserLocation: true,
            showAccuracyCircle: false,
            showUserLocation: true,
        });
        map.addControl(geolocateControl, 'bottom-right');
    });
    // 現在地を取得して地図の中心に設定
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
            (position) => {
                const userLocation = [position.coords.longitude, position.coords.latitude];
                map.setCenter(userLocation);
            },
            () => {
                map.setCenter(defaultCenter);
            }
        );
    } else {
        map.setCenter(defaultCenter);
    }
    const bounds = [
        [128.523336, 30.896306],
        [132.3318, 34.0596]
    ];
    map.setMaxBounds(bounds);
    // 地図クリックイベント
    map.on('click', async function (e) {
        const lat = e.lngLat.lat;
        const lon = e.lngLat.lng;
        if (isWaitingForInput_S) {
            const locationData = await ReverseGeocoding(lat, lon);
            if (locationData) {
                // スタート地点の設定
                start_location.value = locationData.name;
                placeMarker(lat, lon, locationData.name, 'start');
                if (startMarker && endMarker) {
                    clearMarker('stop');
                    clearRoutes();
                    searchRouteEvent()
                }
            } else {
                alert('位置情報データが見つかりませんでした');
            }

        } else if (isWaitingForInput_E) {
            const locationData = await ReverseGeocoding(lat, lon);
            if (locationData) {
                // ゴール地点の設定
                end_location.value = locationData.name;
                placeMarker(lat, lon, locationData.name, 'end');
                if (startMarker && endMarker) {
                    clearMarker('stop');
                    clearRoutes();
                    searchRouteEvent()
                }
            } else {
                alert('位置情報データが見つかりませんでした');
            }
        }
    });
    // 地図のズーム
    function zoomToLocation(lat, lon) {
        map.flyTo({
            center: [lon, lat],
            zoom: 16
        });
    }
    // 地図のルート追加
    function addRouteLayer(routeNumber, index, coordinates) {
        const layerId = 'route-' + routeNumber + '-' + index;
        const colors = ['#1A74FD', '#757575', '#757575'];
        const color = colors[routeNumber];
        // 初期化
        if (map.getLayer(layerId)) {
            map.removeLayer(layerId);
        }
        if (map.getSource(layerId)) {
            map.removeSource(layerId);
        }
        // 新しいレイヤーを追加
        map.addLayer({
            id: 'route-' + routeNumber + '-' + index,
            type: 'line',
            source: {
                type: 'geojson',
                data: {
                    type: 'Feature',
                    geometry: {
                        type: 'LineString',
                        coordinates: coordinates
                    }
                }
            },
            layout: {
                'line-join': 'round',
                'line-cap': 'round'
            },
            paint: {
                'line-color': color,
                'line-width': 5
            }
        });
    }

    // 逆ジオコーディング
    async function ReverseGeocoding(lat, lon) {
        const url = `https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=${lat}&lon=${lon}&zoom=18&addressdetails=1&countrycodes=JP&limit=5`;
        try {
            const response = await fetch(url);
            const data = await response.json();
            if (data.name) {
                return { name: data.name, lat: data.lat, lon: data.lon };
            } else if (data.name === "") {
                const province = data.address.province || ''; // 都道府県
                const county = data.address.county || ''; // 郡
                const suburb = data.address.suburb || ''; // 区
                const city = data.address.city || data.address.town || data.address.village || ''; // 市、町、村
                const quarter = data.address.quarter || data.address.neighbourhood || ''; // 丁目、番地
                return {
                    name: `${province}${county}${city}${suburb}${quarter}`,
                    lat: data.lat,
                    lon: data.lon,
                };
            } else {
                return false;
            }
        } catch (error) {
            console.error('エラーが発生しました:', error);
            return false;
        }
    }

    // ジオコーディング
    async function Geocoding(location) {
        const url = `https://nominatim.openstreetmap.org/search?q=日本, 熊本県, ${encodeURIComponent(location)}&format=json&addressdetails=1&countrycodes=JP&limit=5`;
        try {
            const response = await fetch(url);
            const data = await response.json();

            if (data.length > 0) {
                // 上位5件の結果を取得
                data.slice(0, 5);
                return data;
            } else {
                return false;
            }
        } catch (error) {
            console.error('エラー:', error);
            return false;
        }
    }
    // 現在地の候補表示
    function SetCurrentLocation() {
        start_suggestions_ex.innerHTML = '';
        end_suggestions_ex.innerHTML = '';
        // 地図上から選択の追加
        if (window.innerWidth <= 991) {
            const li_S = document.createElement('li');
            li_S.classList.add('list-group-item');
            li_S.textContent = '地図上から設定';
            li_S.style.fontWeight = 'bold';
            li_S.onclick = () => {
                addCrossMarkerAndButton("start");
            };
            start_suggestions_ex.appendChild(li_S);
            const li_E = document.createElement('li');
            li_E.classList.add('list-group-item');
            li_E.textContent = '地図上から設定';
            li_E.style.fontWeight = 'bold';
            li_E.onclick = () => {
                addCrossMarkerAndButton("end");
            };
            end_suggestions_ex.appendChild(li_E);
        }
        // 現在地の追加
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(function (position) {
                const lat = position.coords.latitude;
                const lon = position.coords.longitude;
                const li_S = document.createElement('li');
                li_S.classList.add('list-group-item');
                li_S.textContent = '現在地';
                li_S.style.fontWeight = 'bold';
                li_S.onclick = () => selectSuggestion("start", '現在地', lat, lon);

                const li_E = document.createElement('li');
                li_E.classList.add('list-group-item');
                li_E.textContent = '現在地';
                li_E.style.fontWeight = 'bold';
                li_E.onclick = () => selectSuggestion("end", '現在地', lat, lon);

                start_suggestions_ex.appendChild(li_S);
                end_suggestions_ex.appendChild(li_E);
            }, function (error) {
                console.error('現在地を取得できませんでした: ', error);
            });
        } else {
            alert('位置情報がサポートされていません');
        }
    }

    // ルート検索
    async function searchRoute(from, to, time, date, mode, arriveBy) {
        // URLのクエリパラメータを正しく構築
        const otp_url = `./php/otp.php?from=${encodeURIComponent(from)}&to=${encodeURIComponent(to)}&time=${encodeURIComponent(time)}&date=${encodeURIComponent(date)}&mode=${encodeURIComponent(mode)}&arriveBy=${arriveBy}`;
        try {
            const response = await fetch(otp_url);
            if (!response.ok) {
                throw new Error('エラーレスポンス:', response);
            }
            const data = await response.json();
            if (data.length === 0) {
                const li = document.createElement('li');
                li.classList.add('route-section');
                li.style.marginLeft = '20px';
                li.textContent = '経路が見つかりませんでした';
                routeList.appendChild(li);
                return;
            }
            await displayAllRoutes(data);
            data.forEach((route, index) => {
                displayRouteMap(route, index);
            });
        } catch (error) {
            console.error('エラー:', error);
        }
    }
    async function displayAllRoutes(routes) {
        routeList.innerHTML = '';
        for (let index = 0; index < routes.length; index++) {
            await displayRouteHTML(routes[index], index);
        }
    }

    // ルート表示のカラーを切り替える関数
    function changeRoutesColorByPattern(routeNumber) {
        // Map内のすべてのレイヤーIDを取得
        const allLayers = map.getStyle().layers;
        let matchingLayerIds = [];

        allLayers.forEach(layer => {
            if (layer.id.startsWith('route-' + routeNumber)) {
                map.setPaintProperty(layer.id, 'line-color', "#1A74FD");
                matchingLayerIds.push(layer.id);
            } else if (layer.id.startsWith('route-')) {
                map.setPaintProperty(layer.id, 'line-color', '#757575');
            }
        });
        // 一致するレイヤーを一番上に移動
        matchingLayerIds.forEach(layerId => {
            map.moveLayer(layerId);
        });
    }

    // 時間のフォーマット整形
    function formatDuration(durationInSeconds) {
        const minutes = Math.floor(durationInSeconds / 60);
        return `${minutes}分`;
    }
    function formatTimestamp(timestamp) {
        const date = new Date(timestamp);
        const hours = date.getHours().toString().padStart(2, '0');
        const minutes = date.getMinutes().toString().padStart(2, '0');
        return `${hours}時${minutes}分`;
    }

    // コミュニティバスの取得
    async function fetchCommunityBus(route_id) {
        const parts = route_id.split('_');
        const fd_community_bus = new FormData();
        fd_community_bus.append('route_id', `${parts[0]}_${parts[1]}_${parts[2]}`);
        try {
            const response = await fetch("./php/getCommunityBus.php", {
                method: "POST",
                body: fd_community_bus,
            });
            return await response.json();
        } catch (error) {
            console.error("コミュニティバスがありません:", error);
            return null;
        }
    }
    // 割引特典の取得
    async function fetchTransitBenefits(userId) {
        const fd_transit_benefit = new FormData();
        fd_transit_benefit.append('user_id', userId);
        try {
            const response = await fetch("./php/get_transit_benefit.php", {
                method: "POST",
                body: fd_transit_benefit,
            });
            return await response.json();
        } catch (error) {
            console.error("割引特典の取得に失敗しました:", error);
            return null;
        }
    }
    // 割引料金の反映
    function calculateFare(leg, transit_benefits) {
        let minFare = leg.fare;  // 最初は通常の運賃を最小料金として設定
        let benefitURL = null;
        let benefitID = null;
        let free_pass = null;
        const compare_agencyID = leg.communitybusID || leg.agencyID;
        for (const transit_benefit of transit_benefits) {
            let discountedFare;
            if (transit_benefit.business_id === compare_agencyID) {
                if (transit_benefit.discount_type === "percentage") {
                    const discounted_cents = leg.fare * (1 - transit_benefit.discount_value / 100);
                    discountedFare = Math.ceil(discounted_cents / 10) * 10;  // 10円単位で切り上げ（県内全域の特典は10円未満切り上げのため）
                } else if (transit_benefit.discount_type === "fixed") {
                    discountedFare = Math.floor(transit_benefit.discount_value);
                } else if (transit_benefit.discount_type === "free_pass") {
                    free_pass = transit_benefit.benefit_id;
                } else {
                    discountedFare = leg.fare;
                }
                // 最小の運賃を更新
                if (discountedFare < minFare) {
                    minFare = discountedFare;
                    benefitURL = transit_benefit.url;
                    benefitID = transit_benefit.benefit_id;
                }
            }
        }
        return { minFare, benefitURL, benefitID, free_pass };
    }
    function applyTransitBenefits(route, transit_benefits) {
        let totalDiscountFare = 0;
        for (let i = 0; i < route.legs.length; i++) {
            const leg = route.legs[i];
            const { minFare, benefitURL, benefitID, free_pass } = calculateFare(leg, transit_benefits);
            if (leg.fare !== minFare) {
                leg.discountFare = minFare;
                leg.benefitURL = benefitURL;
                leg.benefitID = benefitID;
                leg.free_pass = free_pass || null;
            } else {
                leg.discountFare = null;
                leg.benefitURL = null;
                leg.benefitID = null;
                leg.free_pass = free_pass || null;
            }
            totalDiscountFare += minFare;
        }
        if (route.totalFare !== totalDiscountFare) {
            route.totalDiscountFare = totalDiscountFare;
        }
    }
    // ルートを地図に表示
    function displayRouteMap(route, index) {
        const routeNumber = index;
        let all_coordinates = [];
        route.legs.forEach((leg, index) => {
            if (leg.legGeometry && leg.legGeometry.points) {
                const latlngs = decodePolyline(leg.legGeometry.points);
                if (latlngs.length > 0) {
                    const coordinates = latlngs.map(point => [point[1], point[0]]);
                    coordinates.forEach(coord =>
                        all_coordinates.push(coord)
                    );
                    addRouteLayer(routeNumber, index, coordinates);
                }
            }

            if (leg.mode === "BUS" && leg.fromLat && leg.fromLon && leg.toLat && leg.toLon) {
                placeStopMarker(leg.fromLat, leg.fromLon, leg.from, 'stop');
                placeStopMarker(leg.toLat, leg.toLon, leg.to, 'stop');
            }
        });
        const bounds = new maplibregl.LngLatBounds();
        all_coordinates.forEach(coord => bounds.extend(coord));
        map.fitBounds(bounds, { padding: 50 });
    }
    // ルート表示・検索結果をHTMLに埋め込み
    async function displayRouteHTML(route, index) {
        const legPromises = route.legs.map(async (leg) => {
            if (leg.transitLeg === true && leg.agencyName && leg.routeId) {
                const routeId = leg.routeId.split(':')[1];
                const data = await fetchCommunityBus(routeId);
                if (data && data.success) {
                    leg.agencyName = data.data.business_name;
                    leg.communitybusID = data.data.agency_id;
                }
            }
        });
        await Promise.all(legPromises);
        if (userToken) {
            const userId = atob(userToken);
            try {
                const data = await fetchTransitBenefits(userId);
                if (data.success) {
                    applyTransitBenefits(route, data.transit_benefits);
                }
            } catch (error) {
                console.error("割引情報の取得中にエラーが発生しました:", error);
            }
        }
        const li = createRouteListItem(route, index);
        routeList.appendChild(li);
    }

    // OTPサーバのレスポンス結果をデコード（文字列 → 座標）
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

    function createRouteListItem(route, index) {
        const li = document.createElement('li');
        li.classList.add('route-section');

        const button = document.createElement('button');
        button.classList.add('route-btn');
        button.id = `route-${index}`;

        const routeContent = createRouteContent(route, index);

        const arrowIcon = document.createElement('i');
        arrowIcon.classList.add('bi', 'bi-chevron-down');

        button.appendChild(routeContent);
        button.appendChild(arrowIcon);

        addToggleEvent(button, li, arrowIcon, routeContent.querySelector('.route-details'));

        li.appendChild(button);
        return li;
    }

    function createRouteContent(route, index) {
        const routeContent = document.createElement('div');
        routeContent.classList.add('route-content');
        const routeHeader = createRouteHeader(route, index);
        const routeInfo = createRouteInfo(route);
        const routeDetails = createRouteDetails(route);
        routeContent.appendChild(routeHeader);
        routeContent.appendChild(routeInfo);
        routeContent.appendChild(routeDetails);
        return routeContent;
    }

    // 出発時刻・到着時刻・所要時間
    function createRouteHeader(route, index) {
        const routeHeader = document.createElement('div');
        routeHeader.classList.add('route-header');

        const routeNumber = document.createElement('span');
        routeNumber.classList.add('route-number');
        routeNumber.textContent = index + 1;

        const routeTimes = document.createElement('div');
        routeTimes.classList.add('route-times');
        routeTimes.innerHTML = `<span class="start-time">${formatTimestamp(route.startTime)}</span> → 
                            <span class="end-time">${formatTimestamp(route.endTime)}</span> 
                            <span class="duration">( ${formatDuration(route.duration)} )</span>`;
        routeHeader.appendChild(routeNumber);
        routeHeader.appendChild(routeTimes);
        return routeHeader;
    }
    // 運賃・乗り換え回数
    function createRouteInfo(route) {
        const routeInfo = document.createElement('div');
        routeInfo.classList.add('route-info');
        if (route.totalDiscountFare !== null) {
            routeInfo.innerHTML = `運賃：<span style="text-decoration: line-through;">${route.totalFare}円 </span> <strong style="margin-left: 6px;"> ${route.totalDiscountFare}円</strong> / 乗り換え：${route.transfers}回`;
        } else {
            routeInfo.innerHTML = `運賃：${route.totalFare}円 / 乗り換え: ${route.transfers}回`;
        }
        return routeInfo;
    }

    // ルート詳細
    function createRouteDetails(route) {
        const routeDetails = document.createElement('div');
        routeDetails.classList.add('route-details');
        routeDetails.style.display = 'none';
        route.legs.forEach(leg => {
            const legInfo = createLegInfo(leg);
            routeDetails.appendChild(legInfo);
        });
        return routeDetails;

        function createLegInfo(leg) {
            const legInfo = document.createElement('div');
            legInfo.classList.add('leg-info');
            const origin = start_location.value;
            const destination = end_location.value;
            const fromName = leg.from === "Origin" ? origin : leg.from;
            const toName = leg.to === "Destination" ? destination : leg.to;
            legInfo.innerHTML = generateLegTemplate(leg, fromName, toName, leg.agencyName);
            return legInfo;
        }

        function generateLegTemplate(leg, fromName, toName, agencyName) {
            const isOrigin = leg.from === "Origin";
            const isDestination = leg.to === "Destination";

            const fromIcon = isOrigin ? "S" : "O";
            const toIcon = isDestination ? "G" : "O";
            return `
            <div class="stop">
                <div class="icon ${isOrigin ? 'origin' : 'stop_point'}">${fromIcon}</div>
                <div class="details">
                    <div class="stop_name">${fromName}</div>
                </div>
            </div>
            <div class="connector">
                <div class="transport">
                    <div class="icon">${leg.icon}</div>
                    <div class="details">
                        ${agencyName ? `<div class="agency-name">${agencyName}</div>` : ''}
                        <div class="time">${formatDuration(leg.duration)} (${formatTimestamp(leg.startTime)} ~ ${formatTimestamp(leg.endTime)})</div>
                        ${leg.fare > 0 ? `<div class="fare">運賃：${leg.discountFare !== null ? `<span style="text-decoration: line-through;">${leg.fare}円</span><strong style="margin-left: 6px;"> ${leg.discountFare}円</strong>` : `${leg.fare}円`}</div>` : ''}
                        ${leg.benefitID ? `<div class="benefit-detail"><a href="#" class="benefit-link" onclick="scrollToBenefit(${leg.benefitID})">詳細を見る</a></div>` : ''}
                        ${leg.free_pass ? `<div class="free-pass"><a href="#" class="benefit-link" onclick="scrollToBenefit(${leg.free_pass})">割引券</a>が利用できます</div>` : ''}
                    </div>
                </div>
            </div>
                ${isDestination ? `
            <div class="stop">
                <div class="icon destination">${toIcon}</div>
                <div class="details">
                    <div class="stop_name">${toName}</div>
                </div>
            </div>` : ''
                }
            `;
        }
    }

    // ルート詳細の展開・非展開
    function addToggleEvent(button, li, arrowIcon, routeDetails) {
        button.addEventListener('click', function () {
            const route_btn = document.querySelectorAll('.route-btn');
            route_btn.forEach(btn => {
                if (btn !== button) {
                    const otherLi = btn.closest('li');
                    const otherArrowIcon = btn.querySelector('.bi');
                    const otherRouteDetails = otherLi.querySelector('.route-details');
                    otherLi.classList.remove('expanded');
                    otherArrowIcon.classList.remove('bi-chevron-up');
                    otherArrowIcon.classList.add('bi-chevron-down');
                    otherRouteDetails.style.display = 'none';
                }
            });
            li.classList.toggle('expanded');
            if (li.classList.contains('expanded')) {
                arrowIcon.classList.remove('bi-chevron-down');
                arrowIcon.classList.add('bi-chevron-up');
                routeDetails.style.display = 'block';
            } else {
                arrowIcon.classList.remove('bi-chevron-up');
                arrowIcon.classList.add('bi-chevron-down');
                routeDetails.style.display = 'none';
            }

            const route_btn_id = button.id;
            const routeNumber = route_btn_id.split('-')[1];
            changeRoutesColorByPattern(routeNumber);
        });
    }

    // ルート初期化
    function clearRoutes() {
        const layers = map.getStyle().layers;
        layers.forEach((layer) => {
            if (layer.id.startsWith('route-')) {
                map.removeLayer(layer.id);
                if (map.getSource(layer.id)) {
                    map.removeSource(layer.id);
                }
            }
        });
    }
    // マーカー設置
    function placeMarker(lat, lon, name, type) {
        const popupText = type === 'start' ? '出発地: ' + name : '目的地: ' + name;
        const markerClass = type === 'start' ? 'start-marker' : 'end-marker';
        const markerLabel = type === 'start' ? 'S' : 'G';
        const locationElement = type === 'start' ? start_location : end_location;
        const markerType = type === 'start' ? 'start' : 'end';
        const popup = new maplibregl.Popup({ offset: 25 }).setText(popupText);

        // マーカー初期化
        clearMarker(type);

        // カスタムマーカー要素を作成
        const markerElement = document.createElement('div');
        markerElement.className = markerClass;
        markerElement.innerHTML = `<span> ${markerLabel}</span> `;

        const marker = new maplibregl.Marker({ element: markerElement, draggable: true })
            .setLngLat([lon, lat])
            .setPopup(popup)
            .addTo(map);
        // マーカーのドラッグ終了時に新しい位置を取得
        marker.on('dragend', async function () {
            const newLngLat = marker.getLngLat();
            const locationData = await ReverseGeocoding(newLngLat.lat, newLngLat.lng);
            if (locationData) {
                locationElement.value = locationData.name;
                placeMarker(newLngLat.lat, newLngLat.lng, locationData.name, markerType);
                if (startMarker && endMarker) {
                    clearMarker('stop');
                    clearRoutes();
                    searchRouteEvent();
                }
            } else {
                alert('位置情報データが見つかりませんでした');
            }
        });

        if (type === 'start') {
            startMarker = marker;
        } else if (type === 'end') {
            endMarker = marker;
        }
    }

    // 停留所カスタムアイコンのマーカー設置
    function placeStopMarker(lat, lon, name) {
        const marker = new maplibregl.Marker({
            element: createStopMarker(name)
        })
            .setLngLat([lon, lat])
            .addTo(map)
            .setPopup(new maplibregl.Popup().setText(name));
        stopMarker = marker;
    }
    function createStopMarker() {
        const markerElement = document.createElement('div');
        markerElement.className = 'custom-marker';

        // アイコン要素
        const icon = document.createElement('div');
        icon.className = 'icon-stop';
        icon.style.backgroundImage = 'url(css/img/stop.svg)';
        icon.style.backgroundSize = 'contain';
        markerElement.appendChild(icon);
        // ズームレベルに応じてアイコンとラベルの表示を変更
        map.on('zoom', function () {
            const zoomLevel = map.getZoom();
            if (zoomLevel < 14) {
                icon.style.display = 'none';
            } else {
                icon.style.display = 'block';
            }
        });
        return markerElement;
    }

    // マーカー初期化
    function clearMarker(type) {
        if (type === 'start' && startMarker !== null) {
            startMarker.remove();
            startMarker = null;
        } else if (type === 'end' && endMarker !== null) {
            endMarker.remove();
            endMarker = null;
        } else if (type === 'stop' && stopMarker !== null) {
            stopMarker.remove();
            stopMarker = null;
        }
    }

    // 出発地または目的地が選択された時の処理
    function selectSuggestion(type, selectedLocation, lat, lon) {
        const locationElement = type === 'start' ? start_location : end_location;
        const suggestionsElement = type === 'start' ? start_suggestions : end_suggestions;
        const suggestionsExElement = type === 'start' ? start_suggestions_ex : end_suggestions_ex;
        const markerType = type === 'start' ? 'start' : 'end';

        locationElement.value = selectedLocation;

        suggestionsElement.style.display = 'none';
        suggestionsExElement.style.display = 'none';
        placeMarker(lat, lon, selectedLocation, markerType);

        if (startMarker && endMarker) {
            clearMarker('stop');
            clearRoutes();
            searchRouteEvent();
        } else {
            zoomToLocation(lat, lon);
        }
    }

    // 経路探索結果の"詳細"をクリック時に特典ページに遷移
    function scrollToBenefit(benefitId) {
        const users_benefit_tab = document.getElementById('users-benefit-tab');
        users_benefit_tab.click();
        // 指定された特典IDに対応する要素を取得
        const targetBenefit = document.querySelector(`[data - benefit - id="${benefitId}"]`);
        if (targetBenefit) {
            // スクロールして画面中央に配置
            targetBenefit.scrollIntoView({
                behavior: 'smooth',
                block: 'center'
            });
            targetBenefit.style.transition = 'background-color 0.5s ease';
            targetBenefit.style.backgroundColor = '#ffffcc';
            setTimeout(() => {
                targetBenefit.style.backgroundColor = '';
            }, 2000);
        }
    }

    // focusイベントのリスナーを設定
    start_location.addEventListener('focus', function () {
        end_suggestions.style.display = 'none';
        end_suggestions_ex.style.display = 'none';
        if (start_location.value.trim() === '') {
            start_suggestions_ex.style.display = 'block';
            isWaitingForInput_S = true;
        } else {
            isWaitingForInput_S = false;
            start_suggestions.style.display = 'block';
        }
    });
    end_location.addEventListener('focus', function () {
        start_suggestions.style.display = 'none';
        start_suggestions_ex.style.display = 'none';
        if (end_location.value.trim() === '') {
            end_suggestions_ex.style.display = 'block';
            isWaitingForInput_E = true;
        } else {
            isWaitingForInput_E = false;
            end_suggestions.style.display = 'block';
        }
    });

    // 入力値から候補を検索しリスト表示
    async function searchSuggestions(type) {
        const currentRequestId = ++latestRequestId; // 最新のリクエストIDを設定

        const locationInput = type === 'start' ? start_location : end_location;
        const suggestionsElement = type === 'start' ? start_suggestions : end_suggestions;

        const inputValue = locationInput.value.trim();
        if (!inputValue) {
            suggestionsElement.innerHTML = '';
            suggestionsElement.style.display = 'none';
            return;
        }

        try {
            const geocodingResult = await Geocoding(inputValue);
            // 古いリクエストの結果は無視
            if (currentRequestId !== latestRequestId) return;

            suggestionsElement.innerHTML = '';
            if (Array.isArray(geocodingResult) && geocodingResult.length > 0) {
                const displayedNames = new Set();
                geocodingResult.forEach(item => {
                    if (displayedNames.has(item.name)) return;
                    displayedNames.add(item.name);

                    const li = document.createElement('li');
                    li.classList.add('list-group-item');
                    li.onclick = () => selectSuggestion(type, item.name, item.lat, item.lon);

                    const nameSpan = document.createElement('span');
                    nameSpan.textContent = item.name;
                    nameSpan.style.fontWeight = 'bold';

                    const displayNameSpan = document.createElement('span');
                    displayNameSpan.style.marginLeft = '8px';
                    displayNameSpan.textContent = ` (${item.display_name})`;

                    li.appendChild(nameSpan);
                    li.appendChild(displayNameSpan);
                    suggestionsElement.appendChild(li);
                });
            } else {
                const li = document.createElement('li');
                li.classList.add('list-group-item');
                li.textContent = '候補が見つかりません';
                suggestionsElement.appendChild(li);
            }
            suggestionsElement.style.display = 'block';
        } catch (error) {
            console.error('Geocodingエラー:', error);
            suggestionsElement.innerHTML = '<li class="list-group-item">エラーが発生しました</li>';
            suggestionsElement.style.display = 'block';
        }
    }

    // inputイベントのリスナーを設定
    function setupInputListeners(inputElement, type) {
        const suggestionsElement = type === 'start' ? start_suggestions : end_suggestions;
        const suggestionsElement_ex = type === 'start' ? start_suggestions_ex : end_suggestions_ex;

        inputElement.addEventListener('input', () => {
            suggestionsElement.replaceChildren();
            clearTimeout(debounceTimer);

            debounceTimer = setTimeout(() => {
                const inputValue = inputElement.value.trim();
                if (inputValue) {
                    clearRoutes();
                    clearMarker(type);
                    clearMarker('stop');
                    suggestionsElement_ex.style.display = 'none';
                    suggestionsElement.style.display = 'block';
                    searchSuggestions(type);
                } else {
                    suggestionsElement.style.display = 'none';
                    suggestionsElement_ex.style.display = 'block';
                    clearRoutes();
                    clearMarker(type);
                    clearMarker('stop');
                    routeList.innerHTML = '';
                }
            }, debounceTime);
        });
    }

    // inputイベントのリスナーを設定
    setupInputListeners(start_location, 'start');
    setupInputListeners(end_location, 'end');

    // ルート検索イベント
    async function searchRouteEvent() {
        if (startMarker && endMarker) {
            // 初期化
            routeList.innerHTML = '';

            // 交通手段
            const transportMode = document.getElementById('transport-mode').value;

            // 時間指定
            const timeSelect = document.getElementById('time-select').value;
            let dateValue = document.getElementById('date').value;
            let timeValue = document.getElementById('time').value;

            // 出発時刻か到着時刻を判定
            const isDepartureTime = (timeSelect !== 'departure');

            // 日付と時刻が未入力の場合は現在時刻を設定
            if (!dateValue || !timeValue) {
                const now = new Date();
                if (!dateValue) {
                    dateValue = now.toISOString().split('T')[0];
                }
                if (!timeValue) {
                    timeValue = now.toTimeString().split(' ')[0].substring(0, 5);
                }
            }
            const startLngLat = startMarker.getLngLat();
            const endLngLat = endMarker.getLngLat();
            await searchRoute(
                `${startLngLat.lat},${startLngLat.lng} `,
                `${endLngLat.lat},${endLngLat.lng} `,
                timeValue,
                dateValue,
                transportMode,
                isDepartureTime
            );
            // ルートの色を変更
            changeRoutesColorByPattern(0);
        } else {
            return;
        }
    }


    function handleInputChange() {
        if (startMarker && endMarker) {
            clearMarker('stop');
            clearRoutes();
            searchRouteEvent();
        }
    }
    const transport_mode = document.getElementById('transport-mode');
    const time_select = document.getElementById('time-select');
    const input_date = document.getElementById('date');
    const input_time = document.getElementById('time');
    transport_mode.addEventListener('change', handleInputChange);
    time_select.addEventListener('change', handleInputChange);
    input_date.addEventListener('change', handleInputChange);
    input_time.addEventListener('change', handleInputChange);

    // クリックイベント管理
    document.addEventListener('click', function (event) {
        const mapContainer = document.getElementById('map');
        const isClickInsideMap = mapContainer.contains(event.target);
        const isClickOutsideInputs = !start_location_div.contains(event.target) && !end_location_div.contains(event.target);
        if (isClickInsideMap || isClickOutsideInputs) {
            // 入力待ちフラグを解除
            isWaitingForInput_S = false;
            isWaitingForInput_E = false;
            start_suggestions.style.display = 'none';
            end_suggestions.style.display = 'none';
            start_suggestions_ex.style.display = 'none';
            end_suggestions_ex.style.display = 'none';
        }
    });

    // 店舗表示ボタンがクリックされた時の処理
    const display_store = document.getElementById('display-store');
    display_store.addEventListener('click', function () {
        const modal = document.getElementById("modal");
        const modalBody = document.getElementById("modalBody");
        fetch("./php/yolp.php", {
            method: "GET",
        })
            .then((response) => response.json())
            .then((data) => {
                if (data.success) {
                    const existingMarkers = new Set(storeMarker.map(marker => marker.getElement().dataset.storeId));
                    data.stores.forEach((store) => {
                        if (existingMarkers.has(store.id)) {
                            return;
                        }
                        const store_lat = store.latitude;
                        const store_lon = store.longitude;
                        const store_name = store.name;
                        const store_tel = store.tel_number;
                        const store_detail = store.detail;
                        let municipality_name = store.municipality_name;
                        let license_status = store.license_status;
                        const min_age = store.min_age;
                        const max_age = store.max_age;
                        let age_note = ""
                        const note = store.note;

                        const license_status_dict = {
                            "returned": "運転免許を返納した方",
                            "expired": "運転免許を失効した方",
                            "not_held": "運転免許を所持していない方",
                            "any": "どなたでも可"
                        }

                        if (municipality_name === null) {
                            municipality_name = "熊本県にお住まいの方";
                        } else {
                            municipality_name = `${municipality_name} にお住まいの方`;
                        }

                        license_status = license_status_dict[license_status];

                        if ((max_age === null || max_age === "") && (min_age === null || min_age === "")) {
                            age_note = "年齢制限なし";
                        } else if ((max_age === null || max_age === "") && (min_age !== null && min_age !== "")) {
                            age_note = `${min_age} 歳以上の方`;
                        } else if ((max_age !== null && max_age !== "") && (min_age === null || min_age === "")) {
                            age_note = `${max_age} 歳以下の方`;
                        } else {
                            age_note = `${min_age}歳以上 ${max_age} 歳以下の方`;
                        }
                        const map_url = `https://www.google.com/maps/search/?api=1&query=${store_lat},${store_lon}&zoom=20`;
                        // モーダルウィンドウのHTML内容を作成
                        const modalHTML = `
                        <div>
                            <h5>${store_name}</h5>
                            <hr>
                            <div class="store-tel">
                                <i class="fa-solid fa-phone"></i><span>${store_tel}</span>
                            </div>
                            <div class="store-address">
                                <i class="fa-solid fa-location-dot"></i><span>${store.address}</span>
                            </div>
                            <div class="store-googlemap">
                                <i class="fa-solid fa-map-location-dot"></i>
                                <a href="${map_url}" target="_blank">Google Mapで開く</a>
                            </div>
                            <hr>
                            <div><strong>特典内容</strong></div>
                            <div>${store_detail}</div>
                            <hr>
                            <div><strong>特典対象者</strong></div>
                            <div>・ ${license_status}</div>
                            <div>・ ${age_note}</div>
                            <div>・ ${municipality_name}</div>
                            <hr>
                            <div><strong>備考</strong></div>
                            <div>${note}</div>
                        </div>
                        `;

                        // マーカーを作成
                        const store_marker = new maplibregl.Marker({
                            element: createCustomMarker(store_name)
                        })
                            .setLngLat([store_lon, store_lat])
                            .addTo(map);

                        store_marker.getElement().addEventListener('click', () => {
                            if (!isWaitingForInput_S && !isWaitingForInput_E) {
                                modalBody.innerHTML = modalHTML;
                                modal.style.display = 'block';
                            }
                        });

                        store_marker.getElement().dataset.storeId = store.id;
                        storeMarker.push(store_marker);
                    });
                } else {
                    console.error("エラー:", data.message);
                    alert("データの取得に失敗しました。");
                }
            })
            .catch((error) => {
                console.error("フェッチエラー:", error);
                alert("リクエストに失敗しました。");
            });
    });
    // 店舗表示アイコンのカスタム
    function createCustomMarker(storeName) {
        const markerElement = document.createElement('div');
        markerElement.className = 'custom-marker';

        // アイコン要素
        const icon = document.createElement('div');
        icon.className = 'icon-store';
        icon.innerHTML = '<i class="fa-solid fa-shop"></i>';
        markerElement.appendChild(icon);

        // 店名テキスト要素
        const label = document.createElement('div');
        label.className = 'label';
        label.textContent = storeName;
        label.style.display = 'none';
        markerElement.appendChild(label);

        map.on('zoom', function () {
            const zoomLevel = map.getZoom();
            if (zoomLevel < 12) {
                label.style.display = 'none';
                icon.style.width = '24px';
                icon.style.height = '24px';
                icon.style.fontSize = '12px';
            } else if (zoomLevel < 15) {
                label.style.display = 'none';
                icon.style.width = '30px';
                icon.style.height = '30px';
                icon.style.fontSize = '15px';
            } else {
                label.style.display = 'block';
                icon.style.width = '38px';
                icon.style.height = '38px';
                icon.style.fontSize = '19px';
            }
        });

        return markerElement;
    }

    // 地図の中心を指定の位置に移動
    function addCrossMarkerAndButton(type) {
        // ポップアップの初期化
        if (crossMarker && crossMarker.getPopup()) {
            crossMarker.getPopup().remove();
        }
        // 地図に十字マーカーを追加
        const crossMarkerElement = document.createElement('div');
        crossMarkerElement.className = 'cross-marker';

        crossMarker = new maplibregl.Marker({
            element: crossMarkerElement,
            anchor: 'center',
            draggable: true,
        })
            .setLngLat(map.getCenter())
            .addTo(map);

        // ボタンを作成
        const buttonContainer = document.createElement('div');
        buttonContainer.classList.add('popup-btn-container');
        buttonContainer.style.display = 'flex';
        buttonContainer.style.justifyContent = 'center';
        buttonContainer.style.height = '40px';

        const cancelButton = document.createElement('button');
        cancelButton.textContent = '戻る';
        cancelButton.classList.add('sidebar-btn-left');
        cancelButton.style.width = '80px';
        cancelButton.style.fontSize = '16px';
        cancelButton.onclick = () => cancelLocationSelection(crossMarker);

        const setButton = document.createElement('button');
        setButton.textContent = '設定';
        setButton.classList.add('sidebar-btn-right');
        setButton.style.width = '80px';
        setButton.style.fontSize = '16px';
        setButton.onclick = () => setLocationFromMap(crossMarker.getLngLat(), type, crossMarker);

        // ボタンをコンテナに追加
        buttonContainer.appendChild(cancelButton);
        buttonContainer.appendChild(setButton);

        // ボタンのポップアップ設定
        const popup = new maplibregl.Popup({ offset: 25 })
            .setDOMContent(buttonContainer);
        // ポップアップのCSS
        popup._content.style.width = '200px';
        popup._content.id = 'CrossMarker-popup';
        popup._content.querySelector('.maplibregl-popup-close-button').style.display = 'none';
        buttonContainer.style.gap = '10px';
        crossMarker.setPopup(popup);
        crossMarker.togglePopup();
        // ポップアップが閉じられたときにマーカーを非表示にする
        popup.on('close', function () {
            crossMarker.remove();
        });
        // マーカーをドラッグして位置を変更した際に地図の中心を更新
        crossMarker.on('dragend', function () {
            const newLngLat = crossMarker.getLngLat();
            setLocationFromMap(newLngLat, type, crossMarker);
        });
        // 地図を移動した際にマーカーをスムーズに中心に合わせて移動
        map.on('move', function () {
            const newCenter = map.getCenter();
            crossMarker.setLngLat(newCenter);
        });
        if (!sidebar.classList.contains('collapsed')) {
            toggleBtn.click();
        }
    }

    // 地図上の位置から選択
    async function setLocationFromMap(lngLat, type, crossMarker) {
        let locationData;
        if (type === "start") {
            locationData = await ReverseGeocoding(lngLat.lat, lngLat.lng);
            if (locationData) {
                // スタート地点のデータを表示および保存
                start_location.value = locationData.name;
                placeMarker(locationData.lat, locationData.lon, locationData.name, 'start');
                cancelLocationSelection(crossMarker);
                if (startMarker && endMarker) {
                    clearMarker('stop');
                    clearRoutes();
                    searchRouteEvent();
                }
            } else {
                alert('位置情報データが見つかりませんでした');
            }
        } else if (type === "end") {
            locationData = await ReverseGeocoding(lngLat.lat, lngLat.lng);
            if (locationData) {
                // ゴール地点のデータを表示および保存
                end_location.value = locationData.name;
                placeMarker(locationData.lat, locationData.lon, locationData.name, 'end');
                cancelLocationSelection(crossMarker);
                if (startMarker && endMarker) {
                    clearMarker('stop');
                    clearRoutes();
                    searchRouteEvent();
                }
            } else {
                alert('位置情報データが見つかりませんでした');
            }
        }

    }
    // キャンセルボタンの処理
    function cancelLocationSelection(crossMarker) {
        crossMarker.togglePopup();
        crossMarker.remove();
        if (sidebar.classList.contains('collapsed')) {
            toggleBtn.click();
        }
    }
});

