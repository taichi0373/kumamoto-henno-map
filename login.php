<?php
ob_start();
session_start();
if (isset($_SESSION['user_id']) != "") {
    header("Location: index.php");
}

include_once './php/db_connection.php';
$mysqli = connectToDatabase($servername, $username, $password, $dbname);

// ログインボタンがクリックされたとき
if (isset($_POST['login'])) {
    $username = $mysqli->real_escape_string($_POST['username']);
    $password = $mysqli->real_escape_string($_POST['password']);
    $query = "SELECT * FROM users WHERE username = ?";
    $stmt = $mysqli->prepare($query);
    $stmt->bind_param("s", $username);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows == 0) {
?>
        <script>
            alert('ユーザ名またはパスワードが違います');
            location.href = 'login.php';
        </script>
    <?php
        $stmt->close();
        $mysqli->close();
        exit();
    }

    // パスワードとユーザーIDの取り出し
    $row = $result->fetch_assoc();
    $id = $row['id'];
    $db_hashed_pwd = $row['password'];
    $username = $row['username'];

    // データベースの切断
    $stmt->close();

    // ハッシュ化されたパスワードが一致するか
    if (password_verify($password, $db_hashed_pwd)) {
        $_SESSION['user_id'] = $id;
        $_SESSION['username'] = $username;
        header("Location: index.php");
        exit;
    } else {
    ?>
        <script>
            alert('IDまたはパスワードが違います');
            location.href = 'login.php';
        </script>
<?php
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
                <form class="form" id="formLogin" method="post">

                    <h3 class="form-title">ログイン</h3>

                    <div class="input-group">
                        <div class="form-floating">
                            <input type="text" class="form-control" name="username" id="username"
                                placeholder="ユーザー名" required>
                            <label for="username">ユーザー名</label>
                        </div>
                    </div>
                    <div class="input-group">
                        <div class="form-floating">
                            <input type="password" class="form-control" name="password" id="password"
                                placeholder="パスワード" required>
                            <label for="password">パスワード</label>
                        </div>
                    </div>

                    <div class="btn-group">
                        <button class="form-btn" type="button" id="btn-back">戻る</button>
                        <button class="form-btn" type="submit" id="btn-register" name="login">ログイン</button>
                    </div>
                    <script>
                        const btn_back = document.getElementById("btn-back");
                        btn_back.addEventListener("click", function() {
                            window.location.href = "index.php";
                        });
                    </script>
                    <div class="btn-group">
                        <a href="register.php">新規登録はこちら</a>
                    </div>
                </form>
            </div>

        </div>


    </main>
    <script src="js/base.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-geWF76RCwLtnZ8qwWowPQNguL3RmwHVBC9FhGdlKrxdiJJigb/j/68SIy3Te4Bkz"
        crossorigin="anonymous"></script>
</body>

</html>