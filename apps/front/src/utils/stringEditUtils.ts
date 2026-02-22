/**
 * 文字変換・編集に関するユーティリティクラス
 */
class StringEditUtils {

    /**
     * ひらがな → 半角カタカナ 変換マップ
     */
    private static readonly mapFullWidthHiraganaToHalfWidthKatakana = new Map<string, string>([
        ['あ', 'ｱ'], ['い', 'ｲ'], ['う', 'ｳ'], ['え', 'ｴ'], ['お', 'ｵ'],
        ['か', 'ｶ'], ['き', 'ｷ'], ['く', 'ｸ'], ['け', 'ｹ'], ['こ', 'ｺ'],
        ['さ', 'ｻ'], ['し', 'ｼ'], ['す', 'ｽ'], ['せ', 'ｾ'], ['そ', 'ｿ'],
        ['た', 'ﾀ'], ['ち', 'ﾁ'], ['つ', 'ﾂ'], ['て', 'ﾃ'], ['と', 'ﾄ'],
        ['な', 'ﾅ'], ['に', 'ﾆ'], ['ぬ', 'ﾇ'], ['ね', 'ﾈ'], ['の', 'ﾉ'],
        ['は', 'ﾊ'], ['ひ', 'ﾋ'], ['ふ', 'ﾌ'], ['へ', 'ﾍ'], ['ほ', 'ﾎ'],
        ['ま', 'ﾏ'], ['み', 'ﾐ'], ['む', 'ﾑ'], ['め', 'ﾒ'], ['も', 'ﾓ'],
        ['や', 'ﾔ'], ['ゆ', 'ﾕ'], ['よ', 'ﾖ'],
        ['ら', 'ﾗ'], ['り', 'ﾘ'], ['る', 'ﾙ'], ['れ', 'ﾚ'], ['ろ', 'ﾛ'],
        ['わ', 'ﾜ'], ['を', 'ｦ'], ['ん', 'ﾝ'],
        ['が', 'ｶﾞ'], ['ぎ', 'ｷﾞ'], ['ぐ', 'ｸﾞ'], ['げ', 'ｹﾞ'], ['ご', 'ｺﾞ'],
        ['ざ', 'ｻﾞ'], ['じ', 'ｼﾞ'], ['ず', 'ｽﾞ'], ['ぜ', 'ｾﾞ'], ['ぞ', 'ｿﾞ'],
        ['だ', 'ﾀﾞ'], ['ぢ', 'ﾁﾞ'], ['づ', 'ﾂﾞ'], ['で', 'ﾃﾞ'], ['ど', 'ﾄﾞ'],
        ['ば', 'ﾊﾞ'], ['び', 'ﾋﾞ'], ['ぶ', 'ﾌﾞ'], ['べ', 'ﾍﾞ'], ['ぼ', 'ﾎﾞ'],
        ['ぱ', 'ﾊﾟ'], ['ぴ', 'ﾋﾟ'], ['ぷ', 'ﾌﾟ'], ['ぺ', 'ﾍﾟ'], ['ぽ', 'ﾎﾟ'],
        ['ぁ', 'ｧ'], ['ぃ', 'ｨ'], ['ぅ', 'ｩ'], ['ぇ', 'ｪ'], ['ぉ', 'ｫ'],
        ['っ', 'ｯ'], ['ゃ', 'ｬ'], ['ゅ', 'ｭ'], ['ょ', 'ｮ'],
        ['・', '･'], ['ー', 'ｰ'], ['　', ' ']
    ]);

    /**
     * 半角カタカナ → ひらがな 変換マップ
     * */
    private static readonly mapHalfWidthKatakanaToFullWidthHiragana = new Map<string, string>(
        Array.from(StringEditUtils.mapFullWidthHiraganaToHalfWidthKatakana.entries()).map(([hiragana, katakana]) => [katakana, hiragana])
    );


    /**
     * 記号を全角 → 半角 変換マップ
     * */
    private static readonly mapFullWidthSymbolsToHalfWidth = new Map<string, string>([
        ['！', '!'], ['＂', '"'], ['＃', '#'], ['＄', '$'], ['％', '%'],
        ['＆', '&'], ['＇', '\''], ['（', '('], ['）', ')'], ['＊', '*'],
        ['＋', '+'], ['，', ','], ['－', '-'], ['．', '.'], ['／', '/'],
        ['：', ':'], ['；', ';'], ['＜', '<'], ['＝', '='], ['＞', '>'],
        ['？', '?'], ['＠', '@'], ['［', '['], ['＼', '\\'], ['］', ']'],
        ['＾', '^'], ['＿', '_'], ['｀', '`'], ['｛', '{'], ['｜', '|'],
        ['｝', '}'], ['～', '~']
    ]);

    /**
     * 記号を半角 → 全角 変換マップ
     * */
    private static readonly mapHalfWidthSymbolsToFullWidth = new Map<string, string>(
        Array.from(StringEditUtils.mapFullWidthSymbolsToHalfWidth.entries()).map(([fullWidth, halfWidth]) => [halfWidth, fullWidth])
    );

    /**
     * 半角カタカナを全角カタカナに変換する。
     * 濁点（ﾞ）・半濁点（ﾟ）は前の文字と合わせた2文字で1エントリとして変換する。
     * @param input 変換対象の文字列
     * @returns 変換後の文字列
     */
    public static replaceHalfWidthKatakanaToFullWidthKatakana(input: string): string {
        const chars = input.split('');
        const result: string[] = [];
        let i = 0;
        while (i < chars.length) {
            const twoChar = chars[i] + (chars[i + 1] ?? '');
            if (StringEditUtils.mapHalfWidthKatakanaToFullWidthHiragana.has(twoChar)) {
                result.push(StringEditUtils.mapHalfWidthKatakanaToFullWidthHiragana.get(twoChar)!);
                i += 2;
            } else {
                result.push(StringEditUtils.mapHalfWidthKatakanaToFullWidthHiragana.get(chars[i]) ?? chars[i]);
                i++;
            }
        }
        return result.join('');
    }

    /**
     * 全角カタカナを半角カタカナに変換する。
     * @param input 変換対象の文字列
     * @return 変換後の文字列
     */
    public static replaceFullWidthKatakanaToHalfWidthKatakana(input: string): string {
        return input.split('').map(char => StringEditUtils.mapFullWidthHiraganaToHalfWidthKatakana.get(char) || char).join('');
    }


    /**
     * 全角記号を半角記号に変換する。
     * @param input 変換対象の文字列
     * @return 変換後の文字列
     * */
    public static replaceFullWidthSymbolsToHalfWidth(input: string): string {
        return input.split('').map(char => StringEditUtils.mapFullWidthSymbolsToHalfWidth.get(char) || char).join('');
    }

    /**
     * 半角記号を全角記号に変換する。
     * @param input 変換対象の文字列
     * @return 変換後の文字列
     * */
    public static replaceHalfWidthSymbolsToFullWidth(input: string): string {
        return input.split('').map(char => StringEditUtils.mapHalfWidthSymbolsToFullWidth.get(char) || char).join('');
    }

    /**
     * 全角ひらがなを半角カタカナに変換する。
     * @param input 変換対象の文字列
     * @return 変換後の文字列
     */
    public static replaceFullWidthHiraganaToHalfWidthKatakana(input: string): string {
        return input.split('').map(char => StringEditUtils.mapFullWidthHiraganaToHalfWidthKatakana.get(char) || char).join('');
    }

    /**
     * 全角カタカナをひらがなに変換する（内部処理用）。
     * ァ〜ヶ (U+30A1〜U+30F6) はひらがなと 0x60 のオフセット差を利用して変換する。
     * @param input 変換対象の文字列
     * @return 変換後の文字列
     */
    private static replaceFullWidthKatakanaToHiragana(input: string): string {
        return input.replace(/[ァ-ヶ]/g, ch => String.fromCharCode(ch.charCodeAt(0) - 0x60));
    }

    /**
     * 全角英数字記号、全角カタカナを半角英数字記号、半角カタカナに変換する。
     * @param input 変換対象の文字列
     * @return 変換後の文字列
     * */
    public static replaceFullWidthCharToHalfWidthChar(input: string): string {
        let result = input;
        // 全角カタカナ → ひらがな → 半角カタカナ の順で変換
        result = StringEditUtils.replaceFullWidthKatakanaToHiragana(result);
        result = StringEditUtils.replaceFullWidthHiraganaToHalfWidthKatakana(result);
        result = StringEditUtils.replaceFullWidthSymbolsToHalfWidth(result);
        return result;
    }

}

export { StringEditUtils };