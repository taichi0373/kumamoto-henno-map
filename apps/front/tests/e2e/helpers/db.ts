import { createHash } from 'crypto';
import { Client } from 'pg';

/**
 * テスト用DBに接続して処理を実行する
 * 接続情報は global-setup.ts と同じ環境変数（.env.test）から取得する
 */
async function withDbClient<T>(fn: (client: Client) => Promise<T>): Promise<T> {
  const client = new Client({
    host: process.env.DB_HOST,
    port: Number(process.env.DB_PORT),
    database: process.env.DB_NAME,
    user: process.env.DB_USERNAME,
    password: process.env.DB_PASSWORD,
  });
  await client.connect();
  try {
    return await fn(client);
  } finally {
    await client.end();
  }
}

/**
 * 指定ユーザーのメールアドレスを確認済みにする
 * 確認メールのリンクはE2Eから取得できないため、DBを直接更新してログイン可能な状態にする
 */
export async function markEmailVerified(username: string): Promise<void> {
  await withDbClient((client) =>
    client.query(
      'UPDATE USERS SET EMAIL_VERIFIED_AT = CURRENT_TIMESTAMP WHERE USERNAME = $1 AND EMAIL_VERIFIED_AT IS NULL',
      [username],
    ),
  );
}

/**
 * 指定ユーザーの確認トークンを登録する
 * DBにはSHA-256ハッシュのみ保存されるため、既知の平文トークンのハッシュを直接登録する
 */
export async function insertEmailVerificationToken(username: string, plainToken: string): Promise<void> {
  const tokenHash = createHash('sha256').update(plainToken, 'utf8').digest('hex');
  await withDbClient((client) =>
    client.query(
      `INSERT INTO EMAIL_VERIFICATION_TOKENS (USER_ID, TOKEN, NEW_EMAIL, EXPIRES_AT, USED, SYS_CREATED_AT, SYS_UPDATED_AT)
       SELECT USER_ID, $2, NULL, CURRENT_TIMESTAMP + INTERVAL '24 hours', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
       FROM USERS WHERE USERNAME = $1`,
      [username, tokenHash],
    ),
  );
}
