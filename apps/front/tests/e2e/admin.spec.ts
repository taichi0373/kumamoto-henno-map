import { test, expect } from '@playwright/test';
import { loginAsUser, signupAndLogin } from './helpers/auth';

/** 管理者ユーザー情報（global-setup.ts で再投入されるシードデータ） */
const ADMIN_USERNAME = 'admin';
const ADMIN_PASSWORD = 'admin';

test.describe('管理者画面', () => {
  /**
   * アクセス制御テスト
   * 未ログイン・一般ユーザーが /admin/* にアクセスした場合のリダイレクトを確認
   */
  test.describe('アクセス制御', () => {
    test('未ログインで /admin/benefits にアクセスすると / にリダイレクトされる', async ({ page }) => {
      await page.goto('/admin/benefits');
      await page.waitForURL('/');
      await expect(page).toHaveURL(/\/$/);
    });

    test('一般ユーザーは /admin/benefits にアクセスすると / にリダイレクトされる', async ({ page }) => {
      await signupAndLogin(page, 'e2e-regular', 'e2etest00', 'e2e-regular@e2e.com');
      await page.goto('/admin/benefits');
      await page.waitForURL('/');
      await expect(page).toHaveURL(/\/$/);
    });
  });

  /**
   * 管理者操作テスト
   * 管理者ログイン後の CRUD とページネーション操作を確認
   */
  test.describe('管理者操作', () => {
    test.beforeEach(async ({ page }) => {
      await loginAsUser(page, ADMIN_USERNAME, ADMIN_PASSWORD);
    });

    /**
     * 特典カテゴリ（BENEFIT_CATEGORY）の登録・削除フロー
     * - AdminBenefitCategoryPage は FK 依存がなく独立してテスト可能
     * - global-setup.ts で E2E% プレフィックスのカテゴリを事前クリーンアップ済み
     */
    test('特典カテゴリの登録・削除ができる', async ({ page }) => {
      await page.goto('/admin/benefit-categories');
      await page.waitForLoadState('networkidle');

      // ---- 新規登録 ----
      await page.getByRole('button', { name: '新規登録' }).click();
      const dialog = page.locator('.p-dialog');
      await dialog.waitFor({ state: 'visible' });

      // フォーム入力（isActive は openCreateDialog 内でデフォルト '1' が設定済み）
      await dialog.locator('input[placeholder="01"]').fill('E2ECAT');
      await dialog.locator('input[placeholder="カテゴリ名称"]').fill('E2Eテスト用カテゴリ');

      await dialog.getByRole('button', { name: '保存' }).click();

      // 登録成功トーストを確認
      await expect(page.locator('.p-toast-message').filter({ hasText: '登録' })).toBeVisible();
      await page.waitForLoadState('networkidle');

      // テーブルに追加されたことを確認
      await expect(page.getByText('E2Eテスト用カテゴリ')).toBeVisible();

      // ---- 削除 ----
      const row = page.locator('tr', { hasText: 'E2Eテスト用カテゴリ' });
      await row.getByRole('button', { name: '削除' }).click();

      const deleteDialog = page.locator('.p-dialog').filter({ hasText: '削除確認' });
      await deleteDialog.waitFor({ state: 'visible' });
      await deleteDialog.getByRole('button', { name: '削除' }).click();

      // 削除成功トーストを確認
      await expect(page.locator('.p-toast-message').filter({ hasText: '削除' })).toBeVisible();
      await page.waitForLoadState('networkidle');

      // テーブルから削除されたことを確認
      await expect(page.getByText('E2Eテスト用カテゴリ')).not.toBeVisible();
    });

    /**
     * 自治体一覧のページネーション
     * DML シードデータで 30 件以上の自治体が投入されており、ページ 2 が存在することを前提とする
     */
    test('自治体一覧でページ 2 に移動できる', async ({ page }) => {
      await page.goto('/admin/municipalities');
      await page.waitForLoadState('networkidle');

      // AppPaginator が表示されていることを確認
      const paginator = page.locator('.p-paginator');
      await expect(paginator).toBeVisible();

      // ページ 2 ボタンをクリック
      await paginator.locator('.p-paginator-page', { hasText: '2' }).click();
      await page.waitForLoadState('networkidle');

      // ページ 2 のデータが表示されていることを確認
      await expect(page.locator('table tbody tr').first()).toBeVisible();
    });
  });
});
