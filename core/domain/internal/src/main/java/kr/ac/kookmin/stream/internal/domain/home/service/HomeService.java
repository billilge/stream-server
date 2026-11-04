package kr.ac.kookmin.stream.internal.domain.home.service;

import java.util.List;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeApplySection;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBanner;

/**
 * 홈 화면의 배너와 신청 섹션 노출 설정 조회.
 * <p>
 * 설정만 돌려준다. 배너의 연결 공지·행사가 아직 있는지, 신청 섹션이 가리키는 행사·사물함 회차가 아직
 * 신청을 받는지는 각 도메인이 알고 있어, 둘을 맞추는 일은 {@code api:app-api}의 홈 UseCase가
 * 한다({@code architecture.md} 6-1절).
 */
public interface HomeService {

    /**
     * 지금 노출 기간 안인 배너를 표시 순서대로 조회한다. 랜딩 값이 어긋난 배너는 뺀다.
     * <p>
     * 연결 대상이 지워졌거나 아직 미게시인 배너는 여기서 걸러지지 않는다. 부르는 쪽이 대상을 읽어 거른다.
     */
    List<HomeBanner> getExposedBanners();

    /**
     * 노출로 설정된 신청 섹션을 표시 순서대로 조회한다.
     */
    List<HomeApplySection> getExposedApplySections();
}
