## バックエンド

Java 21 + Spring Boot 4 + Doma 2 による REST API サーバーです。

### ローカル開発時の設定

`./gradlew bootRun` と `./gradlew generateOpenApiDocs` では dev プロファイル（`src/main/resources/application-dev.properties`）が自動で有効になります。環境変数を設定しなくても、以下のデフォルト値で起動します。

| 項目 | デフォルト値 |
|------|-------------|
| DB 接続先 | `localhost:5432/kumamoto_henno_map`（`user` / `pass`、`docker compose up -d` で起動） |
| 管理者ユーザー | `admin` / `admin`（起動時に作成） |
| メール送信 | `RESEND_SANDBOX=true` のため実際には送信せず、本文を DEBUG ログに出力 |
| OpenTripPlanner | `http://localhost:8080` |

本番環境で必要な環境変数は [デプロイ環境変数一覧](../../docs/01_overview/deploy_env_vars.md) を参照してください。

### Gradle コマンド

```bash
# コンパイル
./gradlew clean compileJava

# ビルド（テスト含む）
./gradlew clean build

# 開発サーバー起動（http://localhost:8081）
./gradlew bootRun

# テストのみ実行
./gradlew test

# 実行可能JARの作成
./gradlew bootJar

# 依存関係の確認
./gradlew dependencies
```

### OpenAPI YAML の生成

springdoc-openapi-gradle-plugin を使い、Spring Boot を一時起動して `/v3/api-docs.yaml` を取得します。

**前提条件:** PostgreSQL が起動していること（`docker compose up -d`）

```bash
./gradlew generateOpenApiDocs
```

出力先: `build/openapi/openapi.yaml`

> ReDoc で HTML に変換する場合:
> ```bash
> npx @redocly/cli build-docs build/openapi/openapi.yaml --output openapi.html
> ```
