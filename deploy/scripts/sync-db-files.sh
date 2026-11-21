#!/usr/bin/env bash
# config/database の初期化 SQL をチャート内へコピーする（Helm はチャート外を参照できないため）
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/../.." && pwd)"
SRC="$ROOT_DIR/config/database"
DEST="$ROOT_DIR/deploy/helm/kumamoto-henno-map/files/database"

rm -rf "$DEST"
mkdir -p "$DEST"
cp "$SRC/setup.sql" "$DEST/"
cp -r "$SRC/DDL" "$SRC/DML" "$DEST/"

echo "コピー完了: $DEST"
