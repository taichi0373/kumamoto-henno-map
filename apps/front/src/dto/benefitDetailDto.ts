/**
 * 特典詳細情報インタフェース（V_BENEFIT_DETAIL）
 */
interface BenefitDetailInterface {

    // -------------------------------------------------------------------------
    // 特典情報
    // -------------------------------------------------------------------------

    /** 特典ID */
    benefitId: string | null;

    /** 自治体コード */
    municipalityCd: string | null;

    /** 自治体名称 */
    municipalityName: string | null;

    /** 自治体名称かな */
    municipalityKana: string | null;

    /** 特典名称 */
    benefitName: string | null;

    /** 特典短縮名称 */
    benefitShortName: string | null;

    /** 特典内容 */
    benefitDetail: string | null;

    /** 有効期限 */
    expDetail: string | null;

    /** 問い合わせ電話番号 */
    phoneNumber: string | null;

    /** 特典URL */
    benefitUrl: string | null;

    /** 住所 */
    address: string | null;

    /** 緯度 */
    latitude: number | null;

    /** 経度 */
    longitude: number | null;

    // -------------------------------------------------------------------------
    // カテゴリ情報
    // -------------------------------------------------------------------------

    /** カテゴリコード */
    categoryCd: string | null;

    /** カテゴリ名称 */
    categoryName: string | null;

    /** 表示順 */
    displayOrder: number | null;

    /** カテゴリ有効フラグ（1:有効, 0:無効） */
    categoryIsActive: string | null;

    // -------------------------------------------------------------------------
    // 利用条件情報
    // -------------------------------------------------------------------------

    /** 利用条件ID */
    eligibilityId: number | null;

    /** 運転免許所持状況 */
    licenseStatus: string | null;

    /** 最低年齢 */
    minAge: number | null;

    /** 最高年齢 */
    maxAge: number | null;

    /** 利用可能な自治体コード */
    eligibilityMunicipalityCd: string | null;

    /** 備考 */
    eligibilityNote: string | null;
}

/**
 * 特典詳細情報DTO（V_BENEFIT_DETAIL）
 * <p>特典・カテゴリ・利用条件を結合したビュー用DTO。</p>
 */
class BenefitDetailDto {

    // -------------------------------------------------------------------------
    // 特典情報
    // -------------------------------------------------------------------------

    /** 特典ID */
    benefitId: string | null;

    /** 自治体コード */
    municipalityCd: string | null;

    /** 自治体名称 */
    municipalityName: string | null;

    /** 自治体名称かな */
    municipalityKana: string | null;

    /** 特典名称 */
    benefitName: string | null;

    /** 特典短縮名称 */
    benefitShortName: string | null;

    /** 特典内容 */
    benefitDetail: string | null;

    /** 有効期限 */
    expDetail: string | null;

    /** 問い合わせ電話番号 */
    phoneNumber: string | null;

    /** 特典URL */
    benefitUrl: string | null;

    /** 住所 */
    address: string | null;

    /** 緯度 */
    latitude: number | null;

    /** 経度 */
    longitude: number | null;

    // -------------------------------------------------------------------------
    // カテゴリ情報
    // -------------------------------------------------------------------------

    /** カテゴリコード */
    categoryCd: string | null;

    /** カテゴリ名称 */
    categoryName: string | null;

    /** 表示順 */
    displayOrder: number | null;

    /** カテゴリ有効フラグ（1:有効, 0:無効） */
    categoryIsActive: string | null;

    // -------------------------------------------------------------------------
    // 利用条件情報
    // -------------------------------------------------------------------------

    /** 利用条件ID */
    eligibilityId: number | null;

    /** 運転免許所持状況 */
    licenseStatus: string | null;

    /** 最低年齢 */
    minAge: number | null;

    /** 最高年齢 */
    maxAge: number | null;

    /** 利用可能な自治体コード */
    eligibilityMunicipalityCd: string | null;

    /** 備考 */
    eligibilityNote: string | null;

    /**
     * コンストラクタ
     * @param dto 特典詳細情報インタフェース
     */
    constructor(dto?: BenefitDetailInterface) {
        if (dto != null) {
            this.benefitId = dto.benefitId;
            this.municipalityCd = dto.municipalityCd;
            this.municipalityName = dto.municipalityName;
            this.municipalityKana = dto.municipalityKana;
            this.benefitName = dto.benefitName;
            this.benefitShortName = dto.benefitShortName;
            this.benefitDetail = dto.benefitDetail;
            this.expDetail = dto.expDetail;
            this.phoneNumber = dto.phoneNumber;
            this.benefitUrl = dto.benefitUrl;
            this.address = dto.address;
            this.latitude = dto.latitude;
            this.longitude = dto.longitude;
            this.categoryCd = dto.categoryCd;
            this.categoryName = dto.categoryName;
            this.displayOrder = dto.displayOrder;
            this.categoryIsActive = dto.categoryIsActive;
            this.eligibilityId = dto.eligibilityId;
            this.licenseStatus = dto.licenseStatus;
            this.minAge = dto.minAge;
            this.maxAge = dto.maxAge;
            this.eligibilityMunicipalityCd = dto.eligibilityMunicipalityCd;
            this.eligibilityNote = dto.eligibilityNote;
        } else {
            this.benefitId = null;
            this.municipalityCd = null;
            this.municipalityName = null;
            this.municipalityKana = null;
            this.benefitName = null;
            this.benefitShortName = null;
            this.benefitDetail = null;
            this.expDetail = null;
            this.phoneNumber = null;
            this.benefitUrl = null;
            this.address = null;
            this.latitude = null;
            this.longitude = null;
            this.categoryCd = null;
            this.categoryName = null;
            this.displayOrder = null;
            this.categoryIsActive = null;
            this.eligibilityId = null;
            this.licenseStatus = null;
            this.minAge = null;
            this.maxAge = null;
            this.eligibilityMunicipalityCd = null;
            this.eligibilityNote = null;
        }
    }
}

export { BenefitDetailInterface, BenefitDetailDto };
