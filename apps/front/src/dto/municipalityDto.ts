/**
 * 自治体情報インタフェース
 */
interface MunicipalityInterface {
    /** 自治体コード */
    municipalityCd: string;
    /** 自治体名称 */
    municipalityName: string;
    /** 自治体名称かな */
    municipalityKana: string;
}

/**
 * 自治体情報DTO
 */
class MunicipalityDto {
    /** 自治体コード */
    municipalityCd: string;
    /** 自治体名称 */
    municipalityName: string;
    /** 自治体名称かな */
    municipalityKana: string;
    /**
     * コンストラクタ
     * @param municipalityInterface 自治体情報インタフェース
     */
    constructor(municipalityInterface?: MunicipalityInterface) {
        if (municipalityInterface != null) {
            this.municipalityCd = municipalityInterface.municipalityCd;
            this.municipalityName = municipalityInterface.municipalityName;
            this.municipalityKana = municipalityInterface.municipalityKana;
        } else {
            this.municipalityCd = '';
            this.municipalityName = '';
            this.municipalityKana = '';
        }
    }
}

export { MunicipalityInterface, MunicipalityDto };
