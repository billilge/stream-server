package kr.ac.kookmin.stream.internal.domain.schedule.service;

import java.time.LocalDate;
import java.util.List;
import kr.ac.kookmin.stream.internal.domain.schedule.domain.WorkSchedule;
import kr.ac.kookmin.stream.internal.domain.schedule.domain.WorkScheduleCommand;
import kr.ac.kookmin.stream.internal.domain.schedule.domain.WorkScheduleWeek;

/**
 * 운영진용 빌릴게 근무 시간표 서비스. 주 단위 조회와 근무 시간 등록·수정·삭제를 맡는다.
 * 근무자로 배정할 수 있는 회원인지는 회원 도메인을 함께 보는 호출부(UseCase)가 확인한다.
 */
public interface AdminWorkScheduleService {

    // date가 속한 주(월~금)의 근무 시간표. date가 null이면 이번 주(KST)
    WorkScheduleWeek getWeek(LocalDate date);

    // 근무자 한 명의 근무 시간을 한 번에 등록한다. 하나라도 규칙에 맞지 않으면 아무것도 저장하지 않는다
    List<WorkSchedule> register(Long memberId, List<WorkScheduleCommand> commands);

    // 근무 시간 1건의 근무자·근무일·시간을 모두 바꾼다
    WorkSchedule update(Long scheduleId, Long memberId, WorkScheduleCommand command);

    void delete(Long scheduleId);
}
