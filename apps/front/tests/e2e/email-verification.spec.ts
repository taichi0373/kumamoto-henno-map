import { test, expect } from '@playwright/test';
import { insertEmailVerificationToken } from './helpers/db';

/** テスト用ユーザー情報 */
const USERNAME = 'email-verify-test';
const PASSWORD = 'verifytest01';
const EMAIL = 'email-verify@example.com';

/** E2E用の確認トークン（平文） */
const PLAIN_TOKEN = 'e2e-email-verification-token';

/**
 * メールアドレス確認テスト
 * 未確認ログイン / 確認画面 / 再送画面
 * ※ テストの順序に依存するため serial で実行する
 */
test.describe.serial('メールアドレス確認', () => {

  /** 未確認ユーザーを API で登録する */
  test.beforeAll(async ({ request }) => {
    const response = await request.post('/kumamoto-henno-map/api/users/signup', {
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

  test('?signupSuccess=1 クエリ付きアクセスで確認メール送信メッセージが表示される', async ({ page }) => {
    await page.goto('/login?signupSuccess=1');
    await page.waitForLoadState('networkidle');
    await expect(page.getByText('確認メールを送信しました。', { exact: false })).toBeVisible();
  });

  test('未確認ユーザーが正しいパスワードでログインすると確認を促すメッセージが表示される', async ({ page }) => {
    await page.goto('/login');
    await page.waitForLoadState('networkidle');
    await page.locator('input#username').fill(USERNAME);
    await page.locator('input#password').fill(PASSWORD);
    await page.getByRole('button', { name: 'ログイン' }).click();
    await page.waitForLoadState('networkidle');
    await expect(
      page.getByText('メールアドレスの確認が完了していません。確認メールのリンクから確認を完了してください。')
    ).toBeVisible();
    await expect(page).toHaveURL(/\/login/);
  });

  test('未確認ユーザーでもパスワードが誤っている場合は通常の認証エラーが表示される', async ({ page }) => {
    await page.goto('/login');
    await page.waitForLoadState('networkidle');
    await page.locator('input#username').fill(USERNAME);
    await page.locator('input#password').fill('wrongpassword');
    await page.getByRole('button', { name: 'ログイン' }).click();
    await page.waitForLoadState('networkidle');
    await expect(page.getByText('ユーザ名またはパスワードが正しくありません')).toBeVisible();
  });

  test('トークンなしで確認画面を開くと再送画面へ遷移する', async ({ page }) => {
    await page.goto('/verify-email');
    await page.waitForURL('**/resend-verification');
    await expect(page).toHaveURL(/\/resend-verification/);
  });

  test('無効なトークンで確認するとエラーメッセージが表示される', async ({ page }) => {
    await page.goto('/verify-email?token=invalid-token');
    await page.waitForLoadState('networkidle');
    await page.getByRole('button', { name: 'メールアドレスを確認する' }).click();
    await page.waitForLoadState('networkidle');
    await expect(page.getByText('このリンクは無効または期限切れです。')).toBeVisible();
  });

  test('有効なトークンで確認するとログインできるようになる', async ({ page }) => {
    await insertEmailVerificationToken(USERNAME, PLAIN_TOKEN);

    // リンクを開いただけでは確定しない（ボタン押下で確定する）
    await page.goto(`/verify-email?token=${PLAIN_TOKEN}`);
    await page.waitForLoadState('networkidle');
    await page.getByRole('button', { name: 'メールアドレスを確認する' }).click();
    await page.waitForLoadState('networkidle');
    await expect(page.getByText('メールアドレスの確認が完了しました。')).toBeVisible();

    // ログイン画面へ遷移してログインできる
    await page.getByText('ログインへ').click();
    await page.waitForURL('**/login');
    await page.locator('input#username').fill(USERNAME);
    await page.locator('input#password').fill(PASSWORD);
    await page.getByRole('button', { name: 'ログイン' }).click();
    await page.waitForURL('/');
    await expect(page.locator('.user-name')).toContainText(USERNAME);
  });

  test('使用済みのトークンで再度確認するとエラーメッセージが表示される', async ({ page }) => {
    await page.goto(`/verify-email?token=${PLAIN_TOKEN}`);
    await page.waitForLoadState('networkidle');
    await page.getByRole('button', { name: 'メールアドレスを確認する' }).click();
    await page.waitForLoadState('networkidle');
    await expect(page.getByText('このリンクは無効または期限切れです。')).toBeVisible();
  });
});

/**
 * 確認メール再送画面テスト
 * バリデーション / フォームUI / 正常送信
 */
test.describe('確認メール再送画面', () => {

  test.beforeEach(async ({ page }) => {
    await page.goto('/resend-verification');
    await page.waitForLoadState('networkidle');
  });

  test('メールアドレス未入力でエラーが表示される', async ({ page }) => {
    await page.getByRole('button', { name: '確認メールを再送' }).click();
    await expect(page.getByText('メールアドレスを入力してください')).toBeVisible();
  });

  test('メールアドレスの形式が不正でエラーが表示される', async ({ page }) => {
    await page.locator('input#email').fill('invalid-format');
    await page.getByRole('button', { name: '確認メールを再送' }).click();
    await expect(page.getByText('メールアドレスの形式が正しくありません')).toBeVisible();
  });

  test('「ログインに戻る」リンクで /login へ遷移する', async ({ page }) => {
    await page.getByText('ログインに戻る').click();
    await page.waitForURL('**/login');
    await expect(page).toHaveURL(/\/login/);
  });

  test('正常なメールアドレスで送信すると成功メッセージが表示される', async ({ page }) => {
    await page.locator('input#email').fill('any@example.com');
    await page.getByRole('button', { name: '確認メールを再送' }).click();
    await page.waitForLoadState('networkidle');
    await expect(
      page.getByText('確認が完了していないメールアドレスの場合、確認メールを送信しました。メールのリンクから確認を完了してください。')
    ).toBeVisible();
  });

  test('ログイン画面の「確認メールが届かない方はこちら」リンクで再送画面へ遷移する', async ({ page }) => {
    await page.goto('/login');
    await page.waitForLoadState('networkidle');
    await page.getByText('確認メールが届かない方はこちら').click();
    await page.waitForURL('**/resend-verification');
    await expect(page).toHaveURL(/\/resend-verification/);
  });
});
