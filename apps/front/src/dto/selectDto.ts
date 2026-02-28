/**
 * プルダウン用インタフェース
 */
interface SelectOption {
    /** 表示用ラベル */
    label: string;
    /** 検索用テキスト */
    text: string;
    /** 値 */
    value: string | number;
}

class SelectDto {
    /** 表示用ラベル */
    label: string;
    /** 検索用テキスト */
    text: string;
    /** 値 */
    value: string | number;
    /** コンストラクタ */
    constructor(selectOption: SelectOption) {
        this.label = selectOption.label;
        this.text = selectOption.text;
        this.value = selectOption.value;
    }
}

export { SelectOption, SelectDto };