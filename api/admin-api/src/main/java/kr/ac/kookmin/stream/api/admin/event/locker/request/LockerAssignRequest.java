package kr.ac.kookmin.stream.api.admin.event.locker.request;

import jakarta.validation.constraints.NotNull;

/**
 * 운영진이 학생에게 사물함을 직접 배정한다.
 * <p>
 * 회원은 식별자로 받는다. 운영진 화면이 학생회비 납부자 목록을 이름·학번으로 검색해 고르는 구조라, 거기서 받은
 * 식별자를 그대로 넘기면 된다.
 */
public record LockerAssignRequest(

    @NotNull(message = "사물함 운영 회차를 입력해 주세요.")
    Long lockerPeriodId,

    @NotNull(message = "배정할 학생을 선택해 주세요.")
    Long memberId,

    @NotNull(message = "배정할 사물함을 선택해 주세요.")
    Long lockerId
) {
}
