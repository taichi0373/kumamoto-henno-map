import { ValidateUtils } from './validateUtils';

/**
 * ValidateUtilsのテストクラス
 */
describe('ValidateUtils', () => {

  // ----------------------------------------------------------------
  // isNullOrEmpty
  // ----------------------------------------------------------------
  describe('isNullOrEmpty', () => {

    test('nullはtrueを返す', () => {
      expect(ValidateUtils.isNullOrEmpty(null)).toBe(true);
    });

    test('undefinedはtrueを返す', () => {
      expect(ValidateUtils.isNullOrEmpty(undefined)).toBe(true);
    });

    test('空文字はtrueを返す', () => {
      expect(ValidateUtils.isNullOrEmpty('')).toBe(true);
    });

    test('スペースのみの文字列はtrueを返す', () => {
      expect(ValidateUtils.isNullOrEmpty('   ')).toBe(true);
    });

    test('空配列はtrueを返す', () => {
      expect(ValidateUtils.isNullOrEmpty([])).toBe(true);
    });

    test('空オブジェクトはtrueを返す', () => {
      expect(ValidateUtils.isNullOrEmpty({})).toBe(true);
    });

    test('値のある文字列はfalseを返す', () => {
      expect(ValidateUtils.isNullOrEmpty('hello')).toBe(false);
    });

    test('値のある配列はfalseを返す', () => {
      expect(ValidateUtils.isNullOrEmpty([1, 2, 3])).toBe(false);
    });

    test('値のあるオブジェクトはfalseを返す', () => {
      expect(ValidateUtils.isNullOrEmpty({ key: 'value' })).toBe(false);
    });

    test('数値はfalseを返す', () => {
      expect(ValidateUtils.isNullOrEmpty(0)).toBe(false);
    });

    test('真偽値はfalseを返す', () => {
      expect(ValidateUtils.isNullOrEmpty(false)).toBe(false);
    });
  });

  // ----------------------------------------------------------------
  // isHalfWidthChar
  // ----------------------------------------------------------------
  describe('isHalfWidthChar', () => {

    test('半角英小文字はtrueを返す', () => {
      expect(ValidateUtils.isHalfWidthChar('abc')).toBe(true);
    });

    test('半角英大文字はtrueを返す', () => {
      expect(ValidateUtils.isHalfWidthChar('ABC')).toBe(true);
    });

    test('半角数字はtrueを返す', () => {
      expect(ValidateUtils.isHalfWidthChar('123')).toBe(true);
    });

    test('半角記号はtrueを返す', () => {
      expect(ValidateUtils.isHalfWidthChar('!@#')).toBe(true);
    });

    test('全角ひらがなはfalseを返す', () => {
      expect(ValidateUtils.isHalfWidthChar('あ')).toBe(false);
    });

    test('全角英字はfalseを返す', () => {
      expect(ValidateUtils.isHalfWidthChar('Ａ')).toBe(false);
    });

    test('空文字はfalseを返す', () => {
      expect(ValidateUtils.isHalfWidthChar('')).toBe(false);
    });
  });

  // ----------------------------------------------------------------
  // isFullWidthChar
  // ----------------------------------------------------------------
  describe('isFullWidthChar', () => {

    test('全角ひらがなはtrueを返す', () => {
      expect(ValidateUtils.isFullWidthChar('あいう')).toBe(true);
    });

    test('漢字はtrueを返す', () => {
      expect(ValidateUtils.isFullWidthChar('漢字')).toBe(true);
    });

    test('全角カタカナはtrueを返す', () => {
      expect(ValidateUtils.isFullWidthChar('アイウ')).toBe(true);
    });

    test('全角アルファベットはtrueを返す', () => {
      expect(ValidateUtils.isFullWidthChar('ＡＢＣ')).toBe(true);
    });

    test('半角英字はfalseを返す', () => {
      expect(ValidateUtils.isFullWidthChar('abc')).toBe(false);
    });

    test('半角カタカナはfalseを返す', () => {
      expect(ValidateUtils.isFullWidthChar('ｱｲｳ')).toBe(false);
    });

    test('空文字はfalseを返す', () => {
      expect(ValidateUtils.isFullWidthChar('')).toBe(false);
    });
  });

  // ----------------------------------------------------------------
  // isHalfWidthNumber
  // ----------------------------------------------------------------
  describe('isHalfWidthNumber', () => {

    test('半角数字はtrueを返す', () => {
      expect(ValidateUtils.isHalfWidthNumber('123')).toBe(true);
    });

    test('半角アルファベットはfalseを返す', () => {
      expect(ValidateUtils.isHalfWidthNumber('abc')).toBe(false);
    });

    test('全角数字はfalseを返す', () => {
      expect(ValidateUtils.isHalfWidthNumber('１２３')).toBe(false);
    });

    test('数字と英字の混在はfalseを返す', () => {
      expect(ValidateUtils.isHalfWidthNumber('12a')).toBe(false);
    });

    test('空文字はfalseを返す', () => {
      expect(ValidateUtils.isHalfWidthNumber('')).toBe(false);
    });
  });

  // ----------------------------------------------------------------
  // isFullWidthNumber
  // ----------------------------------------------------------------
  describe('isFullWidthNumber', () => {

    test('全角数字はtrueを返す', () => {
      expect(ValidateUtils.isFullWidthNumber('１２３')).toBe(true);
    });

    test('半角数字はfalseを返す', () => {
      expect(ValidateUtils.isFullWidthNumber('123')).toBe(false);
    });

    test('全角数字と半角数字の混在はfalseを返す', () => {
      expect(ValidateUtils.isFullWidthNumber('１2３')).toBe(false);
    });

    test('空文字はfalseを返す', () => {
      expect(ValidateUtils.isFullWidthNumber('')).toBe(false);
    });
  });

  // ----------------------------------------------------------------
  // isHalfWidthAlphabet
  // ----------------------------------------------------------------
  describe('isHalfWidthAlphabet', () => {

    test('半角小文字アルファベットはtrueを返す', () => {
      expect(ValidateUtils.isHalfWidthAlphabet('abc')).toBe(true);
    });

    test('半角大文字アルファベットはtrueを返す', () => {
      expect(ValidateUtils.isHalfWidthAlphabet('ABC')).toBe(true);
    });

    test('半角数字はfalseを返す', () => {
      expect(ValidateUtils.isHalfWidthAlphabet('123')).toBe(false);
    });

    test('全角アルファベットはfalseを返す', () => {
      expect(ValidateUtils.isHalfWidthAlphabet('ＡＢＣ')).toBe(false);
    });

    test('英字と数字の混在はfalseを返す', () => {
      expect(ValidateUtils.isHalfWidthAlphabet('abc1')).toBe(false);
    });

    test('空文字はfalseを返す', () => {
      expect(ValidateUtils.isHalfWidthAlphabet('')).toBe(false);
    });
  });

  // ----------------------------------------------------------------
  // isFullWidthAlphabet
  // ----------------------------------------------------------------
  describe('isFullWidthAlphabet', () => {

    test('全角大文字アルファベットはtrueを返す', () => {
      expect(ValidateUtils.isFullWidthAlphabet('ＡＢＣ')).toBe(true);
    });

    test('全角小文字アルファベットはtrueを返す', () => {
      expect(ValidateUtils.isFullWidthAlphabet('ａｂｃ')).toBe(true);
    });

    test('半角アルファベットはfalseを返す', () => {
      expect(ValidateUtils.isFullWidthAlphabet('abc')).toBe(false);
    });

    test('全角数字はfalseを返す', () => {
      expect(ValidateUtils.isFullWidthAlphabet('１２３')).toBe(false);
    });

    test('空文字はfalseを返す', () => {
      expect(ValidateUtils.isFullWidthAlphabet('')).toBe(false);
    });
  });

  // ----------------------------------------------------------------
  // isPhoneNumber
  // ----------------------------------------------------------------
  describe('isPhoneNumber', () => {

    test('ハイフンあり固定電話形式はtrueを返す', () => {
      expect(ValidateUtils.isPhoneNumber('03-1234-5678')).toBe(true);
    });

    test('ハイフンあり携帯電話形式はtrueを返す', () => {
      expect(ValidateUtils.isPhoneNumber('090-1234-5678')).toBe(true);
    });

    test('ハイフンなし11桁はtrueを返す', () => {
      expect(ValidateUtils.isPhoneNumber('09012345678')).toBe(true);
    });

    test('ハイフンなし10桁はtrueを返す', () => {
      expect(ValidateUtils.isPhoneNumber('0901234567')).toBe(true);
    });

    test('0以外で始まる場合はfalseを返す', () => {
      expect(ValidateUtils.isPhoneNumber('1234567890')).toBe(false);
    });

    test('末尾が3桁の場合はfalseを返す', () => {
      expect(ValidateUtils.isPhoneNumber('090-1234-567')).toBe(false);
    });

    test('空文字はfalseを返す', () => {
      expect(ValidateUtils.isPhoneNumber('')).toBe(false);
    });
  });

  // ----------------------------------------------------------------
  // isEmail
  // ----------------------------------------------------------------
  describe('isEmail', () => {

    test('有効なメールアドレスはtrueを返す', () => {
      expect(ValidateUtils.isEmail('test@example.com')).toBe(true);
    });

    test('サブドメインを含むメールアドレスはtrueを返す', () => {
      expect(ValidateUtils.isEmail('user.name@sub.example.co.jp')).toBe(true);
    });

    test('@がない場合はfalseを返す', () => {
      expect(ValidateUtils.isEmail('invalid')).toBe(false);
    });

    test('ドメインがない場合はfalseを返す', () => {
      expect(ValidateUtils.isEmail('invalid@')).toBe(false);
    });

    test('TLDがない場合はfalseを返す', () => {
      expect(ValidateUtils.isEmail('user@example')).toBe(false);
    });

    test('空文字はfalseを返す', () => {
      expect(ValidateUtils.isEmail('')).toBe(false);
    });
  });

  // ----------------------------------------------------------------
  // isUsername
  // ----------------------------------------------------------------
  describe('isUsername', () => {

    /** 正常系 */
    test('半角英小文字のみの場合はtrueを返す', () => {
      expect(ValidateUtils.isUsername('abc')).toBe(true);
    });

    test('半角英大文字のみの場合はtrueを返す', () => {
      expect(ValidateUtils.isUsername('ABC')).toBe(true);
    });

    test('半角数字のみの場合はtrueを返す', () => {
      expect(ValidateUtils.isUsername('123')).toBe(true);
    });

    test('ハイフンを含む場合はtrueを返す', () => {
      expect(ValidateUtils.isUsername('user-name')).toBe(true);
    });

    test('アンダースコアを含む場合はtrueを返す', () => {
      expect(ValidateUtils.isUsername('user_name')).toBe(true);
    });

    test('英数字・ハイフン・アンダースコアの組み合わせはtrueを返す', () => {
      expect(ValidateUtils.isUsername('User_123-abc')).toBe(true);
    });

    test('最小文字数（3文字）の場合はtrueを返す', () => {
      expect(ValidateUtils.isUsername('a'.repeat(ValidateUtils.USERNAME_MIN_LENGTH))).toBe(true);
    });

    test('最大文字数（30文字）の場合はtrueを返す', () => {
      expect(ValidateUtils.isUsername('a'.repeat(ValidateUtils.USERNAME_MAX_LENGTH))).toBe(true);
    });

    /** 異常系 */
    test('空文字の場合はfalseを返す', () => {
      expect(ValidateUtils.isUsername('')).toBe(false);
    });

    test('スペースのみの場合はfalseを返す', () => {
      expect(ValidateUtils.isUsername('   ')).toBe(false);
    });

    test('スペースを含む場合はfalseを返す', () => {
      expect(ValidateUtils.isUsername('user name')).toBe(false);
    });

    test('@を含む場合はfalseを返す', () => {
      expect(ValidateUtils.isUsername('user@name')).toBe(false);
    });

    test('ドットを含む場合はfalseを返す', () => {
      expect(ValidateUtils.isUsername('user.name')).toBe(false);
    });

    test('感嘆符などの記号を含む場合はfalseを返す', () => {
      expect(ValidateUtils.isUsername('user!name')).toBe(false);
    });

    test('全角英数字を含む場合はfalseを返す', () => {
      expect(ValidateUtils.isUsername('ａｂｃ')).toBe(false);
    });

    test('日本語を含む場合はfalseを返す', () => {
      expect(ValidateUtils.isUsername('ユーザ名')).toBe(false);
    });
  });
});
