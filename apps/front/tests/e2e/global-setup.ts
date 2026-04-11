import { Client } from 'pg';

/**
 * Playwrightのグローバルセットアップ
 * テスト実行前にUSERSテーブル（および関連テーブル）を全件削除する
 */
async function globalSetup() {
  const client = new Client({
    host: process.env.DB_HOST ?? 'localhost',
    port: Number(process.env.DB_PORT ?? 5432),
    database: process.env.DB_NAME ?? 'benefit_map',
    user: process.env.DB_USERNAME ?? 'user',
    password: process.env.DB_PASSWORD ?? 'pass',
  });

  await client.connect();
  try {
    // USERSテーブルを全件削除
    // CASCADE: REFRESH_TOKENS・PASSWORD_RESET_TOKENSなど参照テーブルも連鎖削除
    // RESTART IDENTITY: USER_IDのシーケンスを1からリセット
    await client.query('TRUNCATE TABLE USERS RESTART IDENTITY CASCADE');
    console.log('[global-setup] USERSテーブルを初期化しました');
  } finally {
    await client.end();
  }
}

export default globalSetup;
