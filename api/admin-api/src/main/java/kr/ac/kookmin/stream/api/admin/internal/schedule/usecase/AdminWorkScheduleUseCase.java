package kr.ac.kookmin.stream.api.admin.internal.schedule.usecase;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.internal.domain.schedule.domain.WorkSchedule;
import kr.ac.kookmin.stream.internal.domain.schedule.domain.WorkScheduleCommand;
import kr.ac.kookmin.stream.internal.domain.schedule.domain.WorkScheduleErrorCode;
import kr.ac.kookmin.stream.internal.domain.schedule.domain.WorkScheduleWeek;
import kr.ac.kookmin.stream.internal.domain.schedule.service.AdminWorkScheduleService;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.member.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 근무 시간표 화면이 쓰는 조합. 근무 시간(internal)에 근무자(member)를 붙인다.
 * <p>
 * 근무자는 학생회 부서가 있는 회원이다. 회원 도메인은 학생회 부서가 있는지만 알려주고, 그 회원을 근무자로 볼지는
 * 근무 시간표의 규칙이라 이 UseCase에서만 정한다({@code getWorkers}, {@code getWorker}).
 * <p>
 * 근무자 확인은 읽기뿐이고 근무 시간 쓰기는 서비스 트랜잭션 하나로 끝나므로 트랜잭션을 걸지 않는다.
 */
@Component
@RequiredArgsConstructor
public class AdminWorkScheduleUseCase {

    private final AdminWorkScheduleService adminWorkScheduleService;
    private final MemberService memberService;

    public List<Member> getWorkers() {
        return memberService.findAllWithCouncilDepartment();
    }

    public MemberWorkScheduleWeek getWeek(LocalDate date) {
        WorkScheduleWeek week = adminWorkScheduleService.getWeek(date);
        Map<Long, Member> members = memberService.getMapByIds(
            week.schedules().stream().map(WorkSchedule::getMemberId).toList());

        return new MemberWorkScheduleWeek(
            week.week(),
            week.editable(),
            week.schedules().stream()
                .map(schedule -> new MemberWorkSchedule(schedule, members.get(schedule.getMemberId())))
                .toList()
        );
    }

    public List<MemberWorkSchedule> register(Long memberId, List<WorkScheduleCommand> commands) {
        Member worker = getWorker(memberId);
        return adminWorkScheduleService.register(memberId, commands).stream()
            .map(schedule -> new MemberWorkSchedule(schedule, worker))
            .toList();
    }

    public MemberWorkSchedule update(Long scheduleId, Long memberId, WorkScheduleCommand command) {
        Member worker = getWorker(memberId);
        return new MemberWorkSchedule(adminWorkScheduleService.update(scheduleId, memberId, command), worker);
    }

    // 없는 회원(탈퇴 포함)은 MEMBER_NOT_FOUND, 학생회 부서가 없는 회원은 WORKER_NOT_FOUND다
    private Member getWorker(Long memberId) {
        Member member = memberService.getById(memberId);
        if (!member.hasCouncilDepartment()) {
            throw new BusinessException(WorkScheduleErrorCode.WORKER_NOT_FOUND);
        }
        return member;
    }
}
