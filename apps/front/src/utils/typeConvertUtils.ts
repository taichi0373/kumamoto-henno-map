import { DateUtils } from '@/utils/dateUtils';

/**
 * 型変換ユーティリティクラス
 */
export class TypeConvertUtils {
    /**
     * 西暦8桁、和暦7桁mp数値または文字列をDate型に変換する
    * @param data 西暦8桁または和暦7桁の数値または文字列
    * @returns 変換したDateオブジェクト
    * @throws 引数の日付が不正な場合エラーをスローする
    */
    static toDateFromNum(data: string | number): Date {
        if (!DateUtils.checkDate(data) || String(data).length < 7) {
            throw new Error("引数に渡された値が西暦8桁、和暦7桁ではありません。");
        }
        let strDate = String(data);
        // 和暦→西暦
        if (strDate.length === 7) {
            strDate = DateUtils.convertWarekiDateToSeirekiDate(strDate).toString();
        }
        // 年月日を分割（YYYYMMDD形式）
        if (strDate.length !== 8) {
            throw new Error("引数に渡された値が正しい日付形式ではありません。");
        }

        const year = parseInt(strDate.substring(0, 4));
        const month = parseInt(strDate.substring(4, 6));
        const day = parseInt(strDate.substring(6, 8));
        return new Date(year, month - 1, day);
    }

    /**
     * 対象を数値に変換する
     * 引数が `null` または `undefined` の場合は0を返す。
    * @param data data 数値へ変換する対象
    * @returns 変換した数値
    */
    public static toNumberNullorEmptyToZero(data: string | number | null | undefined): number {
        if (data == null) return 0;
        return Number(data);
    }

    /**
     * 対象を数値に変換する
     * 引数が `null` または `undefined` の場合は0を返す。
     * 引数が空文字の場合はNaNを返す。
    * @param data data 数値へ変換する対象
    * @returns 変換した数値
    */
    public static toNumberNullToZero(data: string | number | null | undefined): number {
        if (data == null) return 0;
        if (data === "") return NaN;
        return Number(data);
    }

    /**
     * 対象を文字列に変換する
     * 引数が `null` または `undefined` の場合は0を返す。
    * @param data data 文字列へ変換する対象
    * @returns 変換した文字列
    */
    public static toStringNullToEmptyToZero(data: string | number | null | undefined): string {
        const isNullorUndefined = data == null;
        const isNan = Number.isNaN(data);
        const isEmpty = data === "";
        if (isNullorUndefined || isNan || isEmpty) return "0";
        return String(data);
    }

    /**
     * 対象を文字列に変換する
     * 引数が `null` または `undefined` の場合は空文字を返す。
    * @param data data 文字列へ変換する対象
    * @returns 変換した文字列
    */
    public static toStringNullToEmpty(data: string | number | null | undefined): string {
        const isNullorUndefined = data == null;
        const isNan = Number.isNaN(data);
        if (isNullorUndefined || isNan) return "";
        return String(data);
    }


    /**
     * 対象を文字列に変換する
     * 引数が `null` または `undefined` の場合は空文字を返す。
     * 引数が数値の0の場合は空文字を返す。
     * 引数が空文字の場合は空文字を返す。
    * @param data data 文字列へ変換する対象
    * @returns 変換した文字列
     */
    public static toStringNullOrZeroToEmpty(data: string | number | null | undefined): string {
        const isNullorUndefined = data == null;
        const isNan = Number.isNaN(data);
        const isEmpty = data === "";
        const isZero = data === 0 || data === "0";
        if (isNullorUndefined || isNan || isEmpty || isZero) return "";
        return String(data);
    }

    /**
     * Date型をYYYY-MM-DD形式の文字列に変換する
     * @param date Date型
     * @returns YYYY-MM-DD形式の文字列（無効な場合はnull）
     */
    public static toStringFromDate(date: Date | null | undefined): string | null {
        if (!(date instanceof Date) || isNaN(date.getTime())) return null;
        const year = date.getFullYear();
        const month = (date.getMonth() + 1).toString().padStart(2, '0');
        const day = date.getDate().toString().padStart(2, '0');
        return `${year}-${month}-${day}`;
    }
    
}
