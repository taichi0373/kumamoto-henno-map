import { test, expect } from '@playwright/test';
import { loginAsUser } from './helpers/auth';

/** テスト用ユーザー情報 */
const USERNAME = 'pw-change-test';
const PASSWORD = 'changetest01';
const EMAIL = 'pw-change@example.com';

/**
 * パスワード変更画面テスト
 * バリデーション / フォームUI
 * ※ 認証必須のため beforeAll でサインアップを1回実施し、beforeEach でログインする
 */
test.describe('パスワード変更画面', () => {

  /**
   * サインアップはテストスイート全体で1回のみ実行
   * UI操作による待ち時間を避けるため、request fixture でサインアップAPIを呼ぶ
   */
  test.beforeAll(async ({ request }) => {
    const response = await request.post('/benefit-map/api/users/signup', {
      data: {
        username: USERNAME,
        password: PASSWORD,
        confirmPassword: PASSWORD,
        email: EMAIL,
        birthDate: '1961-09-23',
        address: '431001', // 熊本市
        licenseStatus: '2', // 返納
        licenseSurrenderedAt: '2025-09-23',
      },
    });
    // 201 Created: 登録成功 / 409 Conflict: 既に存在する（継続可能）
    if (response.status() !== 201 && response.status() !== 409) {
      throw new Error(`サインアップAPI失敗: status=${response.status()}`);
    }
  });

  /** 各テスト前にログイン後、マイページ→プロフィール→パスワードを変更するの順で遷移 */
  test.beforeEach(async ({ page }) => {
    await loginAsUser(page, USERNAME, PASSWORD);
    // ヘッダーの「マイページ」メニューをクリックしてサブメニューを展開
    await page.getByText('マイページ').click();
    // 「プロフィール」をクリックして /profile へ遷移
    await page.getByText('プロフィール').click();
    await page.waitForURL('**/profile');
    // 「パスワードを変更する」リンクをクリックして /change-password へ遷移
    await page.getByText('パスワードを変更する').click();
    await page.waitForURL('**/change-password');
    await page.waitForLoadState('networkidle');
  });

  // ---- バリデーションテスト ----

  test('現在のパスワード未入力でエラーが表示される', async ({ page }) => {
    await page.locator('input#newPassword').fill('newpassword123');
    await page.locator('input#confirmNewPassword').fill('newpassword123');
    await page.getByRole('button', { name: '変更する' }).click();
    await expect(page.getByText('現在のパスワードを入力してください')).toBeVisible();
  });

  test('新しいパスワード未入力でエラーが表示される', async ({ page }) => {
    await page.locator('input#currentPassword').fill(PASSWORD);
    await page.getByRole('button', { name: '変更する' }).click();
    await expect(page.getByText('新しいパスワードを入力してください')).toBeVisible();
  });

  test('新しいパスワードが7文字（最小8文字未満）でエラーが表示される', async ({ page }) => {
    await page.locator('input#currentPassword').fill(PASSWORD);
    await page.locator('input#newPassword').fill('short77');
    await page.getByRole('button', { name: '変更する' }).click();
    await expect(page.getByText('新しいパスワードは8文字以上で入力してください')).toBeVisible();
  });

  test('新しいパスワード（確認）未入力でエラーが表示される', async ({ page }) => {
    await page.locator('input#currentPassword').fill(PASSWORD);
    await page.locator('input#newPassword').fill('newpassword123');
    await page.getByRole('button', { name: '変更する' }).click();
    await expect(page.getByText('新しいパスワード（確認）を入力してください')).toBeVisible();
  });

  test('新しいパスワードと確認が不一致でエラーが表示される', async ({ page }) => {
    await page.locator('input#currentPassword').fill(PASSWORD);
    await page.locator('input#newPassword').fill('newpassword123');
    await page.locator('input#confirmNewPassword').fill('differentpass456');
    await page.getByRole('button', { name: '変更する' }).click();
    await expect(page.getByText('新しいパスワードと新しいパスワード（確認）が一致しません')).toBeVisible();
  });

  test('現在のパスワードが誤りの場合にエラーバーが表示される', async ({ page }) => {
    await page.locator('input#currentPassword').fill('wrongpassword');
    await page.locator('input#newPassword').fill('newpassword123');
    await page.locator('input#confirmNewPassword').fill('newpassword123');
    await page.getByRole('button', { name: '変更する' }).click();
    await page.waitForLoadState('networkidle');
    await expect(page.getByText('現在のパスワードが正しくありません')).toBeVisible();
  });

  // ---- フォームUIテスト ----

  test('「プロフィールに戻る」リンクで /profile へ遷移する', async ({ page }) => {
    await page.getByText('プロフィールに戻る').click();
    await page.waitForURL('**/profile');
    await expect(page).toHaveURL(/\/profile/);
  });
});
