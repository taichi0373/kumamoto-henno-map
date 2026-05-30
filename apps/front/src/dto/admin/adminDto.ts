/**
 * 管理画面用TypeScript型定義
 *
 * 全管理対象エンティティのDTOインターフェースを定義する。
 * バックエンドのAdminPagedResponseDto<T>に対応する共通ページングレスポンス型を含む。
 */

/** 管理画面用ページング付きレスポンス型 */
export interface AdminPagedResponse<T> {
  /** データ一覧 */
  items: T[]
  /** 総件数 */
  total: number
  /** 現在ページ（0始まり） */
  page: number
  /** 1ページあたりの件数 */
  size: number
}

/**
 * 特典管理用DTO（BENEFITテーブル）
 */
export interface BenefitAdminDto {
  /** 特典ID */
  benefitId: string
  /** 自治体コード */
  municipalityCd: string
  /** カテゴリコード */
  categoryCd: string
  /** 特典名称 */
  benefitName: string | null
  /** 特典短縮名称 */
  benefitShortName: string | null
  /** 特典内容 */
  benefitDetail: string | null
  /** 有効期限 */
  expDetail: string | null
  /** 問い合わせ電話番号 */
  phoneNumber: string | null
  /** 特典URL */
  benefitUrl: string | null
  /** 住所 */
  address: string | null
  /** 緯度 */
  latitude: number | null
  /** 経度 */
  longitude: number | null
  /** 作成日時 */
  sysCreatedAt: string | null
  /** 更新日時 */
  sysUpdatedAt: string | null
}

/**
 * 特典条件管理用DTO（BENEFIT_ELIGIBILITYテーブル）
 */
export interface BenefitEligibilityAdminDto {
  /** ID */
  id: number
  /** 特典ID */
  benefitId: string
  /** 運転免許所持状況 */
  licenseStatus: string | null
  /** 最低年齢 */
  minAge: number | null
  /** 最高年齢 */
  maxAge: number | null
  /** 対象自治体コード */
  municipalityCd: string | null
  /** 備考 */
  note: string | null
  /** 作成日時 */
  sysCreatedAt: string | null
  /** 更新日時 */
  sysUpdatedAt: string | null
}

/**
 * 特典カテゴリ管理用DTO（BENEFIT_CATEGORYテーブル）
 */
export interface BenefitCategoryAdminDto {
  /** 種別コード */
  categoryCd: string
  /** 種別名称 */
  categoryName: string
  /** 表示順 */
  displayOrder: number | null
  /** 有効フラグ（1:有効,0:無効） */
  isActive: string
  /** 作成日時 */
  sysCreatedAt: string | null
  /** 更新日時 */
  sysUpdatedAt: string | null
}

/**
 * 自治体管理用DTO（MUNICIPALITYテーブル）
 */
export interface MunicipalityAdminDto {
  /** 自治体コード */
  municipalityCd: string
  /** 自治体名称 */
  municipalityName: string
  /** 自治体名称かな */
  municipalityKana: string | null
  /** 自治体区分 */
  municipalityType: string
  /** 作成日時 */
  sysCreatedAt: string | null
  /** 更新日時 */
  sysUpdatedAt: string | null
}

/**
 * 事業者管理用DTO（AGENCYテーブル）
 */
export interface AgencyAdminDto {
  /** 事業者ID */
  agencyId: string
  /** 事業者名称 */
  agencyName: string
  /** 事業者名称かな */
  agencyKana: string | null
  /** 問い合わせ電話番号 */
  phoneNumber: string | null
  /** 事業者URL */
  agencyUrl: string | null
  /** 運行事業者ID */
  operatorId: string | null
  /** 作成日時 */
  sysCreatedAt: string | null
  /** 更新日時 */
  sysUpdatedAt: string | null
}

/**
 * 運賃割引管理用DTO（FARE_DISCOUNTテーブル）
 * 複合PK（benefitId + agencyId）
 */
export interface FareDiscountAdminDto {
  /** 特典ID */
  benefitId: string
  /** 事業者ID */
  agencyId: string
  /** 割引種別（0:割引率, 1:無料） */
  discountType: string | null
  /** 割引値 */
  discountValue: number | null
  /** 作成日時 */
  sysCreatedAt: string | null
  /** 更新日時 */
  sysUpdatedAt: string | null
}

/**
 * コミュニティバス路線管理用DTO（COMMUNITY_BUSテーブル）
 */
export interface CommunityBusAdminDto {
  /** 路線ID */
  routeId: string
  /** コミュニティバスID（事業者ID） */
  communityBusId: string | null
  /** 行先名称 */
  routeName: string | null
  /** 作成日時 */
  sysCreatedAt: string | null
  /** 更新日時 */
  sysUpdatedAt: string | null
}

/**
 * ユーザー管理用DTO（USERSテーブル）
 * パスワードハッシュは含めない
 */
export interface UserAdminDto {
  /** ユーザーID */
  userId: number
  /** ユーザー名 */
  username: string
  /** メールアドレス */
  email: string
  /** 生年月日 */
  birthDate: string | null
  /** 自治体コード */
  municipalityCd: string | null
  /** 運転免許所持状況 */
  licenseStatus: string | null
  /** 運転免許返納日 */
  licenseSurrenderedAt: string | null
  /** 作成日時 */
  sysCreatedAt: string | null
  /** 更新日時 */
  sysUpdatedAt: string | null
}
