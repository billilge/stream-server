package kr.ac.kookmin.stream.api.admin.event.locker.response;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.event.domain.locker.domain.Locker;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerApplication;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;

/**
 * 운영진 신청 현황의 한 줄.
 *
 * @param name        신청자 이름. 탈퇴한 회원의 지난 신청이면 {@code null}
 * @param studentId   신청자 학번. 탈퇴한 회원의 지난 신청이면 {@code null}
 * @param lockerLabel 배정된 사물함 이름. 철거된 사물함도 이름은 보여준다
 * @param canceled    운영진이 취소했는지. 기간이 끝났는지는 회차의 사용 종료일로 화면이 가른다
 */
public record AdminLockerApplicationSearchResponse(
    Long lockerApplicationId,
    Long memberId,
    String name,
    String studentId,
    String lockerLabel,
    LocalDateTime appliedAt,
    boolean canceled
) {

    public static AdminLockerApplicationSearchResponse of(
        LockerApplication application, Locker locker, Member member
    ) {
        return new AdminLockerApplicationSearchResponse(
            application.getId(),
            application.getMemberId(),
            member == null ? null : member.getName(),
            member == null ? null : member.getStudentId(),
            locker == null ? null : locker.getLockerLabel(),
            application.getAppliedAt(),
            application.isCanceled()
        );
    }
}
