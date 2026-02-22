import { MessageUtils } from './messageUtils';

/**
 * MessageUtilsのテストクラス
 */
describe('MessageUtils', () => {

  // ----------------------------------------------------------------
  // format
  // ----------------------------------------------------------------
  describe('format', () => {

    test('単一プレースホルダを置換する', () => {
      const result = MessageUtils.format('こんにちは、{0}さん！', '田中');
      expect(result).toBe('こんにちは、田中さん！');
    });

    test('複数プレースホルダを置換する', () => {
      const result = MessageUtils.format('{0}年{1}月{2}日', 2026, 2, 11);
      expect(result).toBe('2026年2月11日');
    });

    test('nullパターンの場合はnullを返す', () => {
      const result = MessageUtils.format(null, '値');
      expect(result).toBeNull();
    });

    test('数値を埋め込む', () => {
      const result = MessageUtils.format('価格: {0}円', 1000);
      expect(result).toBe('価格: 1000円');
    });

    test('真偽値を埋め込む', () => {
      const result = MessageUtils.format('在庫: {0}', true);
      expect(result).toBe('在庫: true');
    });

    test('数値と真偽値を複数埋め込む', () => {
      const result = MessageUtils.format('価格: {0}円、在庫: {1}', 1000, true);
      expect(result).toBe('価格: 1000円、在庫: true');
    });

    test('プレースホルダがない場合はそのまま返す', () => {
      const result = MessageUtils.format('プレースホルダなし');
      expect(result).toBe('プレースホルダなし');
    });

    test('引数が余分にある場合は余分な引数は無視される', () => {
      const result = MessageUtils.format('{0}のみ', '値1', '値2');
      expect(result).toBe('値1のみ');
    });
  });
});
