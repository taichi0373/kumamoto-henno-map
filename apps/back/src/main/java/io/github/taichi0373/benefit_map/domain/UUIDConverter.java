package io.github.taichi0373.benefit_map.domain;

import java.util.UUID;

import org.seasar.doma.ExternalDomain;
import org.seasar.doma.jdbc.domain.DomainConverter;

/**
 * UUID用の外部ドメインコンバーター
 * UUIDとString間の変換を行う
 */
@ExternalDomain
public class UUIDConverter implements DomainConverter<UUID, String> {

    @Override
    public String fromDomainToValue(UUID domain) {
        return domain != null ? domain.toString() : null;
    }

    @Override
    public UUID fromValueToDomain(String value) {
        if (value == null) {
            return null;
        }
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format: " + value, e);
        }
    }
}