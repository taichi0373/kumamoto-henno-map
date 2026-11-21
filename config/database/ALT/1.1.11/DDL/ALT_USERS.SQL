-- 既存DB向けALT: USERS にメールアドレス確認日時を追加し、既存ユーザーを確認済みにする
-- 追加カラムは DDL/TABLE/USERS.SQL の末尾と同じ位置のため、ADD COLUMN で追加する
-- 新規構築時は DDL/TABLE/USERS.SQL に含まれるため実行不要
-- 注意: バックエンドのデプロイ前に必ず適用すること（未確認ユーザーは24時間後に自動削除されるため）
-- 実行例: psql -U <user> -d <db> -f config/database/ALT/1.1.11/DDL/ALT_USERS.SQL
\set ON_ERROR_STOP on

BEGIN;

ALTER TABLE USERS ADD COLUMN EMAIL_VERIFIED_AT TIMESTAMP;

COMMENT ON COLUMN USERS.EMAIL_VERIFIED_AT IS 'メールアドレス確認日時（NULL:未確認）';

-- 既存ユーザーは確認済み扱いにする
UPDATE USERS SET EMAIL_VERIFIED_AT = COALESCE(SYS_CREATED_AT, CURRENT_TIMESTAMP) WHERE EMAIL_VERIFIED_AT IS NULL;

COMMIT;
