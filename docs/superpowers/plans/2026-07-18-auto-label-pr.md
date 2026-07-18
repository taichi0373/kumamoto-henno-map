# PR自動ラベル付与ワークフロー 実装計画

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** `auto-label-pr.yml` を改修し、PRのブランチ名と変更ファイルパスに基づいて適切なGitHubラベルを自動付与する。

**Architecture:** 1ジョブ・2ステップ構成。Step 1 でブランチ名を `case` 文で判定してタイプラベルとリリースノートラベルを付与し、Step 2 で `gh pr diff --name-only` の出力を grep してエリアラベルを付与する。外部アクション依存なし。

**Tech Stack:** GitHub Actions、`gh` CLI、bash シェルスクリプト

## Global Constraints

- トリガーは `pull_request_target` の `opened` のみ（セキュリティ上、コードのチェックアウト・実行は行わない）
- 権限は `pull-requests: write` のみ付与
- 外部 GitHub Actions への依存なし（`gh` CLI のみ使用）
- ラベル名は `.github/labels.yml` に定義されている日本語名を使用すること
- Dependabot PR は対象外（`dependabot.yml` で独自管理）

---

## ラベル付与ルール（参照用）

### Step 1: ブランチ名 → タイプ・リリースノートラベル

| ブランチパターン | 付与ラベル |
|----------------|-----------|
| `feature/*` | 機能追加、リリースノート対象 |
| `bugfix/*` / `hotfix/*` | バグ、リリースノート対象 |
| `docs/*` | ドキュメント、リリースノート除外 |
| `ci/*` | CI/CD、リリースノート除外 |
| `refactor/*` / `test/*` | リリースノート除外のみ |
| その他 | リリースノート対象のみ |

### Step 2: 変更ファイルパス → エリアラベル

| ファイルパスパターン | 付与ラベル |
|--------------------|-----------|
| `apps/front/` を含む | フロントエンド |
| `apps/back/` を含む | バックエンド |
| `.github/` を含む | CI/CD |

---

## Task 1: auto-label-pr.yml の全面改修

**Files:**
- Modify: `.github/workflows/auto-label-pr.yml`

**Interfaces:**
- Consumes: `github.head_ref`（ブランチ名）、`github.event.pull_request.number`（PR番号）、`github.repository`（リポジトリ名）
- Produces: PR に対して `gh pr edit --add-label` でラベルを付与

---

- [ ] **Step 1: 既存ファイルの内容を確認する**

```bash
cat .github/workflows/auto-label-pr.yml
```

期待される出力: 現在は「リリースノート対象」を全PRに付与するだけのシンプルな内容。

---

- [ ] **Step 2: ワークフローのブランチ判定ロジックを手元で疑似検証する**

以下のスクリプトをローカルで実行して、各ブランチパターンが正しくラベルに変換されることを確認する。

```bash
verify_branch_labels() {
  local BRANCH="$1"
  local LABELS=""

  case "$BRANCH" in
    feature/*)
      LABELS="機能追加,リリースノート対象"
      ;;
    bugfix/*|hotfix/*)
      LABELS="バグ,リリースノート対象"
      ;;
    docs/*)
      LABELS="ドキュメント,リリースノート除外"
      ;;
    ci/*)
      LABELS="CI/CD,リリースノート除外"
      ;;
    refactor/*|test/*)
      LABELS="リリースノート除外"
      ;;
    *)
      LABELS="リリースノート対象"
      ;;
  esac

  echo "BRANCH: $BRANCH -> LABELS: $LABELS"
}

verify_branch_labels "feature/add-search"
verify_branch_labels "bugfix/fix-login"
verify_branch_labels "hotfix/urgent-fix"
verify_branch_labels "docs/update-readme"
verify_branch_labels "ci/update-actions"
verify_branch_labels "refactor/cleanup"
verify_branch_labels "test/add-tests"
verify_branch_labels "unknown/branch"
```

期待される出力:
```
BRANCH: feature/add-search -> LABELS: 機能追加,リリースノート対象
BRANCH: bugfix/fix-login -> LABELS: バグ,リリースノート対象
BRANCH: hotfix/urgent-fix -> LABELS: バグ,リリースノート対象
BRANCH: docs/update-readme -> LABELS: ドキュメント,リリースノート除外
BRANCH: ci/update-actions -> LABELS: CI/CD,リリースノート除外
BRANCH: refactor/cleanup -> LABELS: リリースノート除外
BRANCH: test/add-tests -> LABELS: リリースノート除外
BRANCH: unknown/branch -> LABELS: リリースノート対象
```

---

- [ ] **Step 3: auto-label-pr.yml を以下の内容で全面書き換えする**

`.github/workflows/auto-label-pr.yml` を以下の内容で置き換える:

```yaml
name: Auto Label PR

on:
  # 注意: pull_request_target はフォークPRのコードをチェックアウトしないこと
  # ラベル付与のみに使用し、コードの実行・actions/checkout は行わない
  pull_request_target:
    types: [opened]

jobs:
  label:
    name: ラベルを自動付与
    runs-on: ubuntu-latest
    permissions:
      pull-requests: write

    steps:
      - name: ブランチ名でタイプ・リリースノートラベルを付与
        env:
          GH_TOKEN: ${{ github.token }}
          BRANCH: ${{ github.head_ref }}
          PR_NUMBER: ${{ github.event.pull_request.number }}
          REPO: ${{ github.repository }}
        run: |
          case "$BRANCH" in
            feature/*)
              LABELS="機能追加,リリースノート対象"
              ;;
            bugfix/*|hotfix/*)
              LABELS="バグ,リリースノート対象"
              ;;
            docs/*)
              LABELS="ドキュメント,リリースノート除外"
              ;;
            ci/*)
              LABELS="CI/CD,リリースノート除外"
              ;;
            refactor/*|test/*)
              LABELS="リリースノート除外"
              ;;
            *)
              LABELS="リリースノート対象"
              ;;
          esac
          gh pr edit "$PR_NUMBER" \
            --add-label "$LABELS" \
            --repo "$REPO"

      - name: 変更ファイルでエリアラベルを付与
        env:
          GH_TOKEN: ${{ github.token }}
          PR_NUMBER: ${{ github.event.pull_request.number }}
          REPO: ${{ github.repository }}
        run: |
          CHANGED_FILES=$(gh pr diff "$PR_NUMBER" --repo "$REPO" --name-only)

          if echo "$CHANGED_FILES" | grep -q "^apps/front/"; then
            gh pr edit "$PR_NUMBER" --add-label "フロントエンド" --repo "$REPO"
          fi

          if echo "$CHANGED_FILES" | grep -q "^apps/back/"; then
            gh pr edit "$PR_NUMBER" --add-label "バックエンド" --repo "$REPO"
          fi

          if echo "$CHANGED_FILES" | grep -q "^\.github/"; then
            gh pr edit "$PR_NUMBER" --add-label "CI/CD" --repo "$REPO"
          fi
```

---

- [ ] **Step 4: YAML の構文を検証する**

```bash
# python が使える場合（yamllint でも可）
python3 -c "import yaml; yaml.safe_load(open('.github/workflows/auto-label-pr.yml'))" && echo "YAML構文OK"
```

期待される出力:
```
YAML構文OK
```

---

- [ ] **Step 5: コミットする**

```bash
git add .github/workflows/auto-label-pr.yml
git commit -m "ci: PRのブランチ名と変更ファイルに基づくラベル自動付与に改修"
```

---

## Task 2: 動作確認

**Files:**
- 変更なし（GitHub上でPRを作成して動作確認）

**Interfaces:**
- Consumes: Task 1 で改修したワークフロー
- Produces: 各ブランチ種別・変更ファイルに応じたラベルが正しく付与されること

---

- [ ] **Step 1: ブランチをプッシュする**

```bash
git push origin ci/auto-label-pr
```

---

- [ ] **Step 2: テスト用PRを作成して動作確認する**

`ci/auto-label-pr` ブランチから `develop` へのPRを GitHub上で作成し、以下を確認する:

**確認項目:**

| 確認内容 | 期待される結果 |
|---------|--------------|
| PR作成直後に Actions が起動するか | `Auto Label PR` ワークフローが実行される |
| Step 1 でブランチ名判定が動くか | `ci/` ブランチのため「CI/CD」「リリースノート除外」が付与される |
| Step 2 で変更ファイル判定が動くか | `.github/` 変更のため「CI/CD」が付与される（重複は問題なし） |
| ワークフローがエラーなく完了するか | 両ステップが緑になる |

---

- [ ] **Step 3: 別ブランチで追加検証する（任意）**

`feature/` や `bugfix/` プレフィックスのブランチでもPRを作成し、それぞれ「機能追加」「バグ」ラベルが付与されることを確認する。

---

- [ ] **Step 4: PRをマージする**

動作確認が完了したら `ci/auto-label-pr` → `develop` へのPRをマージする。
