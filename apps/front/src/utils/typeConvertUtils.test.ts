import { TypeConvertUtils } from './typeConvertUtils';

/**
 * TypeConvertUtilsのテストクラス
 */
describe('TypeConvertUtils', () => {

    // ----------------------------------------------------------------
    // toNumberNullorEmptyToZero
    // ----------------------------------------------------------------
    describe('toNumberNullorEmptyToZero', () => {

        test('nullを渡すと0を返す', () => {
            expect(TypeConvertUtils.toNumberNullorEmptyToZero(null)).toBe(0);
        });

        test('undefinedを渡すと0を返す', () => {
            expect(TypeConvertUtils.toNumberNullorEmptyToZero(undefined)).toBe(0);
        });

        test('空文字を渡すと0を返す', () => {
            // Number("") === 0 のため0が返る
            expect(TypeConvertUtils.toNumberNullorEmptyToZero('')).toBe(0);
        });

        test('数値文字列を渡すと数値に変換して返す', () => {
            expect(TypeConvertUtils.toNumberNullorEmptyToZero('123')).toBe(123);
        });

        test('数値を渡すとそのまま返す', () => {
            expect(TypeConvertUtils.toNumberNullorEmptyToZero(456)).toBe(456);
        });

        test('0を渡すと0を返す', () => {
            expect(TypeConvertUtils.toNumberNullorEmptyToZero(0)).toBe(0);
        });

        test('小数文字列を渡すと小数に変換して返す', () => {
            expect(TypeConvertUtils.toNumberNullorEmptyToZero('3.14')).toBeCloseTo(3.14);
        });

        test('数値に変換できない文字列を渡すとNaNを返す', () => {
            expect(TypeConvertUtils.toNumberNullorEmptyToZero('abc')).toBeNaN();
        });
    });

    // ----------------------------------------------------------------
    // toNumberNullToZero
    // ----------------------------------------------------------------
    describe('toNumberNullToZero', () => {

        test('nullを渡すと0を返す', () => {
            expect(TypeConvertUtils.toNumberNullToZero(null)).toBe(0);
        });

        test('undefinedを渡すと0を返す', () => {
            expect(TypeConvertUtils.toNumberNullToZero(undefined)).toBe(0);
        });

        test('空文字を渡すとNaNを返す', () => {
            expect(TypeConvertUtils.toNumberNullToZero('')).toBeNaN();
        });

        test('数値文字列を渡すと数値に変換して返す', () => {
            expect(TypeConvertUtils.toNumberNullToZero('123')).toBe(123);
        });

        test('数値を渡すとそのまま返す', () => {
            expect(TypeConvertUtils.toNumberNullToZero(456)).toBe(456);
        });

        test('0を渡すと0を返す', () => {
            expect(TypeConvertUtils.toNumberNullToZero(0)).toBe(0);
        });

        test('数値に変換できない文字列を渡すとNaNを返す', () => {
            expect(TypeConvertUtils.toNumberNullToZero('abc')).toBeNaN();
        });
    });

    // ----------------------------------------------------------------
    // toStringNullToEmptyToZero
    // ----------------------------------------------------------------
    describe('toStringNullToEmptyToZero', () => {

        test('nullを渡すと"0"を返す', () => {
            expect(TypeConvertUtils.toStringNullToEmptyToZero(null)).toBe('0');
        });

        test('undefinedを渡すと"0"を返す', () => {
            expect(TypeConvertUtils.toStringNullToEmptyToZero(undefined)).toBe('0');
        });

        test('NaNを渡すと"0"を返す', () => {
            expect(TypeConvertUtils.toStringNullToEmptyToZero(NaN)).toBe('0');
        });

        test('空文字を渡すと"0"を返す', () => {
            expect(TypeConvertUtils.toStringNullToEmptyToZero('')).toBe('0');
        });

        test('数値を渡すと文字列に変換して返す', () => {
            expect(TypeConvertUtils.toStringNullToEmptyToZero(123)).toBe('123');
        });

        test('数値文字列を渡すとそのまま返す', () => {
            expect(TypeConvertUtils.toStringNullToEmptyToZero('456')).toBe('456');
        });

        test('0を渡すと"0"を返す', () => {
            expect(TypeConvertUtils.toStringNullToEmptyToZero(0)).toBe('0');
        });

        test('文字列を渡すとそのまま返す', () => {
            expect(TypeConvertUtils.toStringNullToEmptyToZero('abc')).toBe('abc');
        });
    });

    // ----------------------------------------------------------------
    // toStringNullToEmpty
    // ----------------------------------------------------------------
    describe('toStringNullToEmpty', () => {

        test('nullを渡すと空文字を返す', () => {
            expect(TypeConvertUtils.toStringNullToEmpty(null)).toBe('');
        });

        test('undefinedを渡すと空文字を返す', () => {
            expect(TypeConvertUtils.toStringNullToEmpty(undefined)).toBe('');
        });

        test('NaNを渡すと空文字を返す', () => {
            expect(TypeConvertUtils.toStringNullToEmpty(NaN)).toBe('');
        });

        test('空文字を渡すと空文字を返す', () => {
            // null/undefinedでも NaN でもないため、String("")のまま返る
            expect(TypeConvertUtils.toStringNullToEmpty('')).toBe('');
        });

        test('数値を渡すと文字列に変換して返す', () => {
            expect(TypeConvertUtils.toStringNullToEmpty(123)).toBe('123');
        });

        test('数値文字列を渡すとそのまま返す', () => {
            expect(TypeConvertUtils.toStringNullToEmpty('456')).toBe('456');
        });

        test('0を渡すと"0"を返す', () => {
            expect(TypeConvertUtils.toStringNullToEmpty(0)).toBe('0');
        });

        test('文字列を渡すとそのまま返す', () => {
            expect(TypeConvertUtils.toStringNullToEmpty('abc')).toBe('abc');
        });
    });

    // ----------------------------------------------------------------
    // toDateFromNum
    // ----------------------------------------------------------------
    describe('toDateFromNum', () => {

        describe('正常系', () => {

            test('和暦7桁（数値）をDate型に変換する', () => {
                // 5060101 = 令和6年1月1日 = 2024/01/01
                const result = TypeConvertUtils.toDateFromNum(5060101);
                expect(result.getFullYear()).toBe(2024);
                expect(result.getMonth() + 1).toBe(1);
                expect(result.getDate()).toBe(1);
            });

            test('和暦7桁（文字列）をDate型に変換する', () => {
                // '5060101' = 令和6年1月1日 = 2024/01/01
                const result = TypeConvertUtils.toDateFromNum('5060101');
                expect(result.getFullYear()).toBe(2024);
                expect(result.getMonth() + 1).toBe(1);
                expect(result.getDate()).toBe(1);
            });

            test('和暦7桁の末日をDate型に変換する', () => {
                // 5061231 = 令和6年12月31日 = 2024/12/31
                const result = TypeConvertUtils.toDateFromNum(5061231);
                expect(result.getFullYear()).toBe(2024);
                expect(result.getMonth() + 1).toBe(12);
                expect(result.getDate()).toBe(31);
            });

            test('昭和の和暦7桁をDate型に変換する', () => {
                // 3640101 = 昭和64年1月1日 = 1989/01/01
                const result = TypeConvertUtils.toDateFromNum(3640101);
                expect(result.getFullYear()).toBe(1989);
                expect(result.getMonth() + 1).toBe(1);
                expect(result.getDate()).toBe(1);
            });
        });

        describe('異常系', () => {

            test('7桁未満の値を渡すとエラーをスローする', () => {
                expect(() => TypeConvertUtils.toDateFromNum(123456)).toThrow();
            });

            test('存在しない日付（2月30日）を渡すとエラーをスローする', () => {
                // 5060230 = 令和6年2月30日（存在しない）
                expect(() => TypeConvertUtils.toDateFromNum(5060230)).toThrow();
            });

            test('不正な元号番号を持つ和暦を渡すとエラーをスローする', () => {
                // 9010101 = 元号番号9（存在しない）
                expect(() => TypeConvertUtils.toDateFromNum(9010101)).toThrow();
            });

            test('数字以外の文字列を渡すとエラーをスローする', () => {
                expect(() => TypeConvertUtils.toDateFromNum('abc')).toThrow();
            });
        });
    });
});
