#!/bin/bash
# GTFSファイルの有効期限をチェックするスクリプト
#
# 使用方法:
#   bash check-gtfs-expiry.sh [閾値日数]
#
# 引数:
#   閾値日数: 期限切れ判定の閾値（デフォルト: 7日）
#             今日から閾値日数以内に期限切れになるファイルがあれば更新が必要と判定する
#
# GitHub Actions 連携:
#   GITHUB_OUTPUT が設定されている場合、以下の値を書き込む:
#   - need_update=1  ... 更新が必要
#   - need_update=0  ... 更新不要
#   - expired_files  ... 期限切れ（または期限切れ間近）のファイル名（スペース区切り）
#
# 戻り値:
#   0: 更新不要
#   1: 更新が必要（閾値日数以内に期限切れのファイルあり）

set -euo pipefail

THRESHOLD_DAYS="${1:-7}"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DATA_DIR="${SCRIPT_DIR}/data"
TODAY=$(date +%Y%m%d)
THRESHOLD_DATE=$(date -d "+${THRESHOLD_DAYS} days" +%Y%m%d)

need_update=0
expired_files=()

echo "=========================================="
echo " GTFSファイル有効期限チェック"
echo " 基準日: ${TODAY}"
echo " 閾値日: ${THRESHOLD_DATE}（${THRESHOLD_DAYS}日後）"
echo "=========================================="

# data ディレクトリ内の全 *.gtfs.zip を検査
shopt -s nullglob
zip_files=("${DATA_DIR}"/*.gtfs.zip)
shopt -u nullglob

if [ "${#zip_files[@]}" -eq 0 ]; then
    echo "エラー: ${DATA_DIR} にGTFSファイルが見つかりません"
    exit 1
fi

for zip_file in "${zip_files[@]}"; do
    filename=$(basename "${zip_file}")
    echo ""
    echo "チェック中: ${filename}"

    # feed_info.txt を読み込む（フラット構造を前提）
    feed_info=$(unzip -p "${zip_file}" feed_info.txt 2>/dev/null || echo "")

    if [ -z "${feed_info}" ]; then
        echo "  警告: feed_info.txt が見つかりません → スキップ"
        continue
    fi

    # ヘッダー行から feed_end_date 列のインデックスを特定
    header=$(echo "${feed_info}" | head -n 1 | tr -d '\r')
    IFS=',' read -ra columns <<< "${header}"
    end_date_idx=-1
    for i in "${!columns[@]}"; do
        if [ "${columns[$i]}" = "feed_end_date" ]; then
            end_date_idx=$i
            break
        fi
    done

    if [ "${end_date_idx}" -eq -1 ]; then
        echo "  警告: feed_end_date 列が見つかりません → スキップ"
        continue
    fi

    # データ行から feed_end_date 値を取得
    data_line=$(echo "${feed_info}" | sed -n '2p' | tr -d '\r')
    IFS=',' read -ra values <<< "${data_line}"
    feed_end_date="${values[$end_date_idx]:-}"

    # YYYYMMDD形式に正規化（ハイフンを除去）
    feed_end_date="${feed_end_date//-/}"

    if [ -z "${feed_end_date}" ]; then
        echo "  警告: feed_end_date の値が空です → スキップ"
        continue
    fi

    echo "  有効期限: ${feed_end_date}"

    if [ "${feed_end_date}" -le "${THRESHOLD_DATE}" ]; then
        echo "  → 更新が必要です（${THRESHOLD_DAYS}日以内に期限切れ）"
        need_update=1
        expired_files+=("${filename}")
    else
        echo "  → 問題なし"
    fi
done

echo ""
echo "=========================================="

if [ "${need_update}" -eq 1 ]; then
    echo "更新が必要なファイル:"
    printf '  - %s\n' "${expired_files[@]}"
    # GITHUB_OUTPUT が設定されている場合のみ書き込む
    if [ -n "${GITHUB_OUTPUT:-}" ]; then
        echo "need_update=1" >> "${GITHUB_OUTPUT}"
        echo "expired_files=${expired_files[*]}" >> "${GITHUB_OUTPUT}"
    fi
    echo "=========================================="
    exit 1
else
    echo "全てのGTFSファイルは有効期限内です"
    if [ -n "${GITHUB_OUTPUT:-}" ]; then
        echo "need_update=0" >> "${GITHUB_OUTPUT}"
        echo "expired_files=" >> "${GITHUB_OUTPUT}"
    fi
    echo "=========================================="
    exit 0
fi
