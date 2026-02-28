import { StringEditUtils } from './stringEditUtils';

/**
 * StringEditUtilsのテストクラス
 */
describe('StringEditUtils', () => {

  describe('replaceFullWidthHiraganaToHalfWidthKatakana', () => {
    test('基本的なひらがなを半角カタカナに変換する', () => {
      const input = 'あいうえお';
      const expected = 'ｱｲｳｴｵ';
      const result = StringEditUtils.replaceFullWidthHiraganaToHalfWidthKatakana(input);
      expect(result).toBe(expected);
    });

    test('濁点付きひらがなを半角カタカナに変換する', () => {
      const input = 'がぎぐげご';
      const expected = 'ｶﾞｷﾞｸﾞｹﾞｺﾞ';
      const result = StringEditUtils.replaceFullWidthHiraganaToHalfWidthKatakana(input);
      expect(result).toBe(expected);
    });

    test('半濁点付きひらがなを半角カタカナに変換する', () => {
      const input = 'ぱぴぷぺぽ';
      const expected = 'ﾊﾟﾋﾟﾌﾟﾍﾟﾎﾟ';
      const result = StringEditUtils.replaceFullWidthHiraganaToHalfWidthKatakana(input);
      expect(result).toBe(expected);
    });

    test('小文字のひらがなを半角カタカナに変換する', () => {
      const input = 'ぁぃぅぇぉっゃゅょ';
      const expected = 'ｧｨｩｪｫｯｬｭｮ';
      const result = StringEditUtils.replaceFullWidthHiraganaToHalfWidthKatakana(input);
      expect(result).toBe(expected);
    });

    test('記号と混在したひらがなを変換する', () => {
      const input = 'あ・い　う－え';
      // ー（長音符 U+30FC）はマップにあるが、－（全角ハイフン U+FF0D）はマップにないため変換されない
      const expected = 'ｱ･ｲ ｳ－ｴ';
      const result = StringEditUtils.replaceFullWidthHiraganaToHalfWidthKatakana(input);
      expect(result).toBe(expected);
    });

    test('変換対象外の文字は変換しない', () => {
      const input = 'あいう123ABC漢字';
      const expected = 'ｱｲｳ123ABC漢字';
      const result = StringEditUtils.replaceFullWidthHiraganaToHalfWidthKatakana(input);
      expect(result).toBe(expected);
    });

    test('空文字列を処理する', () => {
      const input = '';
      const expected = '';
      const result = StringEditUtils.replaceFullWidthHiraganaToHalfWidthKatakana(input);
      expect(result).toBe(expected);
    });
  });

  describe('replaceHalfWidthKatakanaToFullWidthKatakana', () => {
    test('基本的な半角カタカナを全角カタカナに変換する', () => {
      const input = 'ｱｲｳｴｵ';
      const expected = 'アイウエオ';
      const result = StringEditUtils.replaceHalfWidthKatakanaToFullWidthKatakana(input);
      expect(result).toBe(expected);
    });

    test('濁点付き半角カタカナを変換する', () => {
      const input = 'ｶﾞｷﾞｸﾞｹﾞｺﾞ';
      const expected = 'ガギグゲゴ';
      const result = StringEditUtils.replaceHalfWidthKatakanaToFullWidthKatakana(input);
      expect(result).toBe(expected);
    });

    test('半濁点付き半角カタカナを変換する', () => {
      const input = 'ﾊﾟﾋﾟﾌﾟﾍﾟﾎﾟ';
      const expected = 'パピプペポ';
      const result = StringEditUtils.replaceHalfWidthKatakanaToFullWidthKatakana(input);
      expect(result).toBe(expected);
    });

    test('小文字の半角カタカナを変換する', () => {
      const input = 'ｧｨｩｪｫｯｬｭｮ';
      const expected = 'ァィゥェォッャュョ';
      const result = StringEditUtils.replaceHalfWidthKatakanaToFullWidthKatakana(input);
      expect(result).toBe(expected);
    });

    test('変換対象外の文字は変換しない', () => {
      const input = 'ｱｲｳ123ABCあい漢字';
      const expected = 'アイウ123ABCあい漢字';
      const result = StringEditUtils.replaceHalfWidthKatakanaToFullWidthKatakana(input);
      expect(result).toBe(expected);
    });
  });

  describe('replaceFullWidthKatakanaToHalfWidthKatakana', () => {
    test('全角カタカナを半角カタカナに変換する', () => {
      const input = 'アイウエオ';
      const expected = 'ｱｲｳｴｵ';
      const result = StringEditUtils.replaceFullWidthKatakanaToHalfWidthKatakana(input);
      expect(result).toBe(expected);
    });

    test('濁点付き全角カタカナを変換する', () => {
      const input = 'ガギグゲゴ';
      const expected = 'ｶﾞｷﾞｸﾞｹﾞｺﾞ';
      const result = StringEditUtils.replaceFullWidthKatakanaToHalfWidthKatakana(input);
      expect(result).toBe(expected);
    });

    test('ヴを半角カタカナに変換する', () => {
      const input = 'ヴ';
      const expected = 'ｳﾞ';
      const result = StringEditUtils.replaceFullWidthKatakanaToHalfWidthKatakana(input);
      expect(result).toBe(expected);
    });
  });

  describe('replaceFullWidthSymbolsToHalfWidth', () => {
    test('基本的な全角記号を半角記号に変換する', () => {
      const input = '！＂＃＄％';
      const expected = '!"#$%';
      const result = StringEditUtils.replaceFullWidthSymbolsToHalfWidth(input);
      expect(result).toBe(expected);
    });

    test('括弧と演算記号を変換する', () => {
      const input = '（）［］｛｝';
      const expected = '()[]{}';
      const result = StringEditUtils.replaceFullWidthSymbolsToHalfWidth(input);
      expect(result).toBe(expected);
    });

    test('よく使用される記号を変換する', () => {
      const input = '：；，．－';
      const expected = ':;,.-';
      const result = StringEditUtils.replaceFullWidthSymbolsToHalfWidth(input);
      expect(result).toBe(expected);
    });

    test('変換対象外の文字は変換しない', () => {
      const input = '！あいう123アイウ漢字';
      const expected = '!あいう123アイウ漢字';
      const result = StringEditUtils.replaceFullWidthSymbolsToHalfWidth(input);
      expect(result).toBe(expected);
    });

    test('空文字列を処理する', () => {
      const input = '';
      const expected = '';
      const result = StringEditUtils.replaceFullWidthSymbolsToHalfWidth(input);
      expect(result).toBe(expected);
    });
  });

  describe('replaceHalfWidthSymbolsToFullWidth', () => {
    test('基本的な半角記号を全角記号に変換する', () => {
      const input = '!"#$%';
      const expected = '！＂＃＄％';
      const result = StringEditUtils.replaceHalfWidthSymbolsToFullWidth(input);
      expect(result).toBe(expected);
    });

    test('括弧と演算記号を変換する', () => {
      const input = '()[]{}';
      const expected = '（）［］｛｝';
      const result = StringEditUtils.replaceHalfWidthSymbolsToFullWidth(input);
      expect(result).toBe(expected);
    });

    test('よく使用される記号を変換する', () => {
      const input = ':;,.-';
      const expected = '：；，．－';
      const result = StringEditUtils.replaceHalfWidthSymbolsToFullWidth(input);
      expect(result).toBe(expected);
    });

    test('変換対象外の文字は変換しない', () => {
      const input = '!あいう123アイウ漢字';
      const expected = '！あいう123アイウ漢字';
      const result = StringEditUtils.replaceHalfWidthSymbolsToFullWidth(input);
      expect(result).toBe(expected);
    });

    test('空文字列を処理する', () => {
      const input = '';
      const expected = '';
      const result = StringEditUtils.replaceHalfWidthSymbolsToFullWidth(input);
      expect(result).toBe(expected);
    });
  });

  describe('replaceFullWidthCharToHalfWidthChar', () => {
    test('全角ひらがなと記号を半角カタカナと半角記号に変換する', () => {
      const input = 'あいう！＃';
      const expected = 'ｱｲｳ!#';
      const result = StringEditUtils.replaceFullWidthCharToHalfWidthChar(input);
      expect(result).toBe(expected);
    });

    test('複合的な文字列を変換する', () => {
      const input = 'こんにちは！（テスト）';
      const expected = 'ｺﾝﾆﾁﾊ!(ﾃｽﾄ)';
      const result = StringEditUtils.replaceFullWidthCharToHalfWidthChar(input);
      expect(result).toBe(expected);
    });

    test('濁点・半濁点付きの文字を含む変換', () => {
      const input = 'がんばって！';
      const expected = 'ｶﾞﾝﾊﾞｯﾃ!';
      const result = StringEditUtils.replaceFullWidthCharToHalfWidthChar(input);
      expect(result).toBe(expected);
    });

    test('変換対象外の文字は変換しない', () => {
      const input = 'あいう123ABC漢字！';
      const expected = 'ｱｲｳ123ABC漢字!';
      const result = StringEditUtils.replaceFullWidthCharToHalfWidthChar(input);
      expect(result).toBe(expected);
    });

    test('空文字列を処理する', () => {
      const input = '';
      const expected = '';
      const result = StringEditUtils.replaceFullWidthCharToHalfWidthChar(input);
      expect(result).toBe(expected);
    });

    test('記号のみの変換', () => {
      const input = '！＃＄％＆';
      const expected = '!#$%&';
      const result = StringEditUtils.replaceFullWidthCharToHalfWidthChar(input);
      expect(result).toBe(expected);
    });
  });

  describe('エッジケースのテスト', () => {
    test('非常に長い文字列の処理', () => {
      const longInput = 'あ'.repeat(1000) + '！'.repeat(1000);
      const expectedOutput = 'ｱ'.repeat(1000) + '!'.repeat(1000);
      const result = StringEditUtils.replaceFullWidthCharToHalfWidthChar(longInput);
      expect(result).toBe(expectedOutput);
      expect(result.length).toBe(2000);
    });

    test('null や undefined の場合は別途エラーハンドリングが必要', () => {
      // 現在の実装では型安全性により null/undefined は渡せない
      // 実際のプロダクションコードではエラーハンドリングを検討する必要がある
    });

    test('一文字のみの変換', () => {
      expect(StringEditUtils.replaceFullWidthHiraganaToHalfWidthKatakana('あ')).toBe('ｱ');
      expect(StringEditUtils.replaceFullWidthSymbolsToHalfWidth('！')).toBe('!');
    });
  });
});