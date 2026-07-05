# GTFS自動更新ワークフロー 週次スケジュール変更 設計書

## 概要

GTFSデータ自動更新ワークフロー（`.github/workflows/gtfs-auto-update.yml`）の実行スケジュールを、毎日実行から毎週月曜日実行に変更する。

## 変更内容

### 対象ファイル

`.github/workflows/gtfs-auto-update.yml`

### 変更箇所

| 項目 | 変更前 | 変更後 |
|------|--------|--------|
| cronコメント | `毎日 JST 02:00（UTC 17:00）に実行` | `毎週月曜日 JST 02:00（UTC 17:00）に実行` |
| cron式 | `0 17 * * *` | `0 17 * * 1` |

### 変更しない項目

- `force_update` 入力（手動実行時の強制更新オプション）
- `threshold_days` 入力（デフォルト: 7日）
- check-expiry / build / deploy / notify 各ジョブの処理内容

## 設計判断

- `threshold_days` のデフォルト7日は週次実行（7日間隔）と整合している
- GTFSデータの有効期限は通常数ヶ月単位のため、週次＋閾値7日で「期限が近づいたときだけ更新」という設計意図を維持できる
- `workflow_dispatch` による手動実行は引き続き利用可能
