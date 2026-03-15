/**
 * タブ用インタフェース
 */
interface TabInterface {
    /** ID */
    id: string;
    /** ラベル */
    label: string;
}

class TabDto {
    /** ID */
    id: string;
    /** ラベル */
    label: string;
    /** コンストラクタ */
    constructor(tabInterface: TabInterface) {
        this.id = tabInterface.id;
        this.label = tabInterface.label;
    }
}

export { TabInterface, TabDto };