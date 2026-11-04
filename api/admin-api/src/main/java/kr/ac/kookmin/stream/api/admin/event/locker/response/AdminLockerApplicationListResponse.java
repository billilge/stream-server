package kr.ac.kookmin.stream.api.admin.event.locker.response;

import kr.ac.kookmin.stream.api.admin.event.locker.usecase.LockerApplicantPage;
import kr.ac.kookmin.stream.api.common.dto.PageResponse;

/**
 * 운영진 신청 현황 화면.
 *
 * @param totalApplicationCount 취소를 포함한 회차 전체 신청 수. 검색어·상태 필터와 상관없다
 * @param appliedCount          신청완료 수. 검색어·상태 필터와 상관없고, 취소 수는 전체에서 빼면 된다
 */
public record AdminLockerApplicationListResponse(
    long totalApplicationCount,
    long appliedCount,
    PageResponse<AdminLockerApplicationSearchResponse> applications
) {

    public static AdminLockerApplicationListResponse from(LockerApplicantPage page) {
        return new AdminLockerApplicationListResponse(
            page.counts().total(),
            page.counts().applied(),
            PageResponse.from(page.applications(), applicant -> AdminLockerApplicationSearchResponse.of(
                applicant.result().application(), applicant.result().locker(), applicant.member()))
        );
    }
}
