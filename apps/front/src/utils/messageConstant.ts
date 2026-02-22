const MessageConstant = {
    MESSAGE_NO_001: '{0}を入力してください。',
    MESSAGE_NO_002: '{0}は{1}文字以上で入力してください。',
    MESSAGE_NO_003: '{0}は{1}文字以下で入力してください。',
    MESSAGE_NO_004: '{0}の形式が正しくありません。',
    MESSAGE_NO_005: '{0}は全角文字で入力してください。',
    MESSAGE_NO_006: '{0}は半角文字で入力してください。',
    MESSAGE_NO_007: '{0}は半角英数字で入力してください。',


};

const ErrorMessageConstant = {
    API_ERROR: 'API実行時にエラーが発生しました。',
    CREATE_ERROR: '登録に失敗しました。',
    READ_ERROR: '取得に失敗しました。',
    UPDATE_ERROR: '更新に失敗しました。',
    DELETE_ERROR: '削除に失敗しました。',
};

const ErrorMessageKbn = {
    ERROR: 1,
    WARNING: 2,
    SUCCESS: 3,
    INFO: 4,
};

export { MessageConstant, ErrorMessageConstant, ErrorMessageKbn };
