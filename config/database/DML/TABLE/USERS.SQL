-- 管理者ユーザーシードデータ
-- パスワード: admin （BCrypt強度10でハッシュ化）
-- 本番環境では必ず変更すること
INSERT INTO USERS (USERNAME, PASSWORD_HASH, EMAIL, IS_ADMIN, SYS_CREATED_AT, SYS_UPDATED_AT)
VALUES (
  'admin',
  '$2a$10$5SxwI1xYqdHgVMge2aZt2./kfkYNYI5ee3KokZW6ec1WFJVeWSfni',
  'admin@example.com',
  '1',
  CURRENT_TIMESTAMP,
  CURRENT_TIMESTAMP
);
