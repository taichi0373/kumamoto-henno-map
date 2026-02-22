/** 
 * メッセージ情報インターフェース
*/
interface MessageInterface {
    /** メッセージ番号 */
    messageNo: number;
    /** メッセージ種別番号 */
    messageType: number;
    /** メッセージ内容 */
    messageContent: string;
}

/** 
 * メッセージ情報DTO
*/
class MessageDto {
    /** メッセージ番号 */
    messageNo: number;
    /** メッセージ種別番号 */
    messageType: number;
    /** メッセージ内容 */
    messageContent: string;
    /**
     * コンストラクタ
     * @param messageInterface メッセージ情報インターフェース
     * */
    constructor(messageInterface: MessageInterface) {
        this.messageNo = messageInterface.messageNo;
        this.messageType = messageInterface.messageType;
        this.messageContent = messageInterface.messageContent;
    }
}

export { MessageInterface, MessageDto };