/** 
 * ユーザー情報インタフェース
*/
interface UsersInterface {
    /** ユーザーID */
    userId: string | null;
    /** ユーザー名 */
    username: string | null;
    /** メールアドレス */
    email: string | null;
    /** パスワード */
    password: string | null;
    /** パスワード確認 */
    confirmPassword: string | null;
    /** 生年月日 */
    birthDate: string | Date | null;
    /** 居住地域 */
    address: string | null;
    /** 運転免許の所持状況 */
    licenseStatus: string | null;
}

/** 
 * ユーザー情報DTO
*/
class UsersDto {
    /** ユーザーID */
    userId: string | null;
    /** ユーザー名 */
    username: string | null;
    /** メールアドレス */
    email: string | null;
    /** パスワード */
    password: string | null;
    /** パスワード確認 */
    confirmPassword: string | null;
    /** 生年月日 */
    birthDate: string | Date | null;
    /** 居住地域 */
    address: string | null;
    /** 運転免許の所持状況 */
    licenseStatus: string | null;
    /**
     * コンストラクタ
     * @param usersInterface ユーザー情報インタフェース
     */
    constructor(usersInterface?: UsersInterface) {
        if (usersInterface != null) {
            this.userId = usersInterface.userId != null ? usersInterface.userId : null;
            this.username = usersInterface.username != null ? usersInterface.username : null;
            this.email = usersInterface.email != null ? usersInterface.email : null;
            this.password = usersInterface.password != null ? usersInterface.password : null;
            this.confirmPassword = usersInterface.confirmPassword != null ? usersInterface.confirmPassword : null;
            this.birthDate = usersInterface.birthDate != null ? usersInterface.birthDate : null;
            this.address = usersInterface.address != null ? usersInterface.address : null;
            this.licenseStatus = usersInterface.licenseStatus != null ? usersInterface.licenseStatus : null;
        } else {
            this.userId = null;
            this.username = null;
            this.email = null;
            this.password = null;
            this.confirmPassword = null;
            this.birthDate = null;
            this.address = null;
            this.licenseStatus = null;
        }
    }
}

export { UsersDto };