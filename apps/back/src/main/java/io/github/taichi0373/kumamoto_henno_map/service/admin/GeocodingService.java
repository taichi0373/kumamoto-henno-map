package io.github.taichi0373.kumamoto_henno_map.service.admin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.github.taichi0373.kumamoto_henno_map.dto.admin.GeocodingResultDto;
import io.github.taichi0373.kumamoto_henno_map.repository.dao.BenefitDao;
import io.github.taichi0373.kumamoto_henno_map.repository.dao.MunicipalityDao;
import io.github.taichi0373.kumamoto_henno_map.repository.entity.BenefitEntity;
import io.github.taichi0373.kumamoto_henno_map.repository.entity.MunicipalityEntity;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.location.LocationClient;
import software.amazon.awssdk.services.location.model.Place;
import software.amazon.awssdk.services.location.model.SearchForTextResult;
import software.amazon.awssdk.services.location.model.SearchPlaceIndexForTextRequest;
import software.amazon.awssdk.services.location.model.SearchPlaceIndexForTextResponse;

/**
 * ジオコーディングサービス
 * <p>
 * AWS Location Service を使用して、店舗特典（CATEGORY_CDが'BS'始まり）の
 * 店舗名からジオコーディングを行い、住所・緯度・経度をDBに保存する。
 * </p>
 */
@Service
public class GeocodingService {

    private static final Logger log = LoggerFactory.getLogger(GeocodingService.class);

    /** カテゴリコード（店舗特典） */
    private static final String CATEGORY_CD_BUSINESS = "BS001";

    /** APIコール間のスリープ時間（ミリ秒） */
    private static final long API_CALL_INTERVAL_MS = 200;

    /** 特典DAO */
    private final BenefitDao benefitDao;

    /** 自治体DAO */
    private final MunicipalityDao municipalityDao;

    /** AWSリージョン */
    @Value("${aws.region}")
    private String awsRegion;

    /** Place Index名 */
    @Value("${aws.location.place-index-name}")
    private String placeIndexName;

    /**
     * コンストラクタ
     *
     * @param benefitDao      特典DAO
     * @param municipalityDao 自治体DAO
     */
    public GeocodingService(BenefitDao benefitDao, MunicipalityDao municipalityDao) {
        this.benefitDao = benefitDao;
        this.municipalityDao = municipalityDao;
    }

    /**
     * 店舗特典のジオコーディングを実行する
     * <p>
     * CATEGORY_CDが'BS001'の全特典レコードに対して、
     * AWS Location Serviceでジオコーディングを行い、住所・緯度・経度を更新する。
     * </p>
     *
     * @param skipExisting 既に座標保存済みのレコードをスキップするか（true: スキップ）
     * @return ジオコーディング結果DTO
     */
    public GeocodingResultDto geocode(boolean skipExisting) {
        List<BenefitEntity> targets = benefitDao.selectByCategoryCd(CATEGORY_CD_BUSINESS);
        log.info("ジオコーディング対象: {}件", targets.size());

        int successCount = 0;
        int skippedCount = 0;
        int failedCount = 0;
        List<String> errors = new ArrayList<>();

        try (LocationClient locationClient = buildLocationClient()) {
            for (BenefitEntity benefit : targets) {
                // 既に座標保存済みの場合はスキップ
                if (skipExisting && benefit.getLatitude() != null && benefit.getLongitude() != null) {
                    skippedCount++;
                    continue;
                }

                try {
                    // 自治体名を取得
                    String municipalityName = resolveMunicipalityName(benefit.getMunicipalityCd());

                    // 検索クエリを構築
                    String query = buildSearchQuery(municipalityName, benefit.getBenefitName());

                    // AWS Location Service でジオコーディング
                    SearchPlaceIndexForTextResponse response = locationClient.searchPlaceIndexForText(
                            SearchPlaceIndexForTextRequest.builder()
                                    .indexName(placeIndexName)
                                    .text(query)
                                    .maxResults(1)
                                    .filterCountries(List.of("JPN"))
                                    .build()
                    );

                    List<SearchForTextResult> results = response.results();
                    if (results.isEmpty()) {
                        failedCount++;
                        errors.add(benefit.getBenefitId() + ": 検索結果なし（クエリ: " + query + "）");
                        log.warn("ジオコーディング結果なし: {} ({})", benefit.getBenefitId(), query);
                        continue;
                    }

                    // 先頭の結果を取得
                    Place place = results.get(0).place();
                    List<Double> point = place.geometry().point();

                    // 経度=point[0], 緯度=point[1]
                    benefit.setLongitude(BigDecimal.valueOf(point.get(0)));
                    benefit.setLatitude(BigDecimal.valueOf(point.get(1)));
                    benefit.setAddress(place.label());

                    benefitDao.update(benefit);
                    successCount++;
                    log.info("ジオコーディング成功: {} -> ({}, {})",
                            benefit.getBenefitId(), benefit.getLatitude(), benefit.getLongitude());

                } catch (Exception e) {
                    failedCount++;
                    errors.add(benefit.getBenefitId() + ": " + e.getMessage());
                    log.error("ジオコーディングエラー: {}", benefit.getBenefitId(), e);
                }

                // レート制限対策
                sleep();
            }
        }

        log.info("ジオコーディング完了: 成功={}, スキップ={}, 失敗={}", successCount, skippedCount, failedCount);
        return new GeocodingResultDto(successCount, skippedCount, failedCount, errors);
    }

    /**
     * LocationClientを構築する
     *
     * @return LocationClient
     */
    private LocationClient buildLocationClient() {
        return LocationClient.builder()
                .region(Region.of(awsRegion))
                .build();
    }

    /**
     * 自治体コードから自治体名を取得する
     *
     * @param municipalityCd 自治体コード
     * @return 自治体名（取得できない場合は空文字）
     */
    private String resolveMunicipalityName(String municipalityCd) {
        if (municipalityCd == null) {
            return "";
        }
        MunicipalityEntity municipality = municipalityDao.selectById(municipalityCd);
        return municipality != null ? municipality.getMunicipalityName() : "";
    }

    /**
     * ジオコーディング用の検索クエリを構築する
     *
     * @param municipalityName 自治体名
     * @param benefitName      特典名（店舗名）
     * @return 検索クエリ
     */
    private String buildSearchQuery(String municipalityName, String benefitName) {
        StringBuilder sb = new StringBuilder("熊本県");
        if (!municipalityName.isEmpty()) {
            sb.append(" ").append(municipalityName);
        }
        if (benefitName != null && !benefitName.isEmpty()) {
            sb.append(" ").append(benefitName);
        }
        return sb.toString();
    }

    /**
     * レート制限対策のスリープ
     */
    private void sleep() {
        try {
            Thread.sleep(API_CALL_INTERVAL_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
