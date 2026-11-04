package kr.ac.kookmin.stream.internal.domain.home.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeApplySection;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeApplyTarget;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeApplyTargetType;

public interface HomeApplySectionRepository {

    /**
     * 노출로 설정된 신청 섹션을 표시 순서대로 조회한다.
     */
    List<HomeApplySection> findExposed();

    /**
     * 대상 하나의 설정 행. 없으면 비어 있다.
     */
    Optional<HomeApplySection> findByTarget(HomeApplyTarget target);

    /**
     * 주어진 대상의 설정 행. 한 종류씩 조회한다 — {@code (target_type, target_id)} 짝 목록을 그대로
     * {@code IN}에 넘기는 문법은 JPQL이 받지 않는다.
     */
    List<HomeApplySection> findByTargetTypeAndTargetIds(
        HomeApplyTargetType targetType, Collection<Long> targetIds);

    /**
     * 설정 행의 표시 순서 중 가장 큰 값. 행이 없으면 비어 있다.
     */
    Optional<Integer> findMaxDisplayOrder();

    HomeApplySection save(HomeApplySection section);

    /**
     * 여러 설정을 한꺼번에 저장한다. 순서 일괄 변경에 쓴다.
     */
    void saveAll(List<HomeApplySection> sections);
}
