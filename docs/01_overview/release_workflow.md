# リリースワークフロー

## ブランチ戦略

```
main     ──● Release v1.0.0 ──────────────────────── Release v1.1.0 ──
              ↑ merge commit                               ↑ merge commit
develop  ──●──●──●──●──●──●──●──●──●──●──●──●──●──●──●──●──
             #1  #2  #3 ...                                #N
```

| ブランチ | 環境 | 役割 |
|---------|------|------|
| `main` | 本番 | リリース済みコードのみ。`Release vX.Y.Z` PR のマージコミットとタグが並ぶ |
| `develop` | 検証 | 開発の主軸。feature/bugfix ブランチのマージ先。常に `main` より前にある |
| `feature/*` | ローカル | 機能開発用。`develop` から切って `develop` へ PR を出す |
| `bugfix/*` | ローカル | バグ修正用。`develop` から切って `develop` へ PR を出す |

---

## バージョン番号ルール

[セマンティックバージョニング](https://semver.org/lang/ja/) に従う。

```
vX.Y.Z
 │ │ └─ パッチ：バグ修正・軽微な改善
 │ └─── マイナー：後方互換の機能追加
 └───── メジャー：破壊的変更・大規模リニューアル
```

| 種別 | バージョン例 | リリース区分 |
|-----|------------|------------|
| メジャー | `v1.0.0` → `v2.0.0` | **正式リリース**（X が変わる） |
| マイナー | `v1.0.0` → `v1.1.0` | **暫定リリース**（Y が変わる） |
| パッチ | `v1.0.0` → `v1.0.1` | **暫定リリース**（Z が変わる） |

---

## 日常の開発フロー

```bash
# 1. develop から作業ブランチを作成
git checkout develop
git pull origin develop
git checkout -b feature/xxx   # または bugfix/xxx

# 2. 開発・コミット
git commit -m "○○機能の実装"

# 3. develop へ PR を出す（GitHub上）
#    → レビュー・承認後にマージ
```

---

## リリース手順

### 1. develop → main へ PR を作成

GitHub 上で `develop` → `main` の PR を作成する。

- タイトルは必ず `Release vX.Y.Z` の形式にする（例: `Release v1.1.0`）
- リリースの実行はこのタイトルで判定する。形式が異なるとリリースされない、または失敗する

### 2. Create a merge commit でマージ

PR のマージ方法は **"Create a merge commit"** を選択する。

- `main` と `develop` の履歴が分岐しないようにするため、Squash and merge は使わない

### 3. GitHub Actions が自動実行（手動作業不要）

`main` 向け PR のマージをトリガーに `.github/workflows/release.yml` が自動で以下を実行する。

```
main 向け PR のマージ（タイトル: Release vX.Y.Z）
  │
  └─ [release job]
       ├─ バージョン番号を抽出（"Release v1.1.0" → "v1.1.0"）
       ├─ git tag vX.Y.Z を作成・プッシュ
       └─ GitHub Release を作成（PR タイトルから変更履歴を自動生成）
```

### 4. main の変更を develop へ取り込む

`main` に `develop` にないコミットがある場合（Dependabot のセキュリティ更新など）は、次のリリース PR の前に `main` → `develop` の PR を作成して取り込む。

### 手順まとめ

```
develop ──●──●──●──●──●
                        ↓ PR "Release v1.1.0" (Create a merge commit) ← 手動
main    ──────────── Release v1.1.0
                        │
                        ├─ tag v1.1.0        ← 自動（GitHub Actions）
                        └─ GitHub Release    ← 自動（GitHub Actions）

develop ──●──●──●──●──●──次の開発へ（main にだけあるコミットは PR で取り込む ← 手動）
```

---

## GitHub Tags と GitHub Releases

| 機能 | 実体 | 作成タイミング |
|------|------|--------------|
| **Tag** | Git の参照ポインタ | 正式・暫定リリースともに自動作成 |
| **Release** | Tag に説明・変更履歴を付加した GitHub 機能 | 正式・暫定リリースともに自動作成 |

GitHub Release の変更履歴は、マージされた PR のタイトルから自動生成される。
正式・暫定リリースの区別はバージョン番号（X が変わるか否か）で表現し、Release 自体は毎回作成する。

---

## CI/CD の動作まとめ

| イベント | 実行されるワークフロー |
|---------|----------------------|
| feature・bugfix → develop の PR | ビルド・テスト |
| develop へのマージ後 push | ビルド・テスト + 検証環境デプロイ |
| develop → main の PR（リリース PR） | ビルド・テスト |
| main 向け PR のマージ（タイトルが `Release v*`） | タグ作成・GitHub Release 作成 |
| main 向け PR のマージ（その他） | なし |
