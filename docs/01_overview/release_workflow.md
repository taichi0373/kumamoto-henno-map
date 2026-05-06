# リリースワークフロー

## ブランチ戦略

```
main     ──● v0.1.0──────────────────────● v1.0.0──
              ↑ squash merge                   ↑ squash merge
develop  ──●──●──●──●──●──●──●──●──●──●──●──●──
              #1  #2  #3 ...                   #N
```

| ブランチ | 役割 |
|---------|------|
| `main` | 本番リリース済みコードのみ。バージョンタグが打たれた squash merge コミットだけが並ぶ |
| `develop` | 開発の主軸。フィーチャーブランチのマージ先。常に `main` より前にある |
| `feature/*` | 機能開発用。`develop` から切って `develop` へ PR を出す |

---

## 日常の開発フロー

```bash
# 1. develop から feature ブランチを作成
git checkout develop
git pull origin develop
git checkout -b feature/xxx

# 2. 開発・コミット
git commit -m "○○機能の実装"

# 3. develop へ PR を出す（GitHub上）
#    → CI（ビルド・テスト）が自動実行される
#    → レビュー・承認後にマージ
```

---

## リリース手順

### 1. develop → main へ PR を作成

GitHub 上で `develop` → `main` の PR を作成する。

- タイトル例: `Release v1.1.0`
- CI が自動実行されるので通過を確認する

### 2. Squash and merge でマージ

PR のマージ方法は必ず **"Squash and merge"** を選択する。

- コミットメッセージ: `Release vX.Y.Z`
- これにより `main` にはリリースコミット1件だけが追加される

### 3. タグを打つ

```bash
git checkout main
git pull origin main
git tag vX.Y.Z
git push origin vX.Y.Z
```

### 4. develop に main を同期する

squash merge 後は `develop` が `main` と diverge するため、必ず同期する。

```bash
git checkout develop
git merge main
git push origin develop
```

---

## バージョン番号ルール

[セマンティックバージョニング](https://semver.org/lang/ja/) に従う。

| 種別 | 例 | 変更するケース |
|-----|-----|--------------|
| メジャー | `v2.0.0` | 破壊的変更・大規模リニューアル |
| マイナー | `v1.1.0` | 後方互換の機能追加 |
| パッチ | `v1.0.1` | バグ修正・軽微な改善 |

---

## CI/CD の動作まとめ

| イベント | 実行されるワークフロー |
|---------|----------------------|
| feature → develop の PR | ビルド・テスト（差分のみ） |
| develop へのマージ後 push | ビルド・テスト + GitHub Pages デプロイ |
| develop → main の PR（リリース PR） | ビルド・テスト（差分のみ） |
| main へのマージ後 push | なし（develop 側で検証済みのため） |
