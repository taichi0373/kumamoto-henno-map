import { Client } from 'pg';

/**
 * Playwrightのグローバルセットアップ
 * テスト実行前にUSERSテーブル（および関連テーブル）を全件削除する
 */
async function globalSetup() {
  // DB接続情報は必須環境変数 — デフォルト値なし
  const host = process.env.DB_HOST;
  const port = process.env.DB_PORT;
  const database = process.env.DB_NAME;
  const user = process.env.DB_USERNAME;
  const password = process.env.DB_PASSWORD;

  const missing = ['DB_HOST', 'DB_PORT', 'DB_NAME', 'DB_USERNAME', 'DB_PASSWORD'].filter(
    (key) => !process.env[key],
  );
  if (missing.length > 0) {
    throw new Error(
      `[global-setup] 必須環境変数が設定されていません: ${missing.join(', ')}\n` +
        'テスト用の .env ファイルを確認してください。',
    );
  }

  // テスト用DB以外への誤接続を防ぐガード
  if (!database!.endsWith('_test')) {
    throw new Error(
      `[global-setup] 安全のため処理を中断しました。\n` +
        `DB_NAME="${database}" は "_test" で終わっていません。\n` +
        'テスト用DBのみ TRUNCATE を実行できます。',
    );
  }

  const client = new Client({
    host,
    port: Number(port),
    database,
    user,
    password,
  });

  await client.connect();
  try {
    // USERSテーブルを全件削除
    // CASCADE: REFRESH_TOKENS・PASSWORD_RESET_TOKENSなど参照テーブルも連鎖削除
    // RESTART IDENTITY: USER_IDのシーケンスを1からリセット
    await client.query('TRUNCATE TABLE USERS RESTART IDENTITY CASCADE');
    console.log('[global-setup] USERSテーブルを初期化しました');

    // 管理者ユーザーの再投入（USERSテーブルTRUNCATEにより削除されるため再挿入）
    // パスワード: admin (BCrypt強度10)
    await client.query(`
      INSERT INTO USERS (USERNAME, PASSWORD_HASH, EMAIL, IS_ADMIN, SYS_CREATED_AT, SYS_UPDATED_AT)
      VALUES ('admin', '$2a$10$5SxwI1xYqdHgVMge2aZt2./kfkYNYI5ee3KokZW6ec1WFJVeWSfni', 'admin@example.com', '1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    `);
    console.log('[global-setup] 管理者ユーザーを再投入しました');

    // 管理者E2EテストのCRUDフロー用残留データをクリーンアップ
    await client.query(`DELETE FROM BENEFIT_CATEGORY WHERE CATEGORY_CD LIKE 'E2E%'`);
    console.log('[global-setup] E2Eテスト用カテゴリをクリーンアップしました');
  } finally {
    await client.end();
  }
}

export default globalSetup;
