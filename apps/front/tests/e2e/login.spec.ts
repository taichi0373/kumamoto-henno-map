import { test, expect } from '@playwright/test';

/**
 * ログイン画面テスト
 * バリデーション / 認証エラー / フォームUI
 */
test.describe('ログイン画面', () => {

  test.beforeEach(async ({ page }) => {
    await page.goto('/login');
    await page.waitForLoadState('networkidle');
  });

  // ---- バリデーションテスト ----

  test('ユーザー名未入力でエラーが表示される', async ({ page }) => {
    await page.locator('input#password').fill('password123');
    await page.getByRole('button', { name: 'ログイン' }).click();
    await expect(page.getByText('ユーザ名を入力してください')).toBeVisible();
  });

  test('パスワード未入力でエラーが表示される', async ({ page }) => {
    await page.locator('input#username').fill('test-user');
    await page.getByRole('button', { name: 'ログイン' }).click();
    await expect(page.getByText('パスワードを入力してください')).toBeVisible();
  });

  test('ユーザー名・パスワードともに未入力でエラーが表示される', async ({ page }) => {
    await page.getByRole('button', { name: 'ログイン' }).click();
    await expect(page.getByText('ユーザ名を入力してください')).toBeVisible();
    await expect(page.getByText('パスワードを入力してください')).toBeVisible();
  });

  // ---- 認証エラーテスト ----

  test('誤ったユーザー名・パスワードでエラーバーが表示される', async ({ page }) => {
    await page.locator('input#username').fill('nonexistent-user');
    await page.locator('input#password').fill('wrongpassword');
    await page.getByRole('button', { name: 'ログイン' }).click();
    // APIレスポンス完了を待機
    await page.waitForLoadState('networkidle');
    await expect(page.getByText('ユーザ名またはパスワードが正しくありません')).toBeVisible();
  });

  // ---- フォームUIテスト ----

  test('「新規登録はこちら」リンクで /signup へ遷移する', async ({ page }) => {
    await page.getByText('新規登録はこちら').click();
    await page.waitForURL('**/signup');
    await expect(page).toHaveURL(/\/signup/);
  });

  test('「パスワードを忘れた方はこちら」リンクで /forgot-password へ遷移する', async ({ page }) => {
    await page.getByText('パスワードを忘れた方はこちら').click();
    await page.waitForURL('**/forgot-password');
    await expect(page).toHaveURL(/\/forgot-password/);
  });

  test('?resetSuccess=1 クエリ付きアクセスでリセット成功メッセージが表示される', async ({ page }) => {
    await page.goto('/login?resetSuccess=1');
    await page.waitForLoadState('networkidle');
    await expect(
      page.getByText('パスワードをリセットしました。新しいパスワードでログインしてください。')
    ).toBeVisible();
  });
});
