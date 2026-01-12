<?php
session_start();
?>
<!DOCTYPE html>
<html lang="ja">

<head>
    <meta charset="utf-8" name="viewport"
        content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" http-equiv="X-UA-Compatible"
        ontent="ie=edge">
    <title>運転免許返納特典マップ</title>

    <!-- CDN Bootstrapを読み込む -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet"
        integrity="sha384-9ndCyUaIbzAi2FUVXJi0CjmCapSmO7SnpJef0486qhLnuZ2cdeRhO02iuK6FUUVM" crossorigin="anonymous">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">

    <!-- CSSファイルを読み込む -->
    <link rel="stylesheet" href="./css/base.css">
    <link rel="stylesheet" href="./css/support_info.css">
</head>

<body>
    <header>
        <!-- PC用 -->
        <nav class="navbar navbar-expand-lg bg-body-tertiary" data-bs-theme="dark" id="pc-nav">
            <div class="container-fluid">
                <a class="navbar-brand ms-2" href="/navi_project/index.php">運転免許返納特典マップ</a>
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
                <a class="navbar-brand ms-2 header-title-sm" href="/navi_project/index.php">運転免許返納特典マップ</a>

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
    <main>
        <div class="container-main">
            <div class="container">
                <div class="container-section">
                    <section>
                        <h2>運転免許の自主返納支援制度とは</h2>
                        <p>
                            　運転免許証を自主返納した高齢者が、自家用車に依存することなく充実した生活を続けられるように、各自治体ごとに協賛する店舗や施設で様々な特典・サービスを受けられる制度です。
                            熊本県内で自主返納支援制度の特典を受けるためには、「運転経歴証明書」、「申請による運転免許の取消通知書」、「免許返納者割引乗車証」のいずれかを提示する必要があります。
                        </p>
                        <h3>主な対象者 <span style="font-size: 0.8em; font-weight:normal;">※特典により異なる場合があります</span></h3>
                        <ul style="margin-bottom: 0px;">
                            <li>熊本県内にお住まいの方</li>
                            <li>65歳以上の方</li>
                            <li>運転免許を自主返納された方及び運転免許を失効された方</li>
                        </ul>
                        <br>
                        <h3>特典を受ける流れ</h3>
                        <img src="css/img/support_info_flow_sm.png" alt="特典を受ける流れ" class="flow-image
                        " style="margin: 0px;">
                    </section>
                </div>
                <div class="container-section">
                    <section>
                        <h2>「運転経歴証明書」について</h2>
                        <p>　運転経歴証明書とは、運転免許を自主返納した方や、失効した方が過去の運転経歴を証明するために取得できる公的な証明書です。
                            平成24年4月1日以降に交付された運転経歴証明書は公的な身分証明書として生涯使用することができます。
                            この証明書を提示することで、様々な特典・サービスを受けることができます。</p>
                        <h3>要件</h3>
                        <ul>
                            <li>本人による申請であること</li>
                            <li>自主返納後5年以内の方</li>
                            <li>運転免許証を失効された方で、失効した日から5年以内の方</li>
                        </ul>

                        <h3>受付場所</h3>
                        <ul>
                            <li>
                                運転免許センター【即日交付】
                            </li>

                            <li>
                                警察署【後日交付：約2週間後】
                            </li>

                            <li>
                                氷川幹部交番【後日交付：約2週間後】
                            </li>
                        </ul>

                        <h3>手数料</h3>
                        <p>1,100円</p>

                        <h3>必要なもの</h3>
                        <ul>
                            <li>身分証明書 ※コピー不可
                                <ul style="list-style-type: square;">
                                    <li>健康保険証</li>
                                    <li>マイナンバーカード（通知カードは不可）</li>
                                    <li>パスポート</li>
                                    <li>社員証など</li>
                                </ul>
                            </li>
                            <li>写真1枚［縦 3cm × 横 2.4cm］（6ヶ月以内に撮影したもの、無帽、無背景、正面）
                            </li>

                        </ul>
                        <p>
                            ※詳細は<a href="https://www.pref.kumamoto.jp/site/police/8706.html" target="_blank">熊本県ホームページ</a>をご確認ください。
                        </p>
                    </section>
                </div>
                <div class="container-section">
                    <section>
                        <h2>「免許返納者割引乗車証」について</h2>
                        <p>　免許返納者割引乗車証とは、熊本県内にお住まいの65歳以上の方が運転免許を自主返納または失効された場合に発行できる証明書です。
                            免許返納者割引乗車証を提示することで、割引対象路線の運賃割引制度を利用することができます。</p>
                        <h3>割引対象路線</h3>
                        <ul>
                            <li>九州産交バス</li>
                            <li>熊本バス</li>
                            <li>熊本電鉄バス</li>
                            <li>熊本都市バス</li>
                            <li>熊本市電</li>
                            <li>熊本電鉄</li>
                        </ul>
                        <h3>割引内容</h3>
                        <p>普通旅客運賃の半額（10 円未満切り上げ）</p>
                        <h3>発行方法</h3>
                        <p>事業者の窓口等で申請</p>
                        <ul>
                            <li>必要なもの（以下2点）
                                <ul style="list-style-type: square;">
                                    <li>申請による運転免許の取り消し通知書</li>
                                    <li>運転経歴証明書 または 運転経歴証明書が交付済みであることを表示する
                                        シールが貼られたマイナンバーカード</li>
                                </ul>
                            </li>
                            <li>顔写真1枚［縦 3cm × 横 2.5cm］（6ヶ月以内に撮影したもの）</li>
                        </ul>

                        <h3>有効期間</h3>
                        <p>『免許返納者割引乗車証』の取得日から2年（以降2年毎の更新）
                        </p>
                        <p>
                            ※詳細は<a href="http://www.kotsu-kumamoto.jp/one_html3/pub/Default.aspx?c_id=69" target="_blank">熊本市交通局ホームページ</a>をご確認ください。
                        </p>
                    </section>
                </div>
                <div class="container-footer">
                    <span style="margin-bottom: 5px;">出典: <a href="https://www.pref.kumamoto.jp/soshiki/54/51729.html?type=top" target="_blank">「運転免許証自主返納者への特典」のご紹介</a></span>
                    <span>※ 熊本県ホームページをもとに作成</span>
                </div>
            </div>
        </div>
    </main>
    <script src="js/base.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-geWF76RCwLtnZ8qwWowPQNguL3RmwHVBC9FhGdlKrxdiJJigb/j/68SIy3Te4Bkz"
        crossorigin="anonymous"></script>
</body>

</html>