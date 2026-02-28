/** 
 * 特典検索インタフェース
*/
interface SearchBenefitInterface {
    /** 居住地域 */
    address: string | null;
    /** 運転免許の所持状況 */
    licenseStatus: string | null;
    /** 年齢 */
    age: number | null;
}

/** 
 * 特典検索DTO
*/
class SearchBenefitDto {
        /** 居住地域 */
    address: string | null;
    /** 運転免許の所持状況 */
    licenseStatus: string | null;
    /** 年齢 */
    age: number | null;
    /**
     * コンストラクタ
     * @param searchBenefitInterface 特典検索インタフェース
     */
    constructor(searchBenefitInterface?: SearchBenefitInterface) {
        if (searchBenefitInterface != null) {
            this.address = searchBenefitInterface.address != null ? searchBenefitInterface.address : null;
            this.licenseStatus = searchBenefitInterface.licenseStatus != null ? searchBenefitInterface.licenseStatus : null;
            this.age = searchBenefitInterface.age != null ? searchBenefitInterface.age : null;
        } else {
            this.address = null;
            this.licenseStatus = null;
            this.age = null;
        }
    }
}

export { SearchBenefitDto };