/** 
 * 特典情報インタフェース
*/
interface BenefitInterface {

    /** 特典ID */
    benefitId: string | null;

    /** 自治体コード */
    municipalityCd: string | null;

    /** 自治体名称 */
    municipalityName: string | null;

    /** カテゴリコード */
    categoryCd: string | null;

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

    /** 運転免許所持状況 */
    licenseStatus: string | null;

    /** 最低年齢 */
    minAge: number | null;

    /** 最高年齢 */
    maxAge: number | null;

    /** 備考 */
    note: string | null;
}

/** 
 * 特典情報DTO
*/
class BenefitDto {
    /** 特典ID */
    benefitId: string | null;

    /** 自治体コード */
    municipalityCd: string | null;

    /** 自治体名称 */
    municipalityName: string | null;

    /** カテゴリコード */
    categoryCd: string | null;

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

    /** 運転免許所持状況 */
    licenseStatus: string | null;

    /** 最低年齢 */
    minAge: number | null;

    /** 最高年齢 */
    maxAge: number | null;

    /** 備考 */
    note: string | null;

    /**
     * コンストラクタ
     * @param benefitInterface 特典情報インタフェース
     */
    constructor(benefitInterface?: BenefitInterface) {
        if (benefitInterface != null) {
            this.benefitId = benefitInterface.benefitId != null ? benefitInterface.benefitId : null;
            this.municipalityCd = benefitInterface.municipalityCd != null ? benefitInterface.municipalityCd : null;
            this.municipalityName = benefitInterface.municipalityName != null ? benefitInterface.municipalityName : null;
            this.categoryCd = benefitInterface.categoryCd != null ? benefitInterface.categoryCd : null;
            this.benefitName = benefitInterface.benefitName != null ? benefitInterface.benefitName : null;
            this.benefitShortName = benefitInterface.benefitShortName != null ? benefitInterface.benefitShortName : null;
            this.benefitDetail = benefitInterface.benefitDetail != null ? benefitInterface.benefitDetail : null;
            this.expDetail = benefitInterface.expDetail != null ? benefitInterface.expDetail : null;
            this.phoneNumber = benefitInterface.phoneNumber != null ? benefitInterface.phoneNumber : null;
            this.benefitUrl = benefitInterface.benefitUrl != null ? benefitInterface.benefitUrl : null;
            this.licenseStatus = benefitInterface.licenseStatus != null ? benefitInterface.licenseStatus : null;
            this.minAge = benefitInterface.minAge != null ? benefitInterface.minAge : null;
            this.maxAge = benefitInterface.maxAge != null ? benefitInterface.maxAge : null;
            this.note = benefitInterface.note != null ? benefitInterface.note : null;
        } else {
            this.benefitId = null;
            this.municipalityCd = null;
            this.municipalityName = null;
            this.categoryCd = null;
            this.benefitName = null;
            this.benefitShortName = null;
            this.benefitDetail = null;
            this.expDetail = null;
            this.phoneNumber = null;
            this.benefitUrl = null;
            this.licenseStatus = null;
            this.minAge = null;
            this.maxAge = null;
            this.note = null;
        }
    }
}

export { BenefitDto };