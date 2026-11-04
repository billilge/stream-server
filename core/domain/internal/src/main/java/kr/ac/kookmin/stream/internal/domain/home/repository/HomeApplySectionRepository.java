package kr.ac.kookmin.stream.internal.domain.home.repository;

import java.util.List;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeApplySection;

public interface HomeApplySectionRepository {

    /**
     * 노출로 설정된 신청 섹션을 표시 순서대로 조회한다.
     */
    List<HomeApplySection> findExposed();
}
