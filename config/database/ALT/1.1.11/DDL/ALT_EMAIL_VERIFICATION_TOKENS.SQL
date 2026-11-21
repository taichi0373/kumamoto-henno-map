-- 既存DB向けALT: メールアドレス確認トークンテーブルを新設する
-- 新規構築時は DDL/TABLE/EMAIL_VERIFICATION_TOKENS.SQL に含まれるため実行不要
-- ALT_USERS.SQL の適用後に実行すること
-- 実行例: psql -U <user> -d <db> -f config/database/ALT/1.1.11/DDL/ALT_EMAIL_VERIFICATION_TOKENS.SQL
\set ON_ERROR_STOP on

BEGIN;

\ir '../../../DDL/TABLE/EMAIL_VERIFICATION_TOKENS.SQL'

COMMIT;
