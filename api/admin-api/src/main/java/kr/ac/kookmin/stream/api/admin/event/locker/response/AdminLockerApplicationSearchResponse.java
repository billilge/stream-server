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
 * @param lockerId    배정된 사물함 식별자. 취소된 신청이거나 철거된 사물함이면 {@code null}
 * @param lockerLabel 배정된 사물함 이름. {@code lockerId}와 같은 기준으로 비운다
 * @param canceled    운영진이 취소했는지. 기간이 끝났는지는 회차의 사용 종료일로 화면이 가른다
 */
public record AdminLockerApplicationSearchResponse(
    Long lockerApplicationId,
    Long memberId,
    String name,
    String studentId,
    Long lockerId,
    String lockerLabel,
    LocalDateTime appliedAt,
    boolean canceled
) {

    public static AdminLockerApplicationSearchResponse of(
        LockerApplication application, Locker locker, Member member
    ) {
        // 취소하면 그 칸은 바로 풀려 다른 학생이 가져갈 수 있다. 칸을 계속 달아두면 같은 칸이 두 줄에 뜬다
        Locker assigned = application.isCanceled() ? null : locker;

        return new AdminLockerApplicationSearchResponse(
            application.getId(),
            application.getMemberId(),
            member == null ? null : member.getName(),
            member == null ? null : member.getStudentId(),
            assigned == null ? null : assigned.getId(),
            assigned == null ? null : assigned.getLockerLabel(),
            application.getAppliedAt(),
            application.isCanceled()
        );
    }
}
