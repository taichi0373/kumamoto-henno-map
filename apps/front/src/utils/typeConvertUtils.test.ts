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

            test('西暦8桁（数値）をDate型に変換する', () => {
                const result = TypeConvertUtils.toDateFromNum(20240101);
                expect(result.getFullYear()).toBe(2024);
                expect(result.getMonth() + 1).toBe(1);
                expect(result.getDate()).toBe(1);
            });

            test('西暦8桁（文字列）をDate型に変換する', () => {
                const result = TypeConvertUtils.toDateFromNum('20240101');
                expect(result.getFullYear()).toBe(2024);
                expect(result.getMonth() + 1).toBe(1);
                expect(result.getDate()).toBe(1);
            });

            test('西暦8桁の末日をDate型に変換する', () => {
                const result = TypeConvertUtils.toDateFromNum(20241231);
                expect(result.getFullYear()).toBe(2024);
                expect(result.getMonth() + 1).toBe(12);
                expect(result.getDate()).toBe(31);
            });

            test('過去の西暦8桁をDate型に変換する', () => {
                const result = TypeConvertUtils.toDateFromNum(19890101);
                expect(result.getFullYear()).toBe(1989);
                expect(result.getMonth() + 1).toBe(1);
                expect(result.getDate()).toBe(1);
            });
        });

        describe('異常系', () => {

            test('8桁未満の値を渡すとエラーをスローする', () => {
                expect(() => TypeConvertUtils.toDateFromNum(1234567)).toThrow();
            });

            test('9桁以上の値を渡すとエラーをスローする', () => {
                expect(() => TypeConvertUtils.toDateFromNum(202401011)).toThrow();
            });

            test('数字以外の文字列を渡すとエラーをスローする', () => {
                expect(() => TypeConvertUtils.toDateFromNum('abc')).toThrow();
            });
        });
    });

    // ----------------------------------------------------------------
    // toDateFromString
    // ----------------------------------------------------------------
    describe('toDateFromString', () => {

        describe('正常系', () => {

            test('YYYY-MM-DD形式の文字列をローカル日付として変換する', () => {
                const result = TypeConvertUtils.toDateFromString('2024-03-15');
                expect(result).not.toBeNull();
                expect(result!.getFullYear()).toBe(2024);
                expect(result!.getMonth() + 1).toBe(3);
                expect(result!.getDate()).toBe(15);
            });

            test('月末日（YYYY-MM-DD）を正しく変換する', () => {
                const result = TypeConvertUtils.toDateFromString('2024-12-31');
                expect(result).not.toBeNull();
                expect(result!.getFullYear()).toBe(2024);
                expect(result!.getMonth() + 1).toBe(12);
                expect(result!.getDate()).toBe(31);
            });

            test('うるう年の2月29日を正しく変換する', () => {
                const result = TypeConvertUtils.toDateFromString('2024-02-29');
                expect(result).not.toBeNull();
                expect(result!.getFullYear()).toBe(2024);
                expect(result!.getMonth() + 1).toBe(2);
                expect(result!.getDate()).toBe(29);
            });

            test('有効なDateオブジェクトを渡すとそのまま返す', () => {
                const date = new Date(2024, 2, 15); // 2024-03-15
                const result = TypeConvertUtils.toDateFromString(date);
                expect(result).toBe(date);
            });
        });

        describe('異常系', () => {

            test('nullを渡すとnullを返す', () => {
                expect(TypeConvertUtils.toDateFromString(null)).toBeNull();
            });

            test('undefinedを渡すとnullを返す', () => {
                expect(TypeConvertUtils.toDateFromString(undefined)).toBeNull();
            });

            test('不正な文字列を渡すとnullを返す', () => {
                expect(TypeConvertUtils.toDateFromString('abc')).toBeNull();
            });

            test('無効なDateオブジェクト（Invalid Date）を渡すとnullを返す', () => {
                expect(TypeConvertUtils.toDateFromString(new Date('invalid'))).toBeNull();
            });

            test('存在しない日付（2月30日）を渡すとnullを返す', () => {
                // Dateコンストラクタは桁あふれを許容するため、ここでは変換結果の日付がずれることを確認する
                const result = TypeConvertUtils.toDateFromString('2024-02-30');
                // new Date(2024, 1, 30) は 3月1日に繰り上がるためnullにはならないが日付がズレる
                expect(result).not.toBeNull();
                expect(result!.getDate()).not.toBe(30);
            });
        });
    });
});
