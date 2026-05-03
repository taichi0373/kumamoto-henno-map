# OTP (OpenTripPlanner) 環境構築

熊本県の公共交通GTFSデータとOSMデータを使って、OTPのグラフ（`graph.obj`）を生成します。

## ディレクトリ構成

```
config/otp/
├── setup.sh              # データ取得〜グラフビルドを一括実行（エントリーポイント）
├── build-graph.sh        # OTPグラフビルドのみ実行
└── data/
    ├── download.sh           # GTFSデータ・OSMデータの取得・加工
    ├── formatted.py          # GTFSデータ加工スクリプト
    ├── upgrade_translations.py  # GTFS翻訳データ変換スクリプト
    ├── requirements.txt      # Python依存パッケージ
    ├── build-config.json     # OTPビルド設定
    ├── router-config.json    # OTPルーター設定
    │
    │   # 以下は生成物（gitignore対象）
    ├── *.gtfs.zip            # ダウンロードしたGTFSデータ
    ├── kumamoto.pbf          # OSMデータ（熊本県範囲に切り出し済み）
    └── graph.obj             # OTPグラフ（ビルド成果物）
```

## 使い方

### 一括実行（推奨）

データ取得からグラフビルドまでをまとめて実行します。

```bash
# config/otp で実行
bash setup.sh
```

### 個別実行

#### GTFSデータ・OSMデータの取得・加工

```bash
# config/otp/data で実行
bash download.sh
```

#### OTPグラフのビルド

`download.sh` 実行後に行います。Dockerが必要です。

```bash
# config/otp で実行
bash build-graph.sh
```

## 取得するデータ

| 種別 | データ名 | 取得元 |
|------|----------|--------|
| GTFS | 産交バス（sankobus） | bus-vision.jp |
| GTFS | 電鉄バス（dentetsubus） | bus-vision.jp |
| GTFS | 熊本バス（kumabus） | bus-vision.jp |
| GTFS | 都市バス（toshibus） | bus-vision.jp |
| GTFS | 熊本市電（kumamotoshiden） | gtfs-data.jp |
| GTFS | 熊本電鉄（kumamotodentetsu） | gtfs-data.jp |
| OSM | 九州全域PBF → 熊本県範囲に切り出し | Geofabrik |

## 前提条件

- `bash download.sh`: `apt-get` が使える Linux 環境（Docker内でも可）、Python 3.10+
- `bash build-graph.sh`: Docker が起動していること
