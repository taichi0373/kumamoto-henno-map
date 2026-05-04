## 環境構成

### フロントエンド
- Vue.js 開発サーバー: http://localhost:3000
- Storybook: http://localhost:6006

### バックエンド
- Spring Boot: http://localhost:8081

### Docker Services
- PostgreSQL: http://localhost:5432
- pgAdmin: http://localhost:65432
- OTP: http://localhost:8080

#### WSL IP 確認
```bash
ip addr show eth0
```

## Docker 初期化

### コンテナの停止と削除
```bash
docker compose down -v
```

## Docker 起動

### Docker Compose 起動
```bash
docker compose up -d
```

### ログ確認
```bash
docker compose logs -f
```

## DB 起動確認

```bash
docker compose exec db bash
psql -U user -d benefit_map
```

### テーブル確認
```bash
\dt
```

### ビュー確認
```bash
\dv
```

## テスト用DB（benefit_map_test）

E2Eテスト（Playwright）用のDBとして `benefit_map_test` が自動作成されます。

### 仕組み

`config/database/setup-test.sh` が `docker-entrypoint-initdb.d` に配置されており、`setup.sql` と同じDDL・マスターデータが適用されます。
`docker compose up -d` の **初回起動時** に自動実行されます。

### 再作成が必要な場合

スキーマ変更などでテスト用DBを作り直す場合は、ボリュームごと削除して再起動してください。

```bash
docker compose down -v
docker compose up -d
```

### テスト用DB接続確認

```bash
docker compose exec db psql -U user -d benefit_map_test
```

### E2Eテスト実行前の設定

`apps/front/.env.test` に以下を設定してください（`_test` で終わることが必須）。

```
DB_HOST=<WSL IPアドレス>
DB_PORT=5432
DB_NAME=benefit_map_test
DB_USERNAME=user
DB_PASSWORD=pass
```

WSL IPアドレスの確認:
```bash
ip addr show eth0
```

## pgAdmin 接続設定

| 項目 | 値 |
|------|-----|
| 名前 | benefit_map_db |
| ホスト名/アドレス | db |
| ポート番号 | 5432 |
| 管理用データベース | benefit_map |
| ユーザ名 | user |
| パスワード | pass |