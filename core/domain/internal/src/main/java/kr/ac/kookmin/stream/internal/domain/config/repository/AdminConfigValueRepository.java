package kr.ac.kookmin.stream.internal.domain.config.repository;

import java.util.Optional;
import kr.ac.kookmin.stream.internal.domain.config.domain.AdminConfigValue;

public interface AdminConfigValueRepository {
    Optional<AdminConfigValue> findByKey(String configKey);

    // findByKey 후 save하는 방식은 두 트랜잭션이 동시에 "없음"을 읽으면 유니크 제약 위반으로
    // 한쪽이 실패할 수 있어, DB의 원자적 upsert로 대체한다.
    void upsert(String configKey, String configValue);
}
