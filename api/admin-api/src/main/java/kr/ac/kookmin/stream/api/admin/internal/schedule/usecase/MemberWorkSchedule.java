package kr.ac.kookmin.stream.api.admin.internal.schedule.usecase;

import kr.ac.kookmin.stream.internal.domain.schedule.domain.WorkSchedule;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;

// 근무 시간에 근무자를 붙인 결과. DTO 변환은 Controller가 한다.
// member는 탈퇴(소프트 삭제)한 회원의 근무 시간이면 null일 수 있다.
public record MemberWorkSchedule(WorkSchedule schedule, Member member) {

    public String memberName() {
        return member == null ? null : member.getName();
    }

    public String memberStudentId() {
        return member == null ? null : member.getStudentId();
    }
}
