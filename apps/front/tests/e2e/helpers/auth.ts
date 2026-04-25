import type { Page } from '@playwright/test';

/**
 * UIログインヘルパー
 * ログインページを開き、指定のユーザー情報でログイン操作を行う
 */
export async function loginAsUser(page: Page, username: string, password: string): Promise<void> {
  await page.goto('/login');
  await page.waitForLoadState('networkidle');
  await page.locator('input#username').fill(username);
  await page.locator('input#password').fill(password);
  await page.getByRole('button', { name: 'ログイン' }).click();
  await page.waitForURL('/');
}

/**
 * テスト用ユーザーをサインアップしてからログインする
 * テスト独立性を確保するために change-password.spec.ts などで使用する
 *
 * サインアップが失敗した場合（既存ユーザーによる重複など）は
 * 直接ログインを試みるフォールバック処理を行う
 */
export async function signupAndLogin(
  page: Page,
  username: string,
  password: string,
  email: string
): Promise<void> {
  // 新規登録
  await page.goto('/signup');
  await page.waitForLoadState('networkidle');
  await page.locator('input#username').fill(username);
  await page.locator('input#password').fill(password);
  await page.locator('input#confirmPassword').fill(password);
  await page.locator('input#email').fill(email);
  await page.locator('#address').click();
  await page.getByRole('option', { name: '熊本市' }).click();
  await page.locator('input#birthDate').fill('1961/09/23');
  await page.keyboard.press('Tab');
  await page.locator('#licenseStatus').click();
  await page.getByRole('option', { name: '返納' }).click();
  await page.locator('input#licenseSurrenderedAt').fill('2025/09/23');
  await page.keyboard.press('Tab');
  await page.getByRole('button', { name: '新規登録' }).click();

  // サインアップ成功時は /login へリダイレクトされる
  // 失敗時（既存ユーザーの重複など）は /signup に留まるため、直接ログインへフォールバック
  const redirected = await page
    .waitForURL('**/login', { timeout: 10000 })
    .then(() => true)
    .catch(() => false);

  if (!redirected) {
    // サインアップ失敗: 直接ログインページへ遷移
    await page.goto('/login');
    await page.waitForLoadState('networkidle');
  }

  // ログイン
  await page.locator('input#username').fill(username);
  await page.locator('input#password').fill(password);
  await page.getByRole('button', { name: 'ログイン' }).click();
  await page.waitForURL('/');
}
