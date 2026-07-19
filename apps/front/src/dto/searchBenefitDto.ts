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
    /** フリーワード */
    keyword: string | null;
    /** カテゴリコード */
    categoryCd: string | null;
    /** 現在地緯度（現在地周辺フィルタリング用） */
    latitude: number | undefined;
    /** 現在地経度（現在地周辺フィルタリング用） */
    longitude: number | undefined;
    /** 検索半径（km） */
    radiusKm: number | undefined;
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
    /** フリーワード */
    keyword: string | null;
    /** カテゴリコード */
    categoryCd: string | null;
    /** 現在地緯度（現在地周辺フィルタリング用） */
    latitude: number | undefined;
    /** 現在地経度（現在地周辺フィルタリング用） */
    longitude: number | undefined;
    /** 検索半径（km） */
    radiusKm: number | undefined;
    /**
     * コンストラクタ
     * @param searchBenefitInterface 特典検索インタフェース
     */
    constructor(searchBenefitInterface?: SearchBenefitInterface) {
        if (searchBenefitInterface != null) {
            this.address = searchBenefitInterface.address != null ? searchBenefitInterface.address : null;
            this.licenseStatus = searchBenefitInterface.licenseStatus != null ? searchBenefitInterface.licenseStatus : null;
            this.age = searchBenefitInterface.age != null ? searchBenefitInterface.age : null;
            this.keyword = searchBenefitInterface.keyword != null ? searchBenefitInterface.keyword : null;
            this.categoryCd = searchBenefitInterface.categoryCd != null ? searchBenefitInterface.categoryCd : null;
            this.latitude = searchBenefitInterface.latitude;
            this.longitude = searchBenefitInterface.longitude;
            this.radiusKm = searchBenefitInterface.radiusKm;
        } else {
            this.address = null;
            this.licenseStatus = null;
            this.age = null;
            this.keyword = null;
            this.categoryCd = null;
            this.latitude = undefined;
            this.longitude = undefined;
            this.radiusKm = undefined;
        }
    }
}

export { SearchBenefitDto };