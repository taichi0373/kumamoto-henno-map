import { test, expect } from '@playwright/test';

/**
 * パスワードリセット画面テスト
 * バリデーション / フォームUI / 正常送信
 */
test.describe('パスワードリセット画面', () => {

  test.beforeEach(async ({ page }) => {
    await page.goto('/forgot-password');
    await page.waitForLoadState('networkidle');
  });

  // ---- バリデーションテスト ----

  test('メールアドレス未入力でエラーが表示される', async ({ page }) => {
    await page.getByRole('button', { name: 'リセットメールを送信' }).click();
    await expect(page.getByText('メールアドレスを入力してください')).toBeVisible();
  });

  test('メールアドレスの形式が不正でエラーが表示される', async ({ page }) => {
    await page.locator('input#email').fill('invalid-format');
    await page.getByRole('button', { name: 'リセットメールを送信' }).click();
    await expect(page.getByText('メールアドレスの形式が正しくありません')).toBeVisible();
  });

  // ---- フォームUIテスト ----

  test('「ログインに戻る」リンクで /login へ遷移する', async ({ page }) => {
    await page.getByText('ログインに戻る').click();
    await page.waitForURL('**/login');
    await expect(page).toHaveURL(/\/login/);
  });

  test('正常なメールアドレスで送信すると成功メッセージが表示される', async ({ page }) => {
    await page.locator('input#email').fill('any@example.com');
    await page.getByRole('button', { name: 'リセットメールを送信' }).click();
    await page.waitForLoadState('networkidle');
    await expect(
      page.getByText('パスワードリセットメールを送信しました。メールのリンクからパスワードを変更してください。')
    ).toBeVisible();
  });
});
