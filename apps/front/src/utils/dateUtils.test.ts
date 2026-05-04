import { DateUtils } from './dateUtils';

/**
 * DateUtilsのテストクラス
 */
describe('DateUtils', () => {

  // ----------------------------------------------------------------
  // checkDate
  // ----------------------------------------------------------------
  describe('checkDate', () => {

    describe('西暦8桁', () => {

      test('有効な西暦8桁（数値）はtrueを返す', () => {
        expect(DateUtils.checkDate(20240101)).toBe(true);
      });

      test('有効な西暦8桁（文字列）はtrueを返す', () => {
        expect(DateUtils.checkDate('20240101')).toBe(true);
      });

      test('存在しない月（13月）はfalseを返す', () => {
        expect(DateUtils.checkDate('20241301')).toBe(false);
      });

      test('存在しない日（2月30日）はfalseを返す', () => {
        expect(DateUtils.checkDate('20240230')).toBe(false);
      });
    });

    describe('和暦7桁', () => {

      test('有効な和暦7桁（令和）はtrueを返す', () => {
        // 5060101 = 令和6年1月1日 = 2024/01/01
        expect(DateUtils.checkDate('5060101')).toBe(true);
      });

      test('有効な和暦7桁（昭和）はtrueを返す', () => {
        // 3640101 = 昭和64年1月1日 = 1989/01/01
        expect(DateUtils.checkDate('3640101')).toBe(true);
      });

      test('不正な元号番号の和暦はfalseを返す', () => {
        // 元号番号9は存在しない
        expect(DateUtils.checkDate('9990101')).toBe(false);
      });
    });

    describe('異常系', () => {

      test('数字以外の文字列はfalseを返す', () => {
        expect(DateUtils.checkDate('abc')).toBe(false);
      });

      test('2桁（桁数不正・下限未満）はfalseを返す', () => {
        expect(DateUtils.checkDate('12')).toBe(false);
      });

      test('9桁（桁数不正・上限超過）はfalseを返す', () => {
        expect(DateUtils.checkDate('123456789')).toBe(false);
      });

      test('NaN（数値型）はfalseを返す', () => {
        expect(DateUtils.checkDate(NaN)).toBe(false);
      });
    });
  });

  // ----------------------------------------------------------------
  // isDateConvertChanged
  // ----------------------------------------------------------------
  describe('isDateConvertChanged', () => {

    test('正常な西暦8桁はfalseを返す（日付が変わっていない）', () => {
      expect(DateUtils.isDateConvertChanged('20240101')).toBe(false);
    });

    test('存在しない日（2月30日）はtrueを返す', () => {
      expect(DateUtils.isDateConvertChanged('20240230')).toBe(true);
    });

    test('存在しない月（13月）はtrueを返す', () => {
      expect(DateUtils.isDateConvertChanged('20241301')).toBe(true);
    });

    test('8桁以外（4桁）はtrueを返す', () => {
      expect(DateUtils.isDateConvertChanged('2024')).toBe(true);
    });

    test('数字以外の文字列はtrueを返す', () => {
      expect(DateUtils.isDateConvertChanged('abcdefgh')).toBe(true);
    });
  });

  // ----------------------------------------------------------------
  // convertWarekiDateToSeirekiDate
  // ----------------------------------------------------------------
  describe('convertWarekiDateToSeirekiDate', () => {

    describe('7桁（EYYMMDD → YYYYMMDD）', () => {

      test('令和6年1月1日を西暦に変換する', () => {
        // 5060101 → 20240101
        expect(DateUtils.convertWarekiDateToSeirekiDate('5060101')).toBe(20240101);
      });

      test('昭和64年1月1日を西暦に変換する', () => {
        // 3640101 → 19890101
        expect(DateUtils.convertWarekiDateToSeirekiDate('3640101')).toBe(19890101);
      });
    });

    describe('5桁（EYYMM → YYYYMM）', () => {

      test('令和6年1月を西暦年月に変換する', () => {
        // 50601 → 202401
        expect(DateUtils.convertWarekiDateToSeirekiDate('50601')).toBe(202401);
      });
    });

    describe('3桁（EYY → YYYY）', () => {

      test('令和6年を西暦年に変換する', () => {
        // 506 → 2024
        expect(DateUtils.convertWarekiDateToSeirekiDate('506')).toBe(2024);
      });
    });

    describe('異常系', () => {

      test('数字以外の文字列はエラーをスローする', () => {
        expect(() => DateUtils.convertWarekiDateToSeirekiDate('abc')).toThrow();
      });

      test('8桁（不正な桁数）はエラーをスローする', () => {
        expect(() => DateUtils.convertWarekiDateToSeirekiDate('12345678')).toThrow();
      });

      test('不正な元号番号（0）はエラーをスローする', () => {
        // 先頭が0の7桁
        expect(() => DateUtils.convertWarekiDateToSeirekiDate('0010101')).toThrow();
      });
    });
  });

  // ----------------------------------------------------------------
  // convertSeirekiDateToWarekiDate
  // ----------------------------------------------------------------
  describe('convertSeirekiDateToWarekiDate', () => {

    describe('8桁（YYYYMMDD → EYYMMDD）', () => {

      test('2024/01/01を和暦7桁に変換する', () => {
        // 20240101 → 5060101 (令和6年1月1日)
        expect(DateUtils.convertSeirekiDateToWarekiDate(20240101)).toBe(5060101);
      });

      test('1989/01/01を和暦7桁に変換する', () => {
        // 19890101 → 3640101 (昭和64年1月1日)
        expect(DateUtils.convertSeirekiDateToWarekiDate(19890101)).toBe(3640101);
      });
    });

    describe('6桁（YYYYMM → EYYMM）', () => {

      test('2024年01月を和暦5桁に変換する', () => {
        // 202401 → 50601 (令和6年1月)
        expect(DateUtils.convertSeirekiDateToWarekiDate(202401)).toBe(50601);
      });
    });

    describe('4桁（YYYY → EYY）', () => {

      test('2024年を和暦3桁に変換する', () => {
        // 2024 → 506 (令和6年)
        expect(DateUtils.convertSeirekiDateToWarekiDate(2024)).toBe(506);
      });
    });

    describe('Dateオブジェクト', () => {

      test('Dateオブジェクトを和暦7桁に変換する', () => {
        // new Date(2024, 0, 1) = 2024/01/01 → 5060101 (令和6年1月1日)
        expect(DateUtils.convertSeirekiDateToWarekiDate(new Date(2024, 0, 1))).toBe(5060101);
      });

      test('Invalid DateはエラーをスローするE', () => {
        expect(() => DateUtils.convertSeirekiDateToWarekiDate(new Date('invalid'))).toThrow();
      });
    });

    describe('異常系', () => {

      test('数字以外の文字列はエラーをスローする', () => {
        expect(() => DateUtils.convertSeirekiDateToWarekiDate('abc')).toThrow();
      });

      test('5桁（不正な桁数）はエラーをスローする', () => {
        expect(() => DateUtils.convertSeirekiDateToWarekiDate(12345)).toThrow();
      });
    });
  });

  // ----------------------------------------------------------------
  // toDate
  // ----------------------------------------------------------------
  describe('toDate', () => {

    test('和暦7桁をDate型に変換する（TypeConvertUtils.toDateFromNumへの委譲確認）', () => {
      // 5060101 = 令和6年1月1日 = 2024/01/01
      const result = DateUtils.toDate(5060101);
      expect(result.getFullYear()).toBe(2024);
      expect(result.getMonth() + 1).toBe(1);
      expect(result.getDate()).toBe(1);
    });

    test('西暦8桁をDate型に変換する', () => {
      const result = DateUtils.toDate(20240101);
      expect(result.getFullYear()).toBe(2024);
      expect(result.getMonth() + 1).toBe(1);
      expect(result.getDate()).toBe(1);
    });

    test('7桁未満の値はエラーをスローする', () => {
      expect(() => DateUtils.toDate(123456)).toThrow();
    });

    test('数字以外の文字列はエラーをスローする', () => {
      expect(() => DateUtils.toDate('abc')).toThrow();
    });
  });

  // ----------------------------------------------------------------
  // dateFormat
  // ----------------------------------------------------------------
  describe('dateFormat', () => {

    describe('西暦フォーマット', () => {

      test('YYYY-MM-DD形式でフォーマットする', () => {
        const date = new Date(2024, 0, 1); // 2024/01/01
        expect(DateUtils.dateFormat(date, 'YYYY-MM-DD')).toBe('2024-01-01');
      });

      test('YYYY/MM/DD形式でフォーマットする', () => {
        const date = new Date(2024, 11, 31); // 2024/12/31
        expect(DateUtils.dateFormat(date, 'YYYY/MM/DD')).toBe('2024-12-31'.replace(/-/g, '/'));
      });
    });

    describe('和暦フォーマット', () => {

      test('GGGYY-MM-DD形式でフォーマットする（令和）', () => {
        const date = new Date(2024, 0, 1); // 令和6年1月1日
        expect(DateUtils.dateFormat(date, 'GGGYY-MM-DD')).toBe('令和06-01-01');
      });

      test('GGG形式で元号名のみ取得する', () => {
        const date = new Date(2024, 0, 1);
        expect(DateUtils.dateFormat(date, 'GGG')).toBe('令和');
      });

      test('GYY形式で元号略称+元号年取得する', () => {
        const date = new Date(2024, 0, 1); // 令和6年
        expect(DateUtils.dateFormat(date, 'GYY')).toBe('R06');
      });

      test('GY形式で元号略称+元号年（パディングなし）を取得する', () => {
        const date = new Date(2024, 0, 1); // 令和6年
        expect(DateUtils.dateFormat(date, 'GY')).toBe('R6');
      });
    });

    describe('パディングモード', () => {

      test('ZEROパディングで1桁の月日をゼロ埋めする', () => {
        const date = new Date(2024, 0, 5); // 2024/01/05
        expect(DateUtils.dateFormat(date, 'YYYY-MM-DD', DateUtils.PADDING_MODE.ZERO)).toBe('2024-01-05');
      });

      test('SPACEパディングで1桁の月日をスペース埋めする', () => {
        const date = new Date(2024, 0, 5); // 2024/01/05
        expect(DateUtils.dateFormat(date, 'YYYY-MM-DD', DateUtils.PADDING_MODE.SPACE)).toBe('2024- 1- 5');
      });

      test('NONEパディングで1桁の月日はそのまま出力する', () => {
        const date = new Date(2024, 0, 5); // 2024/01/05
        expect(DateUtils.dateFormat(date, 'YYYY-MM-DD', DateUtils.PADDING_MODE.NONE)).toBe('2024-1-5');
      });
    });

    describe('全角変換', () => {

      test('全角変換ありで数字が全角になる', () => {
        const date = new Date(2024, 0, 1); // 2024/01/01
        const result = DateUtils.dateFormat(date, 'YYYY-MM-DD', DateUtils.PADDING_MODE.ZERO, true);
        expect(result).toBe('２０２４-０１-０１');
      });

      test('全角変換なしで数字は半角のまま', () => {
        const date = new Date(2024, 0, 1);
        const result = DateUtils.dateFormat(date, 'YYYY-MM-DD', DateUtils.PADDING_MODE.ZERO, false);
        expect(result).toBe('2024-01-01');
      });
    });

    describe('異常系', () => {

      test('nullを渡すと空文字を返す', () => {
        expect(DateUtils.dateFormat(null, 'YYYY-MM-DD')).toBe('');
      });

      test('Invalid Dateを渡すと空文字を返す', () => {
        expect(DateUtils.dateFormat(new Date('invalid'), 'YYYY-MM-DD')).toBe('');
      });
    });
  });
});
