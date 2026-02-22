import { ValidateUtils } from "./validateUtils";
import { TypeConvertUtils } from "./typeConvertUtils";

/**
 * 日付ユーティリティクラス
 * 元号名・元号年の取得には Intl.DateTimeFormat (japanese カレンダー) を使用する
 */
class DateUtils {
    /** 
     * 元号マスタ
     * */
    public static readonly GENGO_MASTER = [
        { name: '明治', abbr: 'M', startYear: 1868, endYear: 1912, kijunBi: new Date(1868, 0, 25) },
        { name: '大正', abbr: 'T', startYear: 1912, endYear: 1926, kijunBi: new Date(1912, 6, 30) },
        { name: '昭和', abbr: 'S', startYear: 1926, endYear: 1989, kijunBi: new Date(1926, 11, 25) },
        { name: '平成', abbr: 'H', startYear: 1989, endYear: 2019, kijunBi: new Date(1989, 0, 8) },
        { name: '令和', abbr: 'R', startYear: 2019, endYear: 9999, kijunBi: new Date(2019, 4, 1) },
    ] as const;

    /** 
     * 日付フォーマットテンプレート
     * */
    public static readonly DATE_FORMAT_TEMPLATE = {
        SEIREKI_PAUSE_HYPHEN: 'YYYY-MM-DD',
        SEIREKI_PAUSE_SLASH: 'YYYY/MM/DD',
        WAREKI_PAUSE_HYPHEN: 'GGGYY-MM-DD',
        WAREKI_PAUSE_SLASH: 'GGGYY/MM/DD',
    } as const;

    /** 
     * パディングモード
     *  @propam SPACE スペースパディング
     *  @propam ZERO  ゼロパディング
     *  @propam NONE  パディングなし
     * */
    static readonly PADDING_MODE = {
        SPACE: 0,
        ZERO: 1,
        NONE: 2,
    } as const;


    /** 
     * 桁数定数
     * @property WAREKI_DATE      和暦日付: 7桁
     * @property WAREKI_MONTH     和暦年月: 5桁
     * @property WAREKI_YEAR      和暦年: 3桁
     * @property SEIREKI_DATE     西暦日付: 8桁
     * @property SEIREKI_MONTH    西暦年月: 6桁
     * @property SEIREKI_YEAR     西暦年: 4桁
     * @property MONTH_DAY        月日: 4桁
     * @property DAY_OF_MONTH     日: 2桁
     * */
    public static readonly digitMode = {
        WAREKI_DATE: 7,
        WAREKI_MONTH: 5,
        WAREKI_YEAR: 3,
        SEIREKI_DATE: 8,
        SEIREKI_MONTH: 6,
        SEIREKI_YEAR: 4,
        MONTH_DAY: 4,
        DAY_OF_MONTH: 2,
    } as const;

    /** 
     * 西暦・和暦変換メソッド用のプライベート定数
      * @property FIRST_OF_MONTH 月初
      * @property END_OF_MONTH 月末
     * */
    public static readonly supplementMode = {
        FIRST_OF_MONTH: "01",
        END_OF_MONTH: "99",
    } as const;

    public static checkDate(dt: number | string): boolean {
        // 半角数値以外 or Nanの場合、false
        if (typeof dt === 'string') {
            if (!ValidateUtils.isHalfWidthNumber(dt)) return false;
        } else if (isNaN(dt)) {
            return false;
        }
        let strDate = String(dt);
        // 桁数が不正の場合、false
        if (strDate.length < 3 || strDate.length > 8) return false;
        if (strDate.length % 2 === 1) {
            // 和暦の場合
            try {
                // 和暦を西暦に変換
                strDate = DateUtils.convertWarekiDateToSeirekiDate(strDate).toString();
            } catch (e) {
                return false;
            }
        } else {
            // 西暦の場合
            // 桁数補正
            switch (strDate.length) {
                case 6:
                    strDate = strDate + '01';
                    break;
                case 4:
                    strDate = strDate + '0101';
                    break;
            }
        }

        if (DateUtils.isDateConvertChanged(strDate)) return false;

        return true;
    }

    /**
     * 西暦日付8桁をDate型に変換して、日付の情報が変わっているかチェック
     * @param dt 日付文字列 (YYYYMMDD)
     * @returns true: 変わっている, false: 変わっていない
     */
    public static isDateConvertChanged(dt: string | number): boolean {
        const dateStr = dt.toString();
        if (!ValidateUtils.isHalfWidthNumber(dateStr) || dateStr.length !== 8) {
            return true;
        }
        // 年月日を分割（YYYYMMDD形式）
        const year = parseInt(dateStr.substring(0, 4));
        const month = parseInt(dateStr.substring(4, 6));
        const day = parseInt(dateStr.substring(6, 8));
        const date = new Date(year, month - 1, day);

        // 生成した日付から年月日を取得して比較
        if (String(date) == "Invalid Date") {
            return true;
        } else if (date.getFullYear() !== year || date.getMonth() + 1 !== month || date.getDate() !== day) {
            return true;
        }
        return false;
    }

    /**
     * Intl.DateTimeFormat を使用して、西暦日付から和暦日付を取得する
     * @param seireki 西暦日付
     * @param refdate 変換の基準日
     * @returns 和暦日付
     * @throws 引数が不正な場合エラーをスローする
     */
    public static convertSeirekiDateToWarekiDate(seireki: number | string | Date, refdate?: string): number {
        const result = DateUtils.seirekiArgsTypeCheckAndSuppplement(seireki, refdate);
        const seirekiDate = result.seirekiDate;
        // 補完済みの西暦日付
        const inputDigit = result.digit; // 入力された西暦日付の桁数

        // Intl.DateTimeFormat で元号名・元号年を取得
        const { gengoName, gengoYear } = DateUtils.getGengoPartsFromIntl(seirekiDate);

        // GENGO_MASTER のインデックス+1 が元号番号 (1=明治〜5=令和)
        const gengoIndex = DateUtils.GENGO_MASTER.findIndex((e) => e.name === gengoName);
        if (gengoIndex < 0) {
            throw new Error(`元号が取得できません: ${gengoName}`);
        }
        const gengoNum = gengoIndex + 1;

        const month = seirekiDate.getMonth() + 1;
        const day = seirekiDate.getDate();

        // 入力の桁数に応じた和暦桁数で返す
        switch (inputDigit) {
            case DateUtils.digitMode.SEIREKI_DATE:
                // 7桁: EYYMMDD
                return gengoNum * 1000000 + gengoYear * 10000 + month * 100 + day;
            case DateUtils.digitMode.SEIREKI_MONTH:
                // 5桁: EYYMM
                return gengoNum * 10000 + gengoYear * 100 + month;
            case DateUtils.digitMode.SEIREKI_YEAR:
                // 3桁: EYY
                return gengoNum * 100 + gengoYear;
            default:
                throw new Error(`不正な桁数: ${inputDigit}`);
        }
    }

    /**
     * 和暦日付を西暦日付に変換する
     * @param wareki 和暦日付 (3桁: EYY / 5桁: EYYMM / 7桁: EYYMMDD)
     * @param refdate 補完日付（5桁入力時: DD / 3桁入力時: MMDD）
     * @returns 西暦日付 (4桁: YYYY / 6桁: YYYYMM / 8桁: YYYYMMDD)
     * @throws 引数が不正な場合エラーをスローする
     */
    public static convertWarekiDateToSeirekiDate(wareki: number | string, refdate?: string): number {
        const warekiStr = wareki.toString();
        if (!ValidateUtils.isHalfWidthNumber(warekiStr)) {
            throw new Error("和暦日付ではない値が入力されました。");
        }
        const digit = warekiStr.length;

        if (digit !== DateUtils.digitMode.WAREKI_YEAR &&
            digit !== DateUtils.digitMode.WAREKI_MONTH &&
            digit !== DateUtils.digitMode.WAREKI_DATE) {
            throw new Error("和暦日付ではない値が入力されました。");
        }

        // 先頭1桁: 元号番号 (1=明治, 2=大正, 3=昭和, 4=平成, 5=令和)
        const gengoNum = parseInt(warekiStr.substring(0, 1));
        const gengo = DateUtils.GENGO_MASTER[gengoNum - 1];
        if (!gengo) {
            throw new Error(`不正な元号番号: ${gengoNum}`);
        }

        // 2〜3桁目: 元号年
        const gengoYear = parseInt(warekiStr.substring(1, 3));
        if (gengoYear < 1) {
            throw new Error(`不正な元号年: ${gengoYear}`);
        }

        // 西暦年を計算 (元号開始年 + 元号年 - 1)
        const seirekiYear = gengo.startYear + gengoYear - 1;

        switch (digit) {
            case DateUtils.digitMode.WAREKI_DATE: {
                // 7桁 (EYYMMDD) → 8桁 (YYYYMMDD)
                const month = parseInt(warekiStr.substring(3, 5));
                const day = parseInt(warekiStr.substring(5, 7));
                return seirekiYear * 10000 + month * 100 + day;
            }
            case DateUtils.digitMode.WAREKI_MONTH: {
                // 5桁 (EYYMM) → 6桁 (YYYYMM) / refdate があれば 8桁 (YYYYMMDD)
                const month = parseInt(warekiStr.substring(3, 5));
                if (refdate != null) {
                    return seirekiYear * 10000 + month * 100 + parseInt(refdate);
                }
                return seirekiYear * 100 + month;
            }
            case DateUtils.digitMode.WAREKI_YEAR: {
                // 3桁 (EYY) → 4桁 (YYYY) / refdate があれば 6桁または 8桁 (YYYYMM or YYYYMMDD)
                if (refdate != null) {
                    return seirekiYear * 10000 + parseInt(refdate);
                }
                return seirekiYear;
            }
            default:
                throw new Error(`不正な桁数: ${digit}`);
        }
    }

    /**
     * 引数として渡された西暦日付の型チェックと補完を行う
     * @param seireki 西暦日付
     * @param refdate 補完日付（和暦変換に使用する基準日）
     * @returns 西暦日付
     * @throws 引数が不正な場合エラーをスローする
     */
    public static seirekiArgsTypeCheckAndSuppplement(seireki: number | string | Date, refdate?: string): { seirekiDate: Date, digit: number } {
        if (seireki instanceof Date) {
            if (seireki.toString() === "Invalid Date") {
                throw new Error("西暦日付ではない値が入力されました。");
            }
            const seirekiDate = new Date(seireki.getFullYear(), seireki.getMonth(), seireki.getDate());
            return { seirekiDate, digit: DateUtils.digitMode.SEIREKI_DATE };
        }
        let seirekiStr = seireki.toString();
        if (!ValidateUtils.isHalfWidthNumber(seirekiStr)) {
            throw new Error("西暦日付ではない値が入力されました。");
        }
        const digit = seirekiStr.length;
        let refdateDigit = 0;
        switch (digit) {
            case DateUtils.digitMode.SEIREKI_DATE:
                break;
            case DateUtils.digitMode.SEIREKI_MONTH:
                if (refdate == null || refdate == undefined) {
                    seirekiStr += DateUtils.supplementMode.FIRST_OF_MONTH;
                } else {
                    seirekiStr += refdate;
                    refdateDigit = refdate.length;
                }
                break;
            case DateUtils.digitMode.SEIREKI_YEAR:
                if (refdate == null || refdate == undefined) {
                    // 指定された西暦年と基準年と一致するデータを元号テーブルより取得
                    const gengo = DateUtils.GENGO_MASTER.find(e => e.startYear <= Number(seirekiStr) && e.endYear >= Number(seirekiStr));
                    if (gengo == null || gengo == undefined) {
                        seirekiStr += "0401";
                    } else {
                        seirekiStr += ("00" + (gengo.kijunBi.getMonth() + 1)).slice(-2) + ("00" + gengo.kijunBi.getDate()).slice(-2);
                    }
                } else {
                    seirekiStr += refdate;
                    refdateDigit = refdate.length;
                }
                break;
            default:
                throw new Error("西暦日付ではない値が入力されました。");
        }
        let result: Date;
        try {
            result = TypeConvertUtils.toDateFromNum(seirekiStr);
        } catch {
            if ((digit === DateUtils.digitMode.SEIREKI_MONTH && refdateDigit === DateUtils.digitMode.DAY_OF_MONTH) ||
                (digit === DateUtils.digitMode.SEIREKI_YEAR && refdateDigit === DateUtils.digitMode.MONTH_DAY)) {
                result = new Date(parseInt(seirekiStr.substring(0, 4)), parseInt(seirekiStr.substring(4, 6)), 0);
            } else {
                throw new Error("西暦日付ではない値が入力されました。");
            }
        }
        return { seirekiDate: result, digit };
    }

    /**
     * 日付を数値や文字列からDate型に変換する
     * 引数が西暦8桁または和暦7桁でない場合はエラーをスローする
     * @param data 日付を表す数値または文字列
     * @returns 変換したDate
     * @throws 変換できない場合エラーをスローする
     */
    public static toDate(data: number | string): Date {
        return TypeConvertUtils.toDateFromNum(data);
    }

    /**
     * Intl.DateTimeFormat を使用して、日付から元号名と元号年を取得する
     * @param date 日付
     * @returns { gengoName: 元号名, gengoYear: 元号年 (和暦年) }
     */
    private static getGengoPartsFromIntl(date: Date): { gengoName: string; gengoYear: number } {
        const parts = new Intl.DateTimeFormat('ja-JP-u-ca-japanese', {
            era: 'long',
            year: 'numeric',
        }).formatToParts(date);
        const gengoName = parts.find((p) => p.type === 'era')?.value ?? '';
        const gengoYear = Number(parts.find((p) => p.type === 'year')?.value ?? '1');
        return { gengoName, gengoYear };
    }

    /**
     * 数値をパディングして文字列に変換する
     * @param value     数値
     * @param mode      パディングモード
     * @param length    桁数 (デフォルト: 2)
     * @returns パディングされた文字列
     */
    private static pad(value: number, mode: number, length: number = 2): string {
        const str = String(value);
        if (mode === DateUtils.PADDING_MODE.ZERO) return str.padStart(length, '0');
        if (mode === DateUtils.PADDING_MODE.SPACE) return str.padStart(length, ' ');
        return str;
    }

    /**
     * 半角数字を全角数字に変換する
     * @param str 変換対象文字列
     * @returns 全角文字列
     */
    private static toFullWidth(str: string): string {
        return str.replace(/[0-9]/g, (c) => String.fromCharCode(c.charCodeAt(0) + 0xfee0));
    }

    /**
     * 日付を指定フォーマットの文字列に変換する
     *
     * フォーマットトークン (長いものを先に指定すること):
     * - GGGYY : 元号名+元号年パディング (例: 令和06)
     * - GGG   : 元号名 (例: 令和)
     * - GYY   : 元号略称+元号年パディング (例: R06)
     * - GY    : 元号略称+元号年 (例: R6)
     * - G     : 元号略称 (例: R)
     * - YYYY  : 西暦4桁 (例: 2024)
     * - YY    : 元号年パディング (例: 06)
     * - MM    : 月パディング (例: 01)
     * - DD    : 日パディング (例: 01)
     *
     * @param date             日付
     * @param format           フォーマット文字列
     * @param paddingMode      パディングモード (デフォルト: ZERO)
     * @param convertFullWidth 全角変換有無 (デフォルト: false)
     * @returns フォーマットされた日付文字列
     */
    static dateFormat(
        date: Date | null,
        format: string,
        paddingMode: number = DateUtils.PADDING_MODE.ZERO,
        convertFullWidth: boolean = false,
    ): string {
        if (!date || isNaN(date.getTime())) return '';

        const { gengoName, gengoYear } = DateUtils.getGengoPartsFromIntl(date);
        const gengoAbbr = DateUtils.GENGO_MASTER.find((e) => e.name === gengoName)?.abbr ?? '';

        const year = date.getFullYear();
        const month = date.getMonth() + 1;
        const day = date.getDate();

        // 長いトークンを先に置換して誤置換を防止する
        let result = format
            .replace('GGGYY', gengoName + DateUtils.pad(gengoYear, paddingMode))
            .replace('GGG', gengoName)
            .replace('GYY', gengoAbbr + DateUtils.pad(gengoYear, paddingMode))
            .replace('GY', gengoAbbr + String(gengoYear))
            .replace('G', gengoAbbr)
            .replace('YYYY', String(year))
            .replace('YY', DateUtils.pad(gengoYear, paddingMode))
            .replace('MM', DateUtils.pad(month, paddingMode))
            .replace('DD', DateUtils.pad(day, paddingMode));

        if (convertFullWidth) {
            result = DateUtils.toFullWidth(result);
        }

        return result;
    }
}

export { DateUtils };