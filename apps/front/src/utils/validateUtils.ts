class ValidateUtils {
  /** パスワードの最小文字数 */
  public static readonly PASSWORD_MIN_LENGTH = 8
  /** 半角文字のパターン */
  private static readonly HALF_WIDTH_PATTERN = /^[\u0020-\u007E]+$/
  /** 全角文字のパターン */
  private static readonly FULL_WIDTH_PATTERN = /^[^ -~｡-ﾟ]+$/
  /** 半角数値のパターン */
  private static readonly HALF_WIDTH_NUMBER_PATTERN = /^[0-9]+$/
  /** 全角数値のパターン */
  private static readonly FULL_WIDTH_NUMBER_PATTERN = /^[０-９]+$/
  /** 半角アルファベットのパターン */
  private static readonly HALF_WIDTH_ALPHABET_PATTERN = /^[A-Za-z]+$/
  /** 全角アルファベットのパターン */
  private static readonly FULL_WIDTH_ALPHABET_PATTERN = /^[Ａ-Ｚａ-ｚ]+$/
  /** 電話番号のパターン */
  private static readonly PHONE_NUMBER_PATTERN = /^(0\d{1,4}-\d{1,4}-\d{4}|0\d{9,10})$/
  /** メールアドレスのパターン */
  private static readonly EMAIL_PATTERN = /^[\w.-]+@[\w.-]+\.[a-zA-Z]{2,}$/

  /**
   * オブジェクトが null / undefined または空（空文字・空配列・空オブジェクト・無効日付）かどうか
   */
  public static isNullOrEmpty(obj: unknown): obj is null | undefined {
    // null または undefined の場合は true
    if (obj === null || obj === undefined) return true
    // 文字列の場合、空文字の場合は true
    if (typeof obj === 'string') return obj.trim().length === 0
    // 配列の場合、要素がない場合は true
    if (Array.isArray(obj)) return obj.length === 0
    // 日付オブジェクトの場合は、有効日付でない場合は true
    if (obj instanceof Date) return isNaN(obj.getTime())
    // オブジェクトの場合、プロパティがない場合は true
    if (typeof obj === 'object') return Object.keys(obj as Record<string, unknown>).length === 0
    return false
  }

  /**
   * 半角文字のみ
   */
  public static isHalfWidthChar(str: string): boolean {
    if (this.isNullOrEmpty(str)) return false
    return this.HALF_WIDTH_PATTERN.test(str)
  }

  /**
   * 全角文字のみ
   */
  public static isFullWidthChar(str: string): boolean {
    if (this.isNullOrEmpty(str)) return false
    return this.FULL_WIDTH_PATTERN.test(str)
  }

  /**
   * 半角数値のみ
   */
  public static isHalfWidthNumber(str: string): boolean {
    if (this.isNullOrEmpty(str)) return false
    return this.HALF_WIDTH_NUMBER_PATTERN.test(str)
  }

  /**
   * 全角数値のみ
   */
  public static isFullWidthNumber(str: string): boolean {
    if (this.isNullOrEmpty(str)) return false
    return this.FULL_WIDTH_NUMBER_PATTERN.test(str)
  }

  /**
   * 半角アルファベットのみ
   */
  public static isHalfWidthAlphabet(str: string): boolean {
    if (this.isNullOrEmpty(str)) return false
    return this.HALF_WIDTH_ALPHABET_PATTERN.test(str)
  }

  /**
   * 全角アルファベットのみ
   */
  public static isFullWidthAlphabet(str: string): boolean {
    if (this.isNullOrEmpty(str)) return false
    return this.FULL_WIDTH_ALPHABET_PATTERN.test(str)
  }

  /**
   * 電話番号形式
   * 例: 03-1234-5678, 09012345678
   */
  public static isPhoneNumber(str: string): boolean {
    if (this.isNullOrEmpty(str)) return false
    return this.PHONE_NUMBER_PATTERN.test(str)
  }

  /**
   * メールアドレス形式
   */
  public static isEmail(str: string): boolean {
    if (this.isNullOrEmpty(str)) return false
    return this.EMAIL_PATTERN.test(str)
  }

  /** コンストラクタ */
  private constructor() {}
}

export { ValidateUtils };
