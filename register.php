<?php
ob_start();
session_start();
include_once './php/db_connection.php';
$mysqli = connectToDatabase($servername, $username, $password, $dbname);

// 登録ボタンがクリックされたとき
if (isset($_POST['register'])) {
    $username = $mysqli->real_escape_string($_POST['username']);
    $password = $mysqli->real_escape_string($_POST['password']);
    $address = $mysqli->real_escape_string($_POST['address']);
    $birth_year = intval($_POST['birth-year']);
    $birth_month = intval($_POST['birth-month']);
    $birth_day = intval($_POST['birth-day']);
    $license_status = $mysqli->real_escape_string($_POST['license-status']);

    // 入力チェック
    if (mb_strlen($username) > 11) {
        echo "<script>alert('ユーザー名は11文字以下で入力してください。');</script>";
    } else if (!preg_match('/^[A-Za-z0-9]*$/', $password)) {
        echo "<script>alert('パスワードは半角英数字で入力してください。');</script>";
    } else if (strlen($password) < 6 || strlen($password) > 11) {
        echo "<script>alert('パスワードは6文字以上、11文字以下で入力してください。');</script>";
    } else if (!checkdate($birth_month, $birth_day, $birth_year)) {
        echo "<script>alert('正しい生年月日を入力してください。');</script>";
    } else {
        // パスワードをハッシュ化
        $hashed_password = password_hash($password, PASSWORD_DEFAULT);
        // 生年月日を結合
        $birth_date = "$birth_year-$birth_month-$birth_day";

        // データベースにユーザ情報を挿入
        $query = "INSERT INTO users (username, password, address, birth_date, license_status) VALUES (?, ?, ?, ?, ?)";
        $stmt = $mysqli->prepare($query);
        $stmt->bind_param("sssss", $username, $hashed_password, $address, $birth_date, $license_status);

        if ($stmt->execute()) {
            echo "<script>alert('登録が完了しました');</script>";
            header("Location: login.php");
            exit();
        } else {
            echo "<script>alert('登録に失敗しました');</script>";
        }

        $stmt->close();
    }
}
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
    <link rel="stylesheet" href="./css/form.css">
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
            <div class="form-container">
                <form class="form" id="formRegister" method="post">
                    <!-- csrfトークンを追加 -->
                    <input id="csrf_token" name="csrf_token" type="hidden"
                        value="IjBkMTliYzNkYzI2YmEzOWVlMmUwYmEwZmRhZjZmMDk4MWI0Njc5ZDIi.ZWWKLQ.rCAuOjmvzPpS4gqT2D6ZGWvkycg">

                    <h3 class="form-title">新規登録</h3>

                    <div class="input-group">
                        <div class="form-floating">
                            <input type="text" class="form-control" name="username" id="username" placeholder="ユーザー名" required>
                            <label for="username">ユーザー名</label>
                        </div>
                    </div>

                    <div class="input-group">
                        <div class="form-floating">
                            <input type="password" class="form-control" name="password" id="password" data-toggle="password"
                                placeholder="パスワード" required>
                            <label for="password">パスワード</label>
                        </div>
                    </div>

                    <div class="input-group">
                        <div class="form-floating">
                            <select class="form-select" name="address" id="address" required>
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
                            <label for="address">居住地域</label>
                        </div>
                    </div>

                    <div class="custom-input-container">
                        <label for="birth-year" class="custom-label">生年月日</label>
                        <div class="custom-input-group">
                            <div class="custom-input-group-item">
                                <select class="form-select" name="birth-year" id="birth-year" required>
                                </select>
                                <span style="color: #495057;">年</span>
                            </div>
                            <script>
                                // 年のセレクトボックスを自動生成
                                const currentYear = new Date().getFullYear(); // 現在の年を取得
                                const startYear = 1900; // 開始年
                                const selectYear = document.getElementById("birth-year");
                                for (let year = currentYear; year >= startYear; year--) {
                                    const option = document.createElement("option");
                                    option.value = year;
                                    option.textContent = year;
                                    selectYear.appendChild(option);
                                }
                            </script>
                            <div class="custom-input-group-item">
                                <select class="form-select" name="birth-month" id="birth-month" style="width: auto;" required>
                                    <option value="1">1</option>
                                    <option value="2">2</option>
                                    <option value="3">3</option>
                                    <option value="4">4</option>
                                    <option value="5">5</option>
                                    <option value="6">6</option>
                                    <option value="7">7</option>
                                    <option value="8">8</option>
                                    <option value="9">9</option>
                                    <option value="10">10</option>
                                    <option value="11">11</option>
                                    <option value="12">12</option>
                                </select>
                                <span style="color: #495057;">月</span>
                            </div>
                            <div class="custom-input-group-item">
                                <select class="form-select" name="birth-day" id="birth-day" style="width: auto;" required>
                                    <option value="1">1</option>
                                    <option value="2">2</option>
                                    <option value="3">3</option>
                                    <option value="4">4</option>
                                    <option value="5">5</option>
                                    <option value="6">6</option>
                                    <option value="7">7</option>
                                    <option value="8">8</option>
                                    <option value="9">9</option>
                                    <option value="10">10</option>
                                    <option value="11">11</option>
                                    <option value="12">12</option>
                                    <option value="13">13</option>
                                    <option value="14">14</option>
                                    <option value="15">15</option>
                                    <option value="16">16</option>
                                    <option value="17">17</option>
                                    <option value="18">18</option>
                                    <option value="19">19</option>
                                    <option value="20">20</option>
                                    <option value="21">21</option>
                                    <option value="22">22</option>
                                    <option value="23">23</option>
                                    <option value="24">24</option>
                                    <option value="25">25</option>
                                    <option value="26">26</option>
                                    <option value="27">27</option>
                                    <option value="28">28</option>
                                    <option value="29">29</option>
                                    <option value="30">30</option>
                                    <option value="31">31</option>
                                </select>
                                <span style="color: #495057;">日</span>
                            </div>
                        </div>
                    </div>

                    <div class="input-group">
                        <div class="form-floating">
                            <select class="form-select" name="license-status" id="license-status" required>
                                <option disabled selected value>選択してください</option>
                                <option value="licensed">所持している</option>
                                <option value="unlicensed">所持していない</option>
                                <option value="returned">返納済み</option>
                                <option value="suspension">停止している</option>
                                <option value="expired">失効している</option>
                            </select>
                            <label for="license-status">運転免許の所持状況</label>
                        </div>
                    </div>
                    <div class="btn-group">
                        <button class="form-btn" type="button" id="btn-back">戻る</button>
                        <button class="form-btn" type="submit" id="btn-register" name="register">登録</button>
                    </div>
                </form>
            </div>
        </div>
    </main>
    <script src="js/base.js"></script>
    <script src="js/register.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-geWF76RCwLtnZ8qwWowPQNguL3RmwHVBC9FhGdlKrxdiJJigb/j/68SIy3Te4Bkz"
        crossorigin="anonymous"></script>
</body>

</html>