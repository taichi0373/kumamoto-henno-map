class MessageUtils {
    /** 
     * メッセージに文字列を埋め込む
     * @param pattern パターン文字列
     * @param args 埋め込む文字列の配列
     * @returns 埋め込まれた文字列
    */
   public static format(pattern: string | null, ...args: (string | number | boolean)[]): string | null {
        let result: string | null = pattern;

        if (result !== null) {
            args.forEach((value, index) => {
                const placeholder = `{${index}}`;
                result = result!.replace(placeholder, String(value));
            });
        }
        return result;
    }
}

export { MessageUtils };


// // 基本的な使用例
// MessageUtils.format("こんにちは、{0}さん！", "田中");
// // 結果: "こんにちは、田中さん！"

// // 複数の値を置換
// MessageUtils.format("{0}年{1}月{2}日", 2026, 2, 11);
// // 結果: "2026年2月11日"

// // 数値・真偽値も対応
// MessageUtils.format("価格: {0}円、在庫: {1}", 1000, true);
// // 結果: "価格: 1000円、在庫: true"

// // nullパターンの場合
// MessageUtils.format(null, "値");
// // 結果: null