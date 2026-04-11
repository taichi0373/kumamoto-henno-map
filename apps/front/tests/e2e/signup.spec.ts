import { test, expect } from '@playwright/test';

/**
 * 新規登録画面テスト
 * バリデーション / フォームUI
 */
test.describe('新規登録画面', () => {

  test.beforeEach(async ({ page }) => {
    await page.goto('/signup');
    await page.waitForLoadState('networkidle');
  });

  // ---- ユーザー名バリデーション ----

  test('ユーザー名未入力でエラーが表示される', async ({ page }) => {
    await page.getByRole('button', { name: '新規登録' }).click();
    await expect(page.getByText('ユーザ名を入力してください')).toBeVisible();
  });

  test('ユーザー名が2文字（最小3文字未満）でエラーが表示される', async ({ page }) => {
    await page.locator('input#username').fill('ab');
    await page.getByRole('button', { name: '新規登録' }).click();
    await expect(page.getByText('ユーザ名は3文字以上で入力してください')).toBeVisible();
  });

  test('ユーザー名が31文字（最大30文字超）でエラーが表示される', async ({ page }) => {
    await page.locator('input#username').fill('a'.repeat(31));
    await page.getByRole('button', { name: '新規登録' }).click();
    await expect(page.getByText('ユーザ名は30文字以下で入力してください')).toBeVisible();
  });

  test('ユーザー名に日本語を含む場合でエラーが表示される', async ({ page }) => {
    await page.locator('input#username').fill('テストユーザー');
    await page.getByRole('button', { name: '新規登録' }).click();
    await expect(page.getByText('ユーザ名の形式が正しくありません')).toBeVisible();
  });

  // ---- パスワードバリデーション ----

  test('パスワード未入力でエラーが表示される', async ({ page }) => {
    await page.locator('input#username').fill('valid-user');
    await page.getByRole('button', { name: '新規登録' }).click();
    await expect(page.getByText('パスワードを入力してください')).toBeVisible();
  });

  test('パスワードが7文字（最小8文字未満）でエラーが表示される', async ({ page }) => {
    await page.locator('input#password').fill('pass123');
    await page.getByRole('button', { name: '新規登録' }).click();
    await expect(page.getByText('パスワードは8文字以上で入力してください')).toBeVisible();
  });

  test('パスワード確認未入力でエラーが表示される', async ({ page }) => {
    await page.locator('input#password').fill('password123');
    await page.getByRole('button', { name: '新規登録' }).click();
    await expect(page.getByText('パスワード確認を入力してください')).toBeVisible();
  });

  test('パスワードとパスワード確認が不一致でエラーが表示される', async ({ page }) => {
    await page.locator('input#password').fill('password123');
    await page.locator('input#confirmPassword').fill('different456');
    await page.getByRole('button', { name: '新規登録' }).click();
    await expect(page.getByText('パスワードとパスワード確認が一致しません')).toBeVisible();
  });

  // ---- その他フィールドバリデーション ----

  test('メールアドレス未入力でエラーが表示される', async ({ page }) => {
    await page.getByRole('button', { name: '新規登録' }).click();
    await expect(page.getByText('メールアドレスを入力してください')).toBeVisible();
  });

  test('メールアドレスの形式が不正でエラーが表示される', async ({ page }) => {
    await page.locator('input#email').fill('invalid-email');
    await page.getByRole('button', { name: '新規登録' }).click();
    await expect(page.getByText('メールアドレスの形式が正しくありません')).toBeVisible();
  });

  test('生年月日未入力でエラーが表示される', async ({ page }) => {
    await page.getByRole('button', { name: '新規登録' }).click();
    await expect(page.getByText('生年月日を入力してください')).toBeVisible();
  });

  test('居住地域未入力でエラーが表示される', async ({ page }) => {
    await page.getByRole('button', { name: '新規登録' }).click();
    await expect(page.getByText('居住地域を入力してください')).toBeVisible();
  });

  test('運転免許の所持状況未入力でエラーが表示される', async ({ page }) => {
    await page.getByRole('button', { name: '新規登録' }).click();
    await expect(page.getByText('運転免許の所持状況を入力してください')).toBeVisible();
  });

  // ---- フォームUIテスト ----

  test('「すでにアカウントをお持ちの方はこちら」リンクで /login へ遷移する', async ({ page }) => {
    await page.getByText('すでにアカウントをお持ちの方はこちら').click();
    await page.waitForURL('**/login');
    await expect(page).toHaveURL(/\/login/);
  });
});
