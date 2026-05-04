package io.github.taichi0373.kumamoto_henno_map.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.taichi0373.kumamoto_henno_map.repository.dao.MunicipalityDao;
import io.github.taichi0373.kumamoto_henno_map.repository.entity.MunicipalityEntity;

/**
 * 市区町村サービス
 * <p>
 * 市区町村情報の取得に関するビジネスロジックを提供する。
 * </p>
 */
@Service
public class MunicipalityService {

    /**
     * 市区町村取得DAO
     */
    @Autowired
    private MunicipalityDao municipalityDao;
    
    /**
     * すべての市区町村情報を取得
     */
    public List<MunicipalityEntity> getMunicipality() {
        try {
            return municipalityDao.selectAllOrderByCd();
        } catch (Exception e) {
            return null;
        }
    }
}