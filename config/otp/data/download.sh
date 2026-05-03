set -e

echo "---------- 実行権限の判定 ----------"

if [ "$(id -u)" -eq 0 ]; then
    SUDO=""
    echo "root 権限で実行されています（sudo不要）"
else
    SUDO="sudo"
    echo "一般ユーザーで実行されています（sudoを使用）"
fi

echo "---------- 使用コマンドのインストール ----------"

$SUDO apt-get update

for cmd in curl wget zip unzip osmconvert; do
    if ! command -v "$cmd" >/dev/null 2>&1; then
        case "$cmd" in
            osmconvert)
                pkg="osmctools"
                ;;
            *)
                pkg="$cmd"
                ;;
        esac
        echo "$cmd がないため $pkg をインストールします"
        $SUDO apt-get install -y "$pkg"
    else
        echo "$cmd は既に存在します"
    fi
done

echo "---------- 使用コマンドのインストール 完了 ----------"

echo "---------- Python環境構築 ----------"

$SUDO apt-get update

# Python3 + venv がない場合、インストール
$SUDO apt-get install -y python3 python3-venv python3-pip

VENV_DIR=".venv"

if [ ! -d "$VENV_DIR" ]; then
  echo "Python仮想環境を作成します: $VENV_DIR"
  python3 -m venv "$VENV_DIR"
fi

echo "仮想環境を有効化します"
source "$VENV_DIR/bin/activate"

pip install --upgrade pip

if [ -f requirements.txt ]; then
  echo "requirements.txt からライブラリをインストール"
  pip install -r requirements.txt
else
  echo "requirements.txt が見つかりません"
fi

echo "---------- Python環境構築 完了 ----------"


BUS_NAMES=(
  sankobus
  dentetsubus
  kumabus
  toshibus
)

declare -A GTFS_URLS=(
  [sankobus]="https://km.bus-vision.jp/gtfs/sankobus/gtfsFeed"
  [dentetsubus]="https://km.bus-vision.jp/gtfs/dentetsu/gtfsFeed"
  [kumabus]="https://km.bus-vision.jp/gtfs/kumabus/gtfsFeed"
  [toshibus]="https://km.bus-vision.jp/gtfs/toshibus/gtfsFeed"
)

echo "---------- ファイルの初期化 ----------"
for bus in "${BUS_NAMES[@]}"; do
  rm -rf \
    "${bus}.gtfs" \
    "${bus}.gtfs_tmp"
done
find . -type f -name "*.zip" -delete
find . -type f -name "*.pbf" -delete
find . -type f -name "*.Identifier" -delete

echo "---------- GTFSデータ取得 ---------"
for bus in "${BUS_NAMES[@]}"; do
  curl -o "${bus}.zip" "${GTFS_URLS[$bus]}"
done
# curl -o yatsushiro-taxi.gtfs.zip https://www.city.yatsushiro.lg.jp/kiji00315975/3_15975_115402_up_6k3sjd7i.zip
# curl -o kumamoto-jr-kyushu.gtfs.zip https://gtfs-gis.jp/gtfs4research/gtfs-kumamoto-jr-kyushu-20250401.zip
curl -L -o kumamotoshiden.gtfs.zip "https://api.gtfs-data.jp/v2/organizations/kumamoto-shiden/feeds/kumamotoshiden/files/feed.zip?rid=current"
curl -L -o kumamotodentetsu.gtfs.zip "https://api.gtfs-data.jp/v2/organizations/kumamotodentetsu/feeds/kumamotodentetsu/files/feed.zip?rid=current"

echo "---------- GTFSデータ解凍 ----------"
fix_gtfs_structure() {
    local zip_file=$1
    local extract_dir=$2
    local final_dir="${extract_dir}.gtfs"
    
    echo "処理中のファイル名： $zip_file..."
    
    mkdir -p temp_extract
    unzip -o "$zip_file" -d temp_extract
    
    mkdir -p "$final_dir"
    
    find temp_extract -name "*.txt" -type f | while read file; do
        filename=$(basename "$file")
        cp "$file" "$final_dir/$filename"
    done
    
    rm -rf temp_extract
}

# 各GTFSファイルを解凍
for bus in "${BUS_NAMES[@]}"; do
  fix_gtfs_structure "${bus}.zip" "$bus"
done

echo "---------- GTFSデータ加工処理 ----------"
for bus in "${BUS_NAMES[@]}"; do
  python3 formatted.py "${bus}.gtfs"
  python3 upgrade_translations.py "${bus}.gtfs" "${bus}.gtfs_tmp"
  rm -rf "${bus}.gtfs"
  mv "${bus}.gtfs_tmp" "${bus}.gtfs"
done

echo "---------- feed_id の設定 ----------"
for bus in "${BUS_NAMES[@]}"; do
  python3 - "$bus" "${bus}.gtfs/feed_info.txt" << 'PYEOF'
import csv, os, sys

bus = sys.argv[1]
path = sys.argv[2]

if not os.path.exists(path):
    # feed_info.txt が存在しない場合は feed_id のみを持つ最小構成で新規作成する
    with open(path, 'w', encoding='utf-8', newline='') as f:
        writer = csv.DictWriter(f, fieldnames=['feed_id'])
        writer.writeheader()
        writer.writerow({'feed_id': bus})
    print(f'feed_info.txt が存在しなかったため新規作成し feed_id={bus} を設定しました')
    sys.exit(0)

with open(path, 'r', encoding='utf-8-sig') as f:
    reader = csv.DictReader(f)
    rows = list(reader)
    fieldnames = list(reader.fieldnames or [])

if 'feed_id' not in fieldnames:
    fieldnames = ['feed_id'] + fieldnames

mismatched_feed_ids = sorted({
    row.get('feed_id', '')
    for row in rows
    if row.get('feed_id', '') not in ('', bus)
})

for row in rows:
    row['feed_id'] = bus

with open(path, 'w', encoding='utf-8', newline='') as f:
    writer = csv.DictWriter(f, fieldnames=fieldnames)
    writer.writeheader()
    writer.writerows(rows)

if mismatched_feed_ids:
    print(
        f'警告: {path} に bus 名と一致しない既存の feed_id が含まれていたため '
        f'{bus} に統一しました: {", ".join(mismatched_feed_ids)}',
        file=sys.stderr
    )

print(f'feed_id={bus} を {path} に設定しました')
PYEOF
done

echo "---------- GTFSデータzipファイル化 ----------"
for bus in "${BUS_NAMES[@]}"; do
  (
    cd "${bus}.gtfs"
    zip -r "../${bus}.gtfs.zip" *
  )
done


echo "---------- OSMデータ取得・加工処理 ----------"
if [ ! -f kyushu-latest.osm.pbf ]; then
    echo "ダウンロード中..."
    wget -O kyushu-latest.osm.pbf https://download.geofabrik.de/asia/japan/kyushu-latest.osm.pbf
    if [ $? -ne 0 ]; then
        echo "kyushu-latest.osm.pbfのダウンロードに失敗しました。インターネット接続を確認してください。"
        exit 1
    fi
    echo "kyushu-latest.osm.pbfのダウンロードが完了しました。"
else
    echo "kyushu-latest.osm.pbfが既に存在します。ダウンロードをスキップします。"
fi
osmconvert kyushu-latest.osm.pbf -b=129.923336,32.096306,131.3318,33.1896 --complete-ways -o=kumamoto.pbf

echo "---------- 不要ファイルの削除 ----------"
for bus in "${BUS_NAMES[@]}"; do
  rm -rf \
    "${bus}.zip" \
    "${bus}.gtfs" \
    "${bus}.gtfs_tmp"
done

rm -f kyushu-latest.osm.pbf

find . -type f -name "*.Identifier" -delete

echo "---------- 全ての処理が完了しました ----------"