import { MessageDto } from "@/dto/messageDto";

/** メッセージ番号定数 */
const MESSAGE_NO = {
    MSG_001: 1, // {0}を入力してください。
    MSG_002: 2, // {0}は{1}文字以上で入力してください。
    MSG_003: 3, // {0}は{1}文字以下で入力してください。
    MSG_004: 4, // {0}の形式が正しくありません。
    MSG_005: 5, // {0}は全角文字で入力してください。
    MSG_006: 6, // {0}は半角文字で入力してください。
    MSG_007: 7, // {0}は半角英数字で入力してください。
    MSG_008: 8, // {0}と{1}が一致しません。
} as const;

/** メッセージリスト（message形式） */
const MESSAGE_LIST: MessageDto[] = [
    { messageNo: 1, messageType: 1, messageContent: "{0}を入力してください。" },
    { messageNo: 2, messageType: 1, messageContent: "{0}は{1}文字以上で入力してください。" },
    { messageNo: 3, messageType: 1, messageContent: "{0}は{1}文字以下で入力してください。" },
    { messageNo: 4, messageType: 1, messageContent: "{0}の形式が正しくありません。" },
    { messageNo: 5, messageType: 1, messageContent: "{0}は全角文字で入力してください。" },
    { messageNo: 6, messageType: 1, messageContent: "{0}は半角文字で入力してください。" },
    { messageNo: 7, messageType: 1, messageContent: "{0}は半角英数字で入力してください。" },
];

/** メッセージ種別定数 */
const ErrorMessageKbn = {
    ERROR: 1,
    WARNING: 2,
    SUCCESS: 3,
    INFO: 4,
} as const;

const API_RESPONSE_MESSAGE = {
    CREATE_SUCCESS: '登録しました',
    READ_SUCCESS: '取得しました',
    UPDATE_SUCCESS: '更新しました',
    DELETE_SUCCESS: '削除しました',
    LOGIN_SUCCESS: 'ログインしました',
    CREATE_FAILED: '登録に失敗しました',
    READ_FAILED: '取得に失敗しました',
    UPDATE_FAILED: '更新に失敗しました',
    DELETE_FAILED: '削除に失敗しました',
    LOGIN_FAILED: 'ログインに失敗しました',
    API_ERROR: 'APIエラーが発生しました',
    DATA_NOT_FOUND: '該当データが存在しません',
}

export { MESSAGE_LIST, MESSAGE_NO, ErrorMessageKbn, API_RESPONSE_MESSAGE };
