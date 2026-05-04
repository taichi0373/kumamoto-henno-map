/** 
 * エラー情報インターフェース
*/
interface InputFormErrorInterface {
    /** エラーの種類 */
    type: number;
    /** エラーメッセージ */
    message: string;
}

/** 
 * エラー情報DTO
*/
class InputFormErrorDto {
    /** エラーの種類 */
    type: number;
    /** エラーメッセージ */
    message: string;
    /**
     * コンストラクタ
     * @param type エラーの種類
     * @param message エラーメッセージ
     */
    constructor(type: number, message: string) {
        this.type = type;
        this.message = message;
    }
}

export { InputFormErrorDto, InputFormErrorInterface };