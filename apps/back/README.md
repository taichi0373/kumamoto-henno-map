## バックエンド

Java 21 + Spring Boot 3 + Doma 2 による REST API サーバーです。

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
