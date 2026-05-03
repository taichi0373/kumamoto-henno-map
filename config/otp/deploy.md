# OTPサーバー デプロイ手順

OTP（OpenTripPlanner）をVPS上にDockerでデプロイする手順です。  
ローカルでビルド済みの `graph.obj` をVPSへ転送し、`--load --serve` で起動します。

---

## VPS選定目安

| サービス | プラン | 月額目安 | メモリ | 日本リージョン | 備考 |
|---|---|---|---|---|---|
| さくらのVPS | 4GB | 約1,738円 | 4GB | あり | 国内サポートあり |
| ConoHa VPS | 4GB | 約2,178円 | 4GB | あり | 国内サポートあり |
| Hetzner Cloud | CX22 | 約700円 | 4GB | なし（シンガポール） | 有料では最安クラス |
| Oracle Cloud | ARM Free Tier | **無料** | 最大24GB | あり（東京） | 登録時クレジットカード必須 |

> OTPはバックエンドからのみ呼び出す内部サービスのため、リージョンが多少遠くても実用上の問題は小さい。

---

## 前提条件

- ローカルで `graph.obj`（106MB）がビルド済みであること（`README.md` 参照）
- VPSに Docker がインストール済みであること
- プロジェクトルートの `.env` に `OTP_HEAP_SIZE` が設定されていること（後述）

---

## メモリ設定

ビルド・実行ともにプロジェクトルートの `.env` で一元管理します。

```bash
# プロジェクトルートで実行
cp .env.example .env
```

`.env` の内容:

```dotenv
# OTP JVMヒープサイズ（ビルド・実行共通）
OTP_HEAP_SIZE=4500m
```

VPSのメモリに合わせて値を調整してください:

| VPSメモリ | 推奨値 |
|---|---|
| 2GB | `1200m` |
| 4GB | `3g` |
| 6GB以上（ローカル等） | `4500m`（デフォルト） |

> `docker compose up` はルートの `.env` を自動で読み込みます。  
> `build-graph.sh` も同じ `.env` を読み込むため、値の二重管理は不要です。

---

## デプロイ手順

### Step 1: VPSの初期セットアップ

Ubuntu 22.04 を推奨します。

```bash
sudo apt update && sudo apt upgrade -y

# Docker インストール
sudo apt install -y docker.io
sudo systemctl enable --now docker
sudo usermod -aG docker $USER
newgrp docker
```

### Step 2: スワップ領域の設定

graph.obj 読み込み時にメモリが一時的に増加するため、スワップを設定しておきます。

```bash
sudo fallocate -l 2G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile

# 再起動後も有効にする
echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab
```

### Step 3: OTPデータをVPSへ転送

ローカルから `graph.obj` と設定ファイルのみ転送します（pbf・GTFSファイルは不要）。

```bash
# ローカルで実行
ssh user@<VPS_IP> "mkdir -p /opt/otp/data"

scp config/otp/data/graph.obj          user@<VPS_IP>:/opt/otp/data/
scp config/otp/data/router-config.json user@<VPS_IP>:/opt/otp/data/
```

### Step 4: docker-compose.yml を配置

```bash
# VPS上で実行
cat > /opt/otp/docker-compose.yml << 'EOF'
services:
  otp:
    image: docker.io/opentripplanner/opentripplanner:2.5.0
    container_name: otp
    restart: unless-stopped
    volumes:
      - ./data:/var/opentripplanner
    ports:
      - "8080:8080"
    environment:
      - JAVA_TOOL_OPTIONS=-Xmx${OTP_HEAP_SIZE:-4500m}
    command: ["--load", "--serve"]
EOF
```

`.env` も配置します:

```bash
cat > /opt/otp/.env << 'EOF'
OTP_HEAP_SIZE=3g
EOF
```

### Step 5: OTP起動

```bash
cd /opt/otp
docker compose up -d

# ログ確認（graph.obj 読み込みに 1〜3 分かかる）
docker compose logs -f
```

`Grizzly server running.` が出たら起動完了です。

動作確認:

```bash
curl -s "http://localhost:8080/otp/routers/default" | head -20
```

### Step 6: ファイアウォール設定

```bash
sudo ufw allow 22/tcp     # SSH
# OTPはバックエンド内部からのみアクセスするため、8080は外部に開けない
sudo ufw enable
```

> バックエンドとOTPが別サーバーにある場合のみ、バックエンドのIPからの8080アクセスを許可します:
> ```bash
> sudo ufw allow from <バックエンドIP> to any port 8080
> ```

---

## graph.obj の更新手順

GTFSデータが更新された場合（時刻改正等）、以下の手順で再デプロイします。

```bash
# 1. ローカルで再ビルド
bash config/otp/setup.sh

# 2. VPSへ転送
scp config/otp/data/graph.obj user@<VPS_IP>:/opt/otp/data/

# 3. OTPを再起動
ssh user@<VPS_IP> "cd /opt/otp && docker compose restart otp"
```

---

## トラブルシューティング

### OOMで起動しない

```bash
# ヒープサイズを下げて再起動
echo 'OTP_HEAP_SIZE=1500m' > /opt/otp/.env
cd /opt/otp && docker compose up -d
```

### 起動が遅い / タイムアウト

graph.obj の読み込みに最大 3 分程度かかります。ログを確認して `Grizzly server running.` を待ちます。

```bash
docker compose logs -f --tail=50
```

### GTFSリアルタイム更新が機能しない

`router-config.json` の更新先 URL（bus-vision.jp）へVPSからの外部通信が必要です。ファイアウォールで外向きの通信が制限されていないか確認してください。
