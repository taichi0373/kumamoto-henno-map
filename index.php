<?php
session_start();
$token = '';
if (isset($_SESSION['user_id'])) {
    $token = base64_encode($_SESSION['user_id']);
}
?>
<script>
    const userToken = "<?php echo $token; ?>";
</script>
<!DOCTYPE html>
<html lang="ja">

<head>
    <meta charset="utf-8" name="viewport"
        content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" http-equiv="X-UA-Compatible"
        ontent="ie=edge" />
    <title>運転免許返納特典マップ</title>

    <!-- CDN Bootstrapを読み込む -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet"
        integrity="sha384-9ndCyUaIbzAi2FUVXJi0CjmCapSmO7SnpJef0486qhLnuZ2cdeRhO02iuK6FUUVM" crossorigin="anonymous" />
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" />

    <!-- MapLibre GL JS -->
    <script src='https://unpkg.com/maplibre-gl/dist/maplibre-gl.js'></script>
    <link href='https://unpkg.com/maplibre-gl/dist/maplibre-gl.css' rel='stylesheet' />

    <!-- fontawesomeの読み込み -->
    <link href="https://use.fontawesome.com/releases/v6.7.1/css/all.css" rel="stylesheet">

    <!-- CSSファイルを読み込む -->
    <link rel="stylesheet" href="./css/base.css" />
    <link rel="stylesheet" href="./css/index.css" />
    <link rel="stylesheet" href="./css/store_icon.css" />
</head>

<body>
    <!-- モーダルウィンドウ -->
    <div id="modal" class="modal">
        <div class="modal-content">
            <!-- <span id="closeModal" class="close">&times;</span> -->
            <button id="closeModal" class="close"><i class="fa-solid fa-xmark"></i></button>
            <div id="modalBody"></div>
        </div>
    </div>

    <header>
        <!-- PC用 -->
        <nav class="navbar navbar-expand-lg bg-body-tertiary" data-bs-theme="dark" id="pc-nav">
            <div class="container-fluid">
                <a class="navbar-brand ms-2" href="#">運転免許返納特典マップ</a>
                <div class="collapse navbar-collapse" id="navbarSupportedContent">
                    <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                        <li class="nav-item ms-2 me-2">
                            <a class="nav-link" href="/navi_project/index.php">ホーム</a>
                        </li>
                        <li class="nav-item ms-2 me-2">
                            <a class="nav-link" href="/navi_project/register.php">新規登録</a>
                        </li>
                        <?php if (!isset($_SESSION['user_id'])): ?>
                            <li class="nav-item ms-2 me-2">
                                <a class="nav-link" href="/navi_project/login.php">ログイン</a>
                            </li>
                        <?php endif; ?>
                        <?php if (isset($_SESSION['user_id'])): ?>
                            <li class="nav-item ms-2 me-2">
                                <a class="nav-link" href="/navi_project/logout.php">ログアウト</a>
                            </li>
                        <?php endif; ?>

                    </ul>
                    <ul class="navbar-nav ms-auto mb-2 mb-lg-0">
                        <?php if (isset($_SESSION['user_id'])): ?>
                            <li class="nav-item dropdown">
                                <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown"
                                    aria-expanded="false">
                                    メニュー
                                </a>
                                <ul class="dropdown-menu">
                                    <li class="nav-item ms-2 me-2">
                                        <a class="nav-link" href="/navi_project/profile.php">プロフィール編集</a>
                                    </li>
                                </ul>
                            </li>
                            <li class="nav-item ms-2 me-2" style="min-width: 70px">
                                <span
                                    class="<?php echo isset($_SESSION['username']) && $_SESSION['username'] !== '' ? 'nav-link active' : 'nav-link'; ?>">
                                    <?php echo isset($_SESSION['username']) ? htmlspecialchars($_SESSION['username']) : ''; ?>
                                </span>
                            </li>
                        <?php endif; ?>
                    </ul>
                </div>
            </div>
        </nav>
        <!-- スマホ用 -->
        <nav class="navbar navbar-dark bg-dark fixed-top" id="sm-nav">
            <div class="container-fluid">
                <a class="navbar-brand ms-2 header-title-sm" href="#">運転免許返納特典マップ</a>

                <button class="navbar-toggler" type="button" data-bs-toggle="offcanvas" data-bs-target="#offcanvasDarkNavbar"
                    aria-controls="offcanvasDarkNavbar" aria-label="Toggle navigation">
                    <span class="navbar-toggler-icon"></span>
                </button>
                <div class="offcanvas offcanvas-end text-bg-dark m-0" tabindex="-1" id="offcanvasDarkNavbar"
                    aria-labelledby="offcanvasDarkNavbarLabel">
                    <div class="offcanvas-header d-flex justify-content-between align-items-center">
                        <h5 class="offcanvas-title" id="offcanvasDarkNavbarLabel">メニュー</h5>
                        <?php if (isset($_SESSION['username'])): ?>
                            <span class="text-white ms-3 nav-link-sm">
                                <?php echo htmlspecialchars($_SESSION['username']); ?>
                            </span>
                        <?php endif; ?>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="offcanvas"
                            aria-label="Close"></button>
                    </div>
                    <div class="offcanvas-body">
                        <ul class="navbar-nav">
                            <li class="nav-item ms-2 me-2">
                                <a class="nav-link nav-link-sm" href="/navi_project/index.php">ホーム</a>
                            </li>
                            <li class="nav-item ms-2 me-2">
                                <a class="nav-link nav-link-sm" href="/navi_project/register.php">新規登録</a>
                            </li>
                            <?php if (!isset($_SESSION['user_id'])): ?>
                                <li class="nav-item ms-2 me-2">
                                    <a class="nav-link nav-link-sm" href="/navi_project/login.php">ログイン</a>
                                </li>
                            <?php endif; ?>
                            <?php if (isset($_SESSION['user_id'])): ?>
                                <li class="nav-item ms-2 me-2">
                                    <a class="nav-link nav-link-sm" href="/navi_project/logout.php">ログアウト</a>
                                </li>
                                <li class="nav-item ms-2 me-2">
                                    <a class="nav-link nav-link-sm" href="/navi_project/profile.php">プロフィール編集</a>
                                </li>
                            <?php endif; ?>
                        </ul>
                    </div>
                </div>
            </div>
        </nav>
    </header>
    <!-- PC用 -->
    <main>
        <!-- sidebar -->
        <div class="sidebar" id="sidebar">
            <div class="sidebar-tabs">
                <button type="button" class="tab-button active" id="route-guidance-tab"
                    data-tab="route-guidance-page">経路案内</button>
                <button type="button" class="tab-button" id="users-benefit-tab"
                    data-tab="users-benefit-page">利用できる特典</button>
                <button type="button" class="tab-button" id="search-benefit-tab"
                    data-tab="search-benefit-page">特典を探す</button>
            </div>

            <!-- 経路案内ページ -->
            <div class="sidebar-page" id="route-guidance-page">
                <!-- 条件入力フォーム -->
                <div class="form-container">
                    <form action="">
                        <div class="form-input-container">
                            <div class="form-input">
                                <label for="transport-mode">交通手段</label>
                                <select class="form-select" id="transport-mode" required>
                                    <option value="TRANSIT, WALK" selected>公共交通機関</option>
                                    <option value="RAIL, TRAM, WALK">電車</option>
                                    <option value="BUS, WALK">バス</option>
                                    <option value="WALK">徒歩</option>
                                    <option value="BICYCLE">自転車</option>
                                </select>
                            </div>
                        </div>

                        <div class="form-input-container">
                            <div class="form-input" id="start-location-div">
                                <label for="start-location">出発地</label>
                                <input type="text" class="form-control" id="start-location" placeholder="出発地を入力してください" required>
                                <ul class="list-group" id="start-suggestions-ex" style="display:none;"></ul>
                                <ul class="list-group" id="start-suggestions" style="display:none;"></ul>
                            </div>
                        </div>

                        <div class="form-input-container" id="end-location-div">
                            <div class="form-input">
                                <label for="end-location">目的地</label>
                                <input type="text" class="form-control" name="end-location" id="end-location"
                                    placeholder="目的地を入力してください" required>
                                <ul class="list-group" id="end-suggestions-ex" style="display:none;"></ul>
                                <ul class="list-group" id="end-suggestions" style="display:none;"></ul>
                            </div>
                        </div>

                        <div class="expand-trigger">条件指定<i class="bi bi-chevron-down icon-expand-trigger"></i></div>
                        <div class="expand-trigger-container">
                            <div class="form-input-container">
                                <div class="form-input-half">
                                    <label for="time-select">時刻指定</label>
                                    <select class="form-select" id="time-select">
                                        <option value="departure">出発</option>
                                        <option value="arrival">到着</option>
                                    </select>
                                </div>
                            </div>

                            <div class="form-input-container" style="margin-top: 10px;">
                                <div class="form-input-half">
                                    <label for="date">日付</label>
                                    <input class="form-control" type="date" id="date">
                                </div>

                                <div class="form-input-half">
                                    <label for="time">時刻</label>
                                    <input class="form-control" type="time" id="time">
                                </div>
                            </div>
                        </div>
                    </form>

                </div>
                <!-- 経路探索結果 -->
                <div class="result-container" id="search-route-result">
                    <ul class="route-list" id="route-list">
                    </ul>
                </div>
            </div>

            <!-- 利用できる特典ページ -->
            <div class="sidebar-page" id="users-benefit-page" style="display: none;">
                <div class="result-container" id="users-benefit-result">
                </div>
            </div>

            <!-- 特典を探すページ -->
            <div class="sidebar-page" id="search-benefit-page" style="display: none;">
                <div class="form-container">
                    <form action="">
                        <div class="form-input-container">
                            <span style="margin-left: 8px;">検索条件で絞り込む</span>
                        </div>
                        <div class="form-input-container">
                            <div class="form-input">
                                <label for="address">居住地域</label>
                                <select class="form-select" id="address" required>
                                    <option disabled selected value>選択してください</option>
                                    <option value="431001">熊本市</option>
                                    <option value="432024">八代市</option>
                                    <option value="432032">人吉市</option>
                                    <option value="432041">荒尾市</option>
                                    <option value="432059">水俣市</option>
                                    <option value="432067">玉名市</option>
                                    <option value="432083">山鹿市</option>
                                    <option value="432105">菊池市</option>
                                    <option value="432113">宇土市</option>
                                    <option value="432121">上天草市</option>
                                    <option value="432130">宇城市</option>
                                    <option value="432148">阿蘇市</option>
                                    <option value="432156">天草市</option>
                                    <option value="432164">合志市</option>
                                    <option value="433489">美里町</option>
                                    <option value="433641">玉東町</option>
                                    <option value="433675">南関町</option>
                                    <option value="433683">長洲町</option>
                                    <option value="433691">和水町</option>
                                    <option value="434035">大津町</option>
                                    <option value="434043">菊陽町</option>
                                    <option value="434230">南小国町</option>
                                    <option value="434248">小国町</option>
                                    <option value="434256">産山村</option>
                                    <option value="434281">高森町</option>
                                    <option value="434329">西原村</option>
                                    <option value="434337">南阿蘇村</option>
                                    <option value="434418">御船町</option>
                                    <option value="434426">嘉島町</option>
                                    <option value="434434">益城町</option>
                                    <option value="434442">甲佐町</option>
                                    <option value="434477">山都町</option>
                                    <option value="434680">氷川町</option>
                                    <option value="434825">芦北町</option>
                                    <option value="434841">津奈木町</option>
                                    <option value="435015">錦町</option>
                                    <option value="435058">多良木町</option>
                                    <option value="435066">湯前町</option>
                                    <option value="435074">水上村</option>
                                    <option value="435104">相良村</option>
                                    <option value="435112">五木村</option>
                                    <option value="435121">山江村</option>
                                    <option value="435139">球磨村</option>
                                    <option value="435147">あさぎり町</option>
                                    <option value="435317">苓北町</option>
                                </select>
                            </div>
                        </div>
                        <div class="form-input-container">
                            <div class="form-input">
                                <label for="license-status">運転免許の所持状況</label>
                                <select class="form-select" id="license-status" required>
                                    <option disabled selected value>選択してください</option>
                                    <option value="licensed">所持している</option>
                                    <option value="unlicensed">所持していない</option>
                                    <option value="returned">返納済み</option>
                                    <option value="suspension">停止している</option>
                                    <option value="expired">失効している</option>
                                </select>
                            </div>
                        </div>
                        <div class="form-input-container">
                            <div class="form-input">
                                <label for="age">年齢</label>
                                <input type="number" class="form-control" id="age" placeholder="年齢を入力してください" required>
                            </div>
                        </div>
                        <div class="btn-container">
                            <button type="button" class="sidebar-btn-left" id="clear-benefit-condition">条件をクリア</button>
                            <button type="submit" class="sidebar-btn-right" id="search-benefit-btn">特典を検索</button>
                        </div>
                    </form>
                </div>
                <div class="result-container" id="search-benefit-result">
                </div>
            </div>

            <!-- 出典・ライセンス情報の記載 -->
            <div class="license-container">
                <p>
                    出典:
                    <a href="https://www.pref.kumamoto.jp/soshiki/54/51729.html?type=top" target="_blank">
                        「運転免許証自主返納者への特典」のご紹介
                    </a>
                </p>
                <p style="margin-bottom: 4px;"> ※ 熊本県ホームページをもとに作成</p>
                <div style="margin-bottom: 4px;">
                    <!-- Begin Yahoo! JAPAN Web Services Attribution Snippet -->
                    <span style="margin:0px"><a href="https://developer.yahoo.co.jp/sitemap/" target="_blank">Webサービス by Yahoo! JAPAN</a></span>
                    <!-- End Yahoo! JAPAN Web Services Attribution Snippet -->
                </div>

                <!-- 出典・ライセンス情報のボタン -->
                <button id="licenseButton">その他ライセンス情報</button>
                <!-- ポップアップ -->
                <div id="licensePopup" style="display: none; position: fixed; top: 50%; left: 50%; transform: translate(-50%, -50%); background: white; padding: 14px 20px 30px 20px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2); border-radius: 8px; width: 100%; max-width: 350px; z-index: 1000;">
                    <div>
                        <div style="margin-bottom: 10px; font-size: 18px;">その他ライセンス情報</div>
                        <p style="margin-bottom: 10px;">
                            本システムは、以下のオープンソースソフトウェア（OSS）を使用しています。
                        <ul style="margin: 0px 0px 10px 0px; padding-left: 20px;">
                            <li>
                                <a href="https://www.opentripplanner.org" target="_blank">OpenTripPlanner</a><br>
                                ライセンス：
                                <a href="https://www.apache.org/licenses/LICENSE-2.0" target="_blank">Apache License 2.0</a>
                                ・
                                <a href="https://www.gnu.org/licenses/lgpl-3.0.html" target="_blank">GNU LGPL v3</a>
                            </li>
                        </ul>
                        </p>
                        <p>本システムは、以下のオープンデータを使用しています。</p>
                        <ul style="margin: 0px 0px 10px 0px; padding-left: 20px; list-style-type: decimal;">
                            <li>
                                <a href="https://km.bus-vision.jp/kumamoto/view/opendataKuma.html" target="_blank">バスきたくまさん</a><br>
                                ライセンス：<a href="https://creativecommons.org/licenses/by/4.0/" target="_blank">CC BY 4.0</a><br>
                                <ul style="margin: 0px; padding-left: 14px; list-style-type: disc;">
                                    <li>
                                        産交バス：https://www.kyusanko.co.jp/sankobus
                                    </li>
                                    <li>
                                        熊本電鉄バス：https://www.kumamotodentetsu.co.jp/bus
                                    <li>
                                        熊本バス：https://www.kuma-bus.co.jp
                                    </li>
                                    <li>
                                        熊本都市バス：https://www.kumamoto-toshibus.co.jp
                                    </li>
                                </ul>
                            </li>
                            <li>
                                <a href="https://gtfs-data.jp/" target="_blank">GTFSデータ公開リポジトリ</a><br>
                                ライセンス：<a href="https://creativecommons.org/licenses/by/2.1/jp/" target="_blank">CC BY 2.1 JP</a><br>
                                <ul style="margin: 0px; padding-left: 14px; list-style-type: disc;">
                                    <li>
                                        熊本市交通局：http://www.kotsu-kumamoto.jp
                                    </li>
                                </ul>
                                ライセンス：<a href="https://creativecommons.org/publicdomain/zero/1.0/deed.ja" target="_blank">CC0 1.0</a><br>
                                <ul style="margin: 0px; padding-left: 14px; list-style-type: disc;">
                                    <li>
                                        熊本電鉄：https://www.kumamotodentetsu.co.jp
                                    </li>
                                </ul>
                            </li>
                            <li>
                                <a href="https://gtfs-gis.jp/gtfs4research/" target="_blank">研究用GTFSデータ公開ページ</a><br>
                                JR九州および南阿蘇鉄道のデータは、研究者によって公開された非公式のGTFSデータを利用しています。
                                調査・研究目的に限る利用条件に基づき使用しています。
                            </li>
                        </ul>
                        <button id="closePopup" style="position: absolute; bottom: 10px; right: 10px; padding: 6px 12px;">閉じる</button>
                    </div>
                </div>
                <div id="popupOverlay" style="display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0, 0, 0, 0.5); z-index: 999;"></div>
                <script>
                    // 出典・ライセンス情報の表示・非表示
                    const licenseButton = document.getElementById('licenseButton');
                    const licensePopup = document.getElementById('licensePopup');
                    const popupOverlay = document.getElementById('popupOverlay');
                    const closePopup = document.getElementById('closePopup');

                    licenseButton.addEventListener('click', () => {
                        licensePopup.style.display = 'block';
                        popupOverlay.style.display = 'block';
                    });

                    closePopup.addEventListener('click', () => {
                        licensePopup.style.display = 'none';
                        popupOverlay.style.display = 'none';
                    });

                    popupOverlay.addEventListener('click', () => {
                        licensePopup.style.display = 'none';
                        popupOverlay.style.display = 'none';
                    });
                </script>
            </div>
        </div>

        <!-- map -->
        <div id="map">
            <button id="sidebar-toggle" class="sidebar-toggle-btn">
                <i class="fa-solid fa-caret-left"></i>
            </button>
            <div class="map-button-container">
                <button class="map-button" id="display-store">
                    <i class="fa-solid fa-shop"></i> 支援協賛店
                </button>
                <a href="support_info.php">
                    <button class="map-button" id="info-about-support">
                        <i class="fa-solid fa-info-circle"></i> 自主返納支援制度とは
                    </button>
                </a>
            </div>
        </div>

    </main>
    <script src="js/base.js"></script>
    <script src="js/index.js"></script>
    <script src="js/users_benefit.js"></script>
    <script src="js/search_benefit.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-geWF76RCwLtnZ8qwWowPQNguL3RmwHVBC9FhGdlKrxdiJJigb/j/68SIy3Te4Bkz"
        crossorigin="anonymous"></script>
    <script>
        function scrollToBenefit(benefitID) {
            users_benefit_tab.click();
            const targetBenefit = document.querySelector(`[data-benefit-id="${benefitID}"]`);
            if (targetBenefit) {
                // スクロールして画面中央に配置
                targetBenefit.scrollIntoView({
                    behavior: 'smooth',
                    block: 'center'
                });
                targetBenefit.style.transition = 'border 0.4s ease, background-color 0.5s ease';
                targetBenefit.style.border = '3px solid #007bff';
                setTimeout(() => {
                    targetBenefit.style.border = '';
                    targetBenefit.style.backgroundColor = '';
                }, 2000);
            }
        }
    </script>
</body>

</html>