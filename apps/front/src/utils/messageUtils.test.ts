import { MessageUtils } from './messageUtils';
import { MessageDto } from '@/dto/messageDto';

/** テスト用メッセージリスト */
const TEST_MESSAGE_LIST: MessageDto[] = [
    { messageNo: 1, messageType: 1, messageContent: "{0}を入力してください。" },
    { messageNo: 2, messageType: 1, messageContent: "{0}は{1}文字以上で入力してください。" },
    { messageNo: 3, messageType: 2, messageContent: "{0}は{1}文字以下で入力してください。" },
    { messageNo: 4, messageType: 3, messageContent: "保存しました。" },
];

/**
 * MessageUtilsのテストクラス
 */
describe('MessageUtils', () => {

    // ----------------------------------------------------------------
    // getMessageDto
    // ----------------------------------------------------------------
    describe('getMessageDto', () => {

        test('単一プレースホルダを置換してDTOを返す', () => {
            const result = MessageUtils.getMessageDto(TEST_MESSAGE_LIST, 1, 'ユーザ名');
            expect(result.type).toBe(1);
            expect(result.message).toBe('ユーザ名を入力してください。');
        });

        test('複数プレースホルダを置換してDTOを返す', () => {
            const result = MessageUtils.getMessageDto(TEST_MESSAGE_LIST, 2, 'パスワード', '8');
            expect(result.type).toBe(1);
            expect(result.message).toBe('パスワードは8文字以上で入力してください。');
        });

        test('プレースホルダがないメッセージはそのまま返す', () => {
            const result = MessageUtils.getMessageDto(TEST_MESSAGE_LIST, 4);
            expect(result.type).toBe(3);
            expect(result.message).toBe('保存しました。');
        });

        test('存在しないメッセージ番号の場合はtype=0, message=""のDTOを返す', () => {
            const result = MessageUtils.getMessageDto(TEST_MESSAGE_LIST, 999, '値');
            expect(result.type).toBe(0);
            expect(result.message).toBe('');
        });
    });

    // ----------------------------------------------------------------
    // getMessageType
    // ----------------------------------------------------------------
    describe('getMessageType', () => {

        test('メッセージ番号に対応する種別番号を返す', () => {
            const result = MessageUtils.getMessageType(TEST_MESSAGE_LIST, 2);
            expect(result).toBe(1);
        }
        );

        test('存在しないメッセージ番号の場合は0を返す', () => {
            const result = MessageUtils.getMessageType(TEST_MESSAGE_LIST, 999);
            expect(result).toBe(0);
        });
    });

    // ----------------------------------------------------------------
    // getFormatMessage
    // ----------------------------------------------------------------
    describe('getFormatMessage', () => {

        test('単一プレースホルダを置換した文字列を返す', () => {
            const result = MessageUtils.getFormatMessage(TEST_MESSAGE_LIST, 1, 'メールアドレス');
            expect(result).toBe('メールアドレスを入力してください。');
        });

        test('複数プレースホルダを置換した文字列を返す', () => {
            const result = MessageUtils.getFormatMessage(TEST_MESSAGE_LIST, 2, '氏名', '2');
            expect(result).toBe('氏名は2文字以上で入力してください。');
        });

        test('存在しないメッセージ番号の場合は空文字を返す', () => {
            const result = MessageUtils.getFormatMessage(TEST_MESSAGE_LIST, 999, '値');
            expect(result).toBe('');
        });
    });
});
