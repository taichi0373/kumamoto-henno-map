/**
 * 候補用インタフェース
 */
interface SuggestionInterface {
    /** id */
    id: number | null;
    /** 名称 */
    name: string | null;
    /** 住所 */
    address: string | null;
    /** 緯度 */
    lat?: number | null;
    /** 経度 */
    lon?: number | null;
}

class SuggestionDto {
    /** ID */
    id: number | null;
    /** 名称 */
    name: string | null;
    /** 住所 */
    address: string | null;
    /** 緯度 */
    lat?: number | null;
    /** 経度 */
    lon?: number | null;

    /** コンストラクタ */
    constructor(suggestion: SuggestionInterface) {
        this.id = suggestion.id;
        this.name = suggestion.name;
        this.address = suggestion.address;
        this.lat = suggestion.lat;
        this.lon = suggestion.lon;
    }
}

export { SuggestionDto, SuggestionInterface };