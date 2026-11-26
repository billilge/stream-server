package kr.ac.kookmin.stream.internal.domain.display.service;

import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayCalendarSchedule;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayCalendarScheduleCreateCommand;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayCalendarScheduleUpdateCommand;

/**
 * 운영진용 디스플레이 학사 일정 서비스. 일정 등록·수정·삭제를 맡는다. 조회는 학생 앱과 같은
 * {@link DisplayService#getSchedules}를 쓴다.
 */
public interface AdminDisplayCalendarScheduleService {

    // 같은 이름·같은 기간의 일정이 있어도 중복 확인 없이 저장한다
    DisplayCalendarSchedule create(DisplayCalendarScheduleCreateCommand command);

    DisplayCalendarSchedule update(Long scheduleId, DisplayCalendarScheduleUpdateCommand command);

    void delete(Long scheduleId);
}
