package io.github.taichi0373.benefit_map.config;

import org.seasar.doma.DomainConverters;

import io.github.taichi0373.benefit_map.domain.UUIDConverter;

/**
 * Doma用ドメインコンバーター設定
 * 外部ドメインクラスのコンバーターを登録する
 */
@DomainConverters({
    UUIDConverter.class
})
public class DomaDomainConverters {
}