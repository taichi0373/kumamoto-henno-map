<?php
ob_start();
session_start();
if (!isset($_SESSION['user_id'])) {
    header("Location: login.php");
    exit();
} else {
    include_once './php/db_connection.php';
    $mysqli = connectToDatabase($servername, $username, $password, $dbname);

    // ユーザー情報を取得
    $user_id = $_SESSION['user_id'];
    $query = "SELECT username, address, birth_date, license_status FROM users WHERE id = ?";
    $stmt = $mysqli->prepare($query);
    $stmt->bind_param("i", $user_id);
    $stmt->execute();
    $result = $stmt->get_result();
    $user = $result->fetch_assoc();

    $stmt->close();
    $mysqli->close();
}

if (isset($_POST['save'])) {
    include_once './php/db_connection.php';
    $mysqli = connectToDatabase($servername, $username, $password, $dbname);

    // ユーザー情報を更新
    $username = $_POST['username'];
    $address = $_POST['address'];
    $birth_date = $_POST['birth-date'];
    $license_status = $_POST['license-status'];
    $query = "UPDATE users SET username = ?, address = ?, birth_date = ?, license_status = ? WHERE id = ?";
    $stmt = $mysqli->prepare($query);
    $stmt->bind_param("sissi", $username, $address, $birth_date, $license_status, $user_id);
    $stmt->execute();

    $stmt->close();
    $mysqli->close();
    header("Location: index.php");
    exit();
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
                <form class="form" id="formProfileEdit" method="post">
                    <!-- csrfトークンを追加 -->
                    <input id="csrf_token" name="csrf_token" type="hidden"
                        value="IjBkMTliYzNkYzI2YmEzOWVlMmUwYmEwZmRhZjZmMDk4MWI0Njc5ZDIi.ZWWKLQ.rCAuOjmvzPpS4gqT2D6ZGWvkycg">

                    <h3 class="form-title">プロフィール編集</h3>

                    <div class="input-group">
                        <div class="form-floating">
                            <input type="text" class="form-control" name="username" id="username" placeholder="ユーザー名" value="<?php echo htmlspecialchars($user['username']); ?>" required>
                            <label for="username">ユーザー名</label>
                        </div>
                    </div>

                    <div class="input-group">
                        <div class="form-floating">
                            <select class="form-select" name="address" id="address" required>
                                <option disabled selected value>選択してください</option>
                                <option value="431001" <?php echo $user['address'] == '431001' ? 'selected' : ''; ?>>熊本市</option>
                                <option value="432024" <?php echo $user['address'] == '432024' ? 'selected' : ''; ?>>八代市</option>
                                <option value="432032" <?php echo $user['address'] == '432032' ? 'selected' : ''; ?>>人吉市</option>
                                <option value="432041" <?php echo $user['address'] == '432041' ? 'selected' : ''; ?>>荒尾市</option>
                                <option value="432059" <?php echo $user['address'] == '432059' ? 'selected' : ''; ?>>水俣市</option>
                                <option value="432067" <?php echo $user['address'] == '432067' ? 'selected' : ''; ?>>玉名市</option>
                                <option value="432083" <?php echo $user['address'] == '432083' ? 'selected' : ''; ?>>山鹿市</option>
                                <option value="432105" <?php echo $user['address'] == '432105' ? 'selected' : ''; ?>>菊池市</option>
                                <option value="432113" <?php echo $user['address'] == '432113' ? 'selected' : ''; ?>>宇土市</option>
                                <option value="432121" <?php echo $user['address'] == '432121' ? 'selected' : ''; ?>>上天草市</option>
                                <option value="432130" <?php echo $user['address'] == '432130' ? 'selected' : ''; ?>>宇城市</option>
                                <option value="432148" <?php echo $user['address'] == '432148' ? 'selected' : ''; ?>>阿蘇市</option>
                                <option value="432156" <?php echo $user['address'] == '432156' ? 'selected' : ''; ?>>天草市</option>
                                <option value="432164" <?php echo $user['address'] == '432164' ? 'selected' : ''; ?>>合志市</option>
                                <option value="433489" <?php echo $user['address'] == '433489' ? 'selected' : ''; ?>>美里町</option>
                                <option value="433641" <?php echo $user['address'] == '433641' ? 'selected' : ''; ?>>玉東町</option>
                                <option value="433675" <?php echo $user['address'] == '433675' ? 'selected' : ''; ?>>南関町</option>
                                <option value="433683" <?php echo $user['address'] == '433683' ? 'selected' : ''; ?>>長洲町</option>
                                <option value="433691" <?php echo $user['address'] == '433691' ? 'selected' : ''; ?>>和水町</option>
                                <option value="434035" <?php echo $user['address'] == '434035' ? 'selected' : ''; ?>>大津町</option>
                                <option value="434043" <?php echo $user['address'] == '434043' ? 'selected' : ''; ?>>菊陽町</option>
                                <option value="434230" <?php echo $user['address'] == '434230' ? 'selected' : ''; ?>>南小国町</option>
                                <option value="434248" <?php echo $user['address'] == '434248' ? 'selected' : ''; ?>>小国町</option>
                                <option value="434256" <?php echo $user['address'] == '434256' ? 'selected' : ''; ?>>産山村</option>
                                <option value="434281" <?php echo $user['address'] == '434281' ? 'selected' : ''; ?>>高森町</option>
                                <option value="434329" <?php echo $user['address'] == '434329' ? 'selected' : ''; ?>>西原村</option>
                                <option value="434337" <?php echo $user['address'] == '434337' ? 'selected' : ''; ?>>南阿蘇村</option>
                                <option value="434418" <?php echo $user['address'] == '434418' ? 'selected' : ''; ?>>御船町</option>
                                <option value="434426" <?php echo $user['address'] == '434426' ? 'selected' : ''; ?>>嘉島町</option>
                                <option value="434434" <?php echo $user['address'] == '434434' ? 'selected' : ''; ?>>益城町</option>
                                <option value="434442" <?php echo $user['address'] == '434442' ? 'selected' : ''; ?>>甲佐町</option>
                                <option value="434477" <?php echo $user['address'] == '434477' ? 'selected' : ''; ?>>山都町</option>
                                <option value="434680" <?php echo $user['address'] == '434680' ? 'selected' : ''; ?>>氷川町</option>
                                <option value="434825" <?php echo $user['address'] == '434825' ? 'selected' : ''; ?>>芦北町</option>
                                <option value="434841" <?php echo $user['address'] == '434841' ? 'selected' : ''; ?>>津奈木町</option>
                                <option value="435015" <?php echo $user['address'] == '435015' ? 'selected' : ''; ?>>錦町</option>
                                <option value="435058" <?php echo $user['address'] == '435058' ? 'selected' : ''; ?>>多良木町</option>
                                <option value="435066" <?php echo $user['address'] == '435066' ? 'selected' : ''; ?>>湯前町</option>
                                <option value="435074" <?php echo $user['address'] == '435074' ? 'selected' : ''; ?>>水上村</option>
                                <option value="435104" <?php echo $user['address'] == '435104' ? 'selected' : ''; ?>>相良村</option>
                                <option value="435112" <?php echo $user['address'] == '435112' ? 'selected' : ''; ?>>五木村</option>
                                <option value="435121" <?php echo $user['address'] == '435121' ? 'selected' : ''; ?>>山江村</option>
                                <option value="435139" <?php echo $user['address'] == '435139' ? 'selected' : ''; ?>>球磨村</option>
                                <option value="435147" <?php echo $user['address'] == '435147' ? 'selected' : ''; ?>>あさぎり町</option>
                                <option value="435317" <?php echo $user['address'] == '435317' ? 'selected' : ''; ?>>苓北町</option>
                            </select>
                            <label for="address">居住地域</label>
                        </div>
                    </div>

                    <div class="input-group">
                        <div class="form-floating">
                            <input type="date" class="form-control" name="birth-date" id="birth-date" value="<?php echo htmlspecialchars($user['birth_date']); ?>" required>
                            <label for="birth-date">生年月日</label>
                        </div>
                    </div>

                    <div class="input-group">
                        <div class="form-floating">
                            <select class="form-select" name="license-status" id="license-status" required>
                                <option disabled selected value>選択してください</option>
                                <option value="licensed" <?php echo $user['license_status'] == 'licensed' ? 'selected' : ''; ?>>所持している</option>
                                <option value="unlicensed" <?php echo $user['license_status'] == 'unlicensed' ? 'selected' : ''; ?>>所持していない</option>
                                <option value="returned" <?php echo $user['license_status'] == 'returned' ? 'selected' : ''; ?>>返納済み</option>
                                <option value="suspension" <?php echo $user['license_status'] == 'suspension' ? 'selected' : ''; ?>>停止している</option>
                                <option value="expired" <?php echo $user['license_status'] == 'expired' ? 'selected' : ''; ?>>失効している</option>
                            </select>
                            <label for="license-status">運転免許の所持状況</label>
                        </div>
                    </div>

                    <div class="btn-group">
                        <button class="form-btn" type="button" id="btn-back">戻る</button>
                        <button class="form-btn" type="submit" id="btn-register" name="save">保存</button>
                    </div>
                </form>
            </div>
        </div>
    </main>
    <script src="js/base.js"></script>
    <script src="js/profile.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-geWF76RCwLtnZ8qwWowPQNguL3RmwHVBC9FhGdlKrxdiJJigb/j/68SIy3Te4Bkz"
        crossorigin="anonymous"></script>
</body>

</html>