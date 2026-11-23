#!/usr/bin/env bash
# deploy/.env を読み込み Helm チャートをデプロイする
# 使い方: bash deploy/scripts/deploy.sh [apply|template|lint]
# 秘密情報は ps で見えないよう標準入力で Helm に渡す
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/../.." && pwd)"
CHART="$ROOT_DIR/deploy/helm/kumamoto-henno-map"
ENV_FILE="${ENV_FILE:-$ROOT_DIR/deploy/.env}"
MODE="${1:-apply}"

if [ ! -f "$ENV_FILE" ]; then
  echo "設定ファイルがありません: $ENV_FILE（deploy/.env.example を複製して作成してください）" >&2
  exit 1
fi

# ----- .env の読み込み -----
while IFS= read -r line || [ -n "$line" ]; do
  line="${line%$'\r'}"
  [[ "$line" =~ ^[[:space:]]*(#|$) ]] && continue
  key="${line%%=*}"
  value="${line#*=}"
  key="${key//[[:space:]]/}"
  [[ "$key" =~ ^[A-Z_][A-Z0-9_]*$ ]] || { echo "不正な行です: $key" >&2; exit 1; }
  if [[ "$value" =~ ^\"(.*)\"$ ]] || [[ "$value" =~ ^\'(.*)\'$ ]]; then
    value="${BASH_REMATCH[1]}"
  fi
  # 既存の環境変数を優先する（CI で IMAGE_TAG を上書きする用途）
  [ -n "${!key+x}" ] || export "$key=$value"
done < "$ENV_FILE"

: "${VALUES_FILE:?VALUES_FILE を設定してください（例: values-home.yaml）}"
: "${IMAGE_TAG:?IMAGE_TAG を設定してください（コミット SHA。ローカル検証は local）}"
RELEASE_NAME="${RELEASE_NAME:-khm}"
NAMESPACE="${NAMESPACE:-khm}"

# ----- values の生成 -----
# YAML のダブルクォート文字列として出力する
q() {
  local s="$1"
  [[ "$s" == *$'\n'* ]] && { echo "改行を含む値は設定できません" >&2; exit 1; }
  s="${s//\\/\\\\}"
  s="${s//\"/\\\"}"
  printf '"%s"' "$s"
}

# 未設定なら values ファイルの値を使う
opt() {
  local indent="$1" key="$2" value="$3"
  [ -n "$value" ] && printf '%s%s: %s\n' "$indent" "$key" "$value"
  return 0
}

generate_values() {
  printf 'global:\n  imageTag: %s\n' "$(q "$IMAGE_TAG")"

  printf 'db:\n'
  opt '  ' enabled "${DB_ENABLED:-}"
  printf '  backup:\n'
  opt '    ' enabled "${DB_BACKUP_ENABLED:-}"
  printf '    r2:\n'
  opt '      ' endpoint "${R2_ENDPOINT:+$(q "$R2_ENDPOINT")}"

  printf 'externalDb:\n'
  opt '  ' host "${EXTERNAL_DB_HOST:+$(q "$EXTERNAL_DB_HOST")}"
  opt '  ' port "${EXTERNAL_DB_PORT:-}"
  opt '  ' name "${EXTERNAL_DB_NAME:+$(q "$EXTERNAL_DB_NAME")}"
  opt '  ' username "${EXTERNAL_DB_USERNAME:+$(q "$EXTERNAL_DB_USERNAME")}"

  printf 'secrets:\n'
  printf '  dbPassword: %s\n' "$(q "${DB_PASSWORD:-}")"
  printf '  jwtSecret: %s\n' "$(q "${JWT_SECRET:-}")"
  printf '  resendApiKey: %s\n' "$(q "${RESEND_API_KEY:-}")"
  printf '  awsAccessKeyId: %s\n' "$(q "${AWS_ACCESS_KEY_ID:-}")"
  printf '  awsSecretAccessKey: %s\n' "$(q "${AWS_SECRET_ACCESS_KEY:-}")"
  printf '  slackWebhookUrl: %s\n' "$(q "${SLACK_WEBHOOK_URL:-}")"
  printf '  adminUsername: %s\n' "$(q "${ADMIN_USERNAME:-}")"
  printf '  adminPassword: %s\n' "$(q "${ADMIN_PASSWORD:-}")"
  printf '  adminEmail: %s\n' "$(q "${ADMIN_EMAIL:-}")"
  printf '  cloudflaredToken: %s\n' "$(q "${CLOUDFLARED_TOKEN:-}")"
  printf '  r2AccessKeyId: %s\n' "$(q "${R2_ACCESS_KEY_ID:-}")"
  printf '  r2SecretAccessKey: %s\n' "$(q "${R2_SECRET_ACCESS_KEY:-}")"
}

bash "$ROOT_DIR/deploy/scripts/sync-db-files.sh" > /dev/null

ARGS=(-f "$CHART/$VALUES_FILE" -f -)
case "$MODE" in
  apply)
    generate_values | helm upgrade --install "$RELEASE_NAME" "$CHART" \
      -n "$NAMESPACE" --create-namespace "${ARGS[@]}"
    ;;
  template)
    generate_values | helm template "$RELEASE_NAME" "$CHART" -n "$NAMESPACE" "${ARGS[@]}"
    ;;
  lint)
    generate_values | helm lint "$CHART" "${ARGS[@]}"
    ;;
  *)
    echo "不明なモードです: $MODE（apply / template / lint）" >&2
    exit 1
    ;;
esac
