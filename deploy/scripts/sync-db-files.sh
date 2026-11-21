#!/usr/bin/env bash
# config/database の初期化 SQL を Helm チャート内（files/database）へコピーする
# Helm はチャートディレクトリ外のファイルを .Files で参照できないため、deploy.sh がデプロイ前に自動で実行する
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/../.." && pwd)"
SRC="$ROOT_DIR/config/database"
DEST="$ROOT_DIR/deploy/helm/kumamoto-henno-map/files/database"

rm -rf "$DEST"
mkdir -p "$DEST"
cp "$SRC/setup.sql" "$DEST/"
cp -r "$SRC/DDL" "$SRC/DML" "$DEST/"

echo "コピー完了: $DEST"
