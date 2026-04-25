## Gradle

### 基本コマンド

```bash
# ビルドとコンパイル
.\gradlew clean compileJava

# ビルド（テストも含む）
.\gradlew clean build

# アプリケーションの実行
.\gradlew bootRun

# テストのみ実行
.\gradlew test

# 実行可能JARファイルの作成
.\gradlew bootJar

# 依存関係の確認
.\gradlew dependencies

# プロジェクト情報の確認
.\gradlew projects
```

### OpenAPI YAML の生成

springdoc-openapi-gradle-plugin を使い、Spring Boot を一時起動して `/v3/api-docs.yaml` を取得する。

**前提条件:** PostgreSQL が起動していること（`docker compose up -d` 等）

```bash
.\gradlew generateOpenApiDocs
```

出力先: `build/openapi/openapi.yaml`

> **補足**
> - dev プロファイルで起動するため、DB 接続情報は `application.properties` のデフォルト値（`localhost:5432/benefit_map`、ユーザー `user`、パスワード `pass`）が使われる
> - 生成した YAML を Redoc で HTML に変換する場合:
>   ```bash
>   npx @redocly/cli build-docs build/openapi/openapi.yaml --output openapi.html
>   ```
