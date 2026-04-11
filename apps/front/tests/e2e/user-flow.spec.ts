import { test, expect } from '@playwright/test';

/**
 * テスト用ユーザー情報
 * 注意: ユーザー名はバリデーション制約（半角英数字・ハイフン・アンダースコアのみ）に従う必要がある
 * global-setup.ts で USERS テーブルを TRUNCATE するため、再実行時も重複なく動作する
 */
const USERNAME = 'test-user';
const USERNAME_UPDATED = 'test-user-1';
const PASSWORD = 'test041500';
const EMAIL = 'test@example.com';

/**
 * 新規登録→ログイン→プロフィール更新→ログアウト→再ログインの一連フロー
 */
test('新規登録→ログイン→プロフィール更新→ログアウト→再ログインフロー', async ({ page }) => {

  // ---- Step 1: トップページを開く ----
  await page.goto('/');
  await page.waitForLoadState('networkidle');

  // ---- Step 2: ヘッダーの「新規登録」をクリックして遷移 ----
  await page.locator('.p-menubar-item-link', { hasText: '新規登録' }).click();
  await page.waitForURL('**/signup');
  // 自治体APIのレスポンス完了を待機
  await page.waitForLoadState('networkidle');

  // ---- Step 3: 新規登録フォームの入力 ----

  // ユーザー名
  await page.locator('input#username').fill(USERNAME);

  // パスワード
  await page.locator('input#password').fill(PASSWORD);

  // パスワード確認
  await page.locator('input#confirmPassword').fill(PASSWORD);

  // メールアドレス
  await page.locator('input#email').fill(EMAIL);

  // 居住地域（PrimeVue Select）
  await page.locator('#address').click();
  // role="option" セレクターで待機・クリック（CSSクラス名より信頼性が高い）
  await page.getByRole('option', { name: '熊本市' }).click();

  // 生年月日（PrimeVue DatePicker）
  await page.locator('input#birthDate').fill('1961/09/23');
  await page.keyboard.press('Tab');

  // 運転免許の所持状況（PrimeVue Select）
  await page.locator('#licenseStatus').click();
  await page.getByRole('option', { name: '返納' }).click();

  // 運転免許返納日（PrimeVue DatePicker）
  await page.locator('input#licenseSurrenderedAt').fill('2025/09/23');
  await page.keyboard.press('Tab');

  // ---- Step 4: 「新規登録」ボタンをクリック → ログインページへ遷移 ----
  await page.getByRole('button', { name: '新規登録' }).click();
  // 登録成功時は /login へリダイレクト
  // 失敗時（既存ユーザーの重複など）は /signup に留まるため、直接ログインへフォールバック
  const redirected = await page
    .waitForURL('**/login', { timeout: 10000 })
    .then(() => true)
    .catch(() => false);
  if (!redirected) {
    await page.goto('/login');
    await page.waitForLoadState('networkidle');
  }

  // ---- Step 5: ログイン ----
  await page.locator('input#username').fill(USERNAME);
  await page.locator('input#password').fill(PASSWORD);
  await page.getByRole('button', { name: 'ログイン' }).click();
  await page.waitForURL('http://localhost:3000/');

  // ログイン成功を確認（ヘッダーにユーザー名が表示される）
  await expect(page.locator('.user-name')).toContainText(USERNAME);

  // ---- Step 6: 「マイページ」→「プロフィール」へ遷移 ----
  await page.locator('.p-menubar-item-link', { hasText: 'マイページ' }).click();
  await page.locator('.p-menubar-submenu').waitFor({ state: 'visible' });
  await page.locator('.p-menubar-item-link', { hasText: 'プロフィール' }).click();
  await page.waitForURL('**/profile');

  // ---- Step 7: ユーザー名を更新 ----
  await page.locator('input#username').fill(USERNAME_UPDATED);
  await page.getByRole('button', { name: '更新' }).click();

  // 更新完了のトーストメッセージが表示されるまで待機
  await page.waitForTimeout(1500);

  // ---- Step 8: 「マイページ」→「ログアウト」 ----
  await page.locator('.p-menubar-item-link', { hasText: 'マイページ' }).click();
  await page.locator('.p-menubar-submenu').waitFor({ state: 'visible' });
  await page.locator('.p-menubar-item-link', { hasText: 'ログアウト' }).click();
  await page.waitForURL('**/login');

  // ---- Step 9: 更新後のユーザー名で再ログイン ----
  await page.locator('input#username').fill(USERNAME_UPDATED);
  await page.locator('input#password').fill(PASSWORD);
  await page.getByRole('button', { name: 'ログイン' }).click();
  await page.waitForURL('http://localhost:3000/');

  // 再ログイン成功を確認
  await expect(page.locator('.user-name')).toContainText(USERNAME_UPDATED);
});
