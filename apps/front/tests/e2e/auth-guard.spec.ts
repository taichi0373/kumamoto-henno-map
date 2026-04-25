import { test, expect } from '@playwright/test';

/**
 * 認証ガードテスト
 * 未ログイン状態で認証必須ルートへアクセスした場合のリダイレクト確認
 */
test.describe('認証ガード', () => {

  test('未ログインで /profile にアクセスすると /login にリダイレクトされる', async ({ page }) => {
    await page.goto('/profile');
    await page.waitForURL(/\/login/);
    await expect(page).toHaveURL(/\/login/);
  });

  test('未ログインで /profile にアクセスするとredirectクエリが付与される', async ({ page }) => {
    await page.goto('/profile');
    await page.waitForURL(/\/login/);
    await expect(page).toHaveURL(/redirect=.*profile/);
  });

  test('未ログインで /change-password にアクセスすると /login にリダイレクトされる', async ({ page }) => {
    await page.goto('/change-password');
    await page.waitForURL(/\/login/);
    await expect(page).toHaveURL(/\/login/);
  });

  test('未ログインで /change-password にアクセスするとredirectクエリが付与される', async ({ page }) => {
    await page.goto('/change-password');
    await page.waitForURL(/\/login/);
    await expect(page).toHaveURL(/redirect=.*change-password/);
  });

  test('未ログインで / にアクセスするとリダイレクトなしでホームが表示される', async ({ page }) => {
    await page.goto('/');
    await page.waitForLoadState('networkidle');
    await expect(page).toHaveURL(/\/$/);
  });

  test('未ログインで /forgot-password にアクセスするとリダイレクトなしで表示される', async ({ page }) => {
    await page.goto('/forgot-password');
    await page.waitForLoadState('networkidle');
    await expect(page).toHaveURL(/\/forgot-password/);
  });
});
