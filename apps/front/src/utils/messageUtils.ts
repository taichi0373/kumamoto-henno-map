import { InputFormErrorDto } from "@/dto/InputFormErrorDto";
import { TypeConvertUtils } from "./typeConvertUtils";
import { messageDto } from "@/dto/messageDto";

class MessageUtils {
    /**
     * プレースホルダー {0}, {1}, ... を引数で置換する
     * @param message 置換対象のメッセージ文字列
     * @param args 置換する文字列
     * @returns 置換後の文字列
     */
    private static format(message: string, ...args: string[]): string {
        return message.replace(/\{(\d+)\}/g, (_, index) => {
            const i = Number(index);
            return i < args.length ? args[i] : `{${i}}`;
        });
    }

    /**
     * メッセージリストからDTOを取得する
     * @param messageList メッセージリスト
     * @param messageNo メッセージ番号
     * @param messageString プレースホルダーに埋め込む文字列
     * @returns InputFormErrorDto メッセージDTO
     */
    public static getMessageDto(
        messageList: Array<messageDto>,
        messageNo: number,
        ...messageString: string[]
    ): InputFormErrorDto {
        let type = 0;
        let message = "";
        for (const data of messageList) {
            if (data.messageNo === messageNo) {
                type = data.messageType;
                message = data.messageContent;
            }
        }
        return new InputFormErrorDto(
            type,
            MessageUtils.format(message, ...messageString)
        );
    }

    /**
     * メッセージ種別番号から種別名文字列を取得する
     * @param messageList メッセージリスト
     * @param messageNo メッセージ番号
     * @return number エラータイプ
     */
    public static getMessageType(messageList: Array<messageDto>, messageNo: number): number {
        let type = 0;
        for (const data of messageList) {
            if (data.messageNo === messageNo) {
                type = data.messageType;
            }
        }
        return type;
    }

    /**
     * メッセージリストからフォーマット済み文字列を取得する
     * @param messageList メッセージリスト
     * @param messageNo メッセージ番号
     * @param messageString プレースホルダーに埋め込む文字列
     * @returns フォーマット済みメッセージ文字列
     */
    public static getFormatMessage(
        messageList: Array<messageDto>,
        messageNo: number,
        ...messageString: string[]
    ): string {
        let message = "";
        for (const data of messageList) {
            if (data.messageNo === messageNo) {
                message = data.messageContent;
            }
        }
        return TypeConvertUtils.toStringNullOrZeroToEmpty(
            MessageUtils.format(message, ...messageString)
        );
    }
}

export { MessageUtils };
