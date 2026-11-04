package kr.ac.kookmin.stream.internal.domain.home.repository;

import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBanner;

public interface HomeBannerRepository {

    /**
     * {@code at}이 노출 기간 안인 배너를 표시 순서대로 조회한다. 삭제한 배너는 뺀다.
     */
    List<HomeBanner> findExposedAt(LocalDateTime at);
}
