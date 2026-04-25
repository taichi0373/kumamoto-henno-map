/**
 * パスワード変更インタフェース
 */
interface ChangePasswordInterface {
    /** 現在のパスワード */
    currentPassword: string | null;
    /** 新しいパスワード */
    newPassword: string | null;
    /** 新しいパスワード（確認用） */
    confirmNewPassword: string | null;
}

/**
 * パスワード変更DTO
 */
class ChangePasswordDto {
    /** 現在のパスワード */
    currentPassword: string | null;
    /** 新しいパスワード */
    newPassword: string | null;
    /** 新しいパスワード（確認用） */
    confirmNewPassword: string | null;

    /**
     * コンストラクタ
     * @param i パスワード変更インタフェース
     */
    constructor(i?: ChangePasswordInterface) {
        if (i != null) {
            this.currentPassword = i.currentPassword ?? null;
            this.newPassword = i.newPassword ?? null;
            this.confirmNewPassword = i.confirmNewPassword ?? null;
        } else {
            this.currentPassword = null;
            this.newPassword = null;
            this.confirmNewPassword = null;
        }
    }
}

export { ChangePasswordDto };
