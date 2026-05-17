/**
 * フィードバックインタフェース
 */
interface FeedbackInterface {
    /** カテゴリ（BUG / REQUEST / OTHER） */
    category: string | null;
    /** お名前（任意） */
    name: string | null;
    /** メールアドレス（任意） */
    email: string | null;
    /** 内容（必須） */
    content: string | null;
}

/**
 * フィードバックDTO
 */
class FeedbackDto {
    /** カテゴリ（BUG / REQUEST / OTHER） */
    category: string | null;
    /** お名前（任意） */
    name: string | null;
    /** メールアドレス（任意） */
    email: string | null;
    /** 内容（必須） */
    content: string | null;

    /**
     * コンストラクタ
     * @param feedbackInterface フィードバックインタフェース
     */
    constructor(feedbackInterface?: FeedbackInterface) {
        if (feedbackInterface != null) {
            this.category = feedbackInterface.category != null ? feedbackInterface.category : null;
            this.name = feedbackInterface.name != null ? feedbackInterface.name : null;
            this.email = feedbackInterface.email != null ? feedbackInterface.email : null;
            this.content = feedbackInterface.content != null ? feedbackInterface.content : null;
        } else {
            this.category = null;
            this.name = null;
            this.email = null;
            this.content = null;
        }
    }
}

export { FeedbackDto };
