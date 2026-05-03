#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "=========================================="
echo " OTP グラフビルド（データ取得 → ビルド）"
echo "=========================================="

echo ""
echo "---------- [1/2] データダウンロード・加工 ----------"
cd "$SCRIPT_DIR/data"
bash download.sh

echo ""
echo "---------- [2/2] OTP グラフビルド ----------"
cd "$SCRIPT_DIR"
bash build-graph.sh

echo ""
echo "=========================================="
echo " 全ての処理が完了しました"
echo " 生成物: config/otp/data/graph.obj"
echo "=========================================="
