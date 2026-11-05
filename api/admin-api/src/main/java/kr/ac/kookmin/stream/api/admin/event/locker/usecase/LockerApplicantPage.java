package kr.ac.kookmin.stream.api.admin.event.locker.usecase;

import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerApplicationCounts;

// AdminLockerApplicationSearchUseCase가 조합한 결과. 개수는 검색어·상태 필터와 무관한 회차 전체 수라
// 목록과 따로 담는다.
public record LockerApplicantPage(LockerApplicationCounts counts, PageResult<MemberLockerApplication> applications) {
}
