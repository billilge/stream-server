package kr.ac.kookmin.stream.internal.domain.display.service;

import java.time.LocalDate;
import java.util.List;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayCalendarSchedule;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayCalendarScheduleCreateCommand;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayCalendarScheduleUpdateCommand;

/**
 * 운영진용 디스플레이 학사 일정 서비스. 일정 조회·등록·수정·삭제를 맡는다.
 */
public interface AdminDisplayCalendarScheduleService {

    // 기간이 startDate~endDate와 하루라도 겹치는 일정. 관리자 화면은 한 달 범위로 부른다
    List<DisplayCalendarSchedule> getSchedules(LocalDate startDate, LocalDate endDate);

    // 같은 이름·같은 기간의 일정이 있어도 중복 확인 없이 저장한다
    DisplayCalendarSchedule create(DisplayCalendarScheduleCreateCommand command);

    DisplayCalendarSchedule update(Long scheduleId, DisplayCalendarScheduleUpdateCommand command);

    void delete(Long scheduleId);
}
