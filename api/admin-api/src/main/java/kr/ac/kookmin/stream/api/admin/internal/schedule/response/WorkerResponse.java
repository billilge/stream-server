package kr.ac.kookmin.stream.api.admin.internal.schedule.response;

import kr.ac.kookmin.stream.member.domain.member.domain.Member;

public record WorkerResponse(Long memberId, String name, String studentId) {

    public static WorkerResponse from(Member member) {
        return new WorkerResponse(member.getId(), member.getName(), member.getStudentId());
    }
}
