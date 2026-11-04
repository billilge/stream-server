package kr.ac.kookmin.stream.internal.domain.display.service.impl;

import java.time.LocalDate;
import java.util.List;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayCalendarSchedule;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayCalendarScheduleCreateCommand;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayCalendarScheduleUpdateCommand;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayErrorCode;
import kr.ac.kookmin.stream.internal.domain.display.repository.DisplayCalendarScheduleRepository;
import kr.ac.kookmin.stream.internal.domain.display.service.AdminDisplayCalendarScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class AdminDisplayCalendarScheduleServiceImpl implements AdminDisplayCalendarScheduleService {

    private final DisplayCalendarScheduleRepository displayCalendarScheduleRepository;

    @Override
    public List<DisplayCalendarSchedule> getSchedules(LocalDate startDate, LocalDate endDate) {
        validateSearchPeriod(startDate, endDate);
        return displayCalendarScheduleRepository.findAllOverlapping(startDate, endDate);
    }

    @Override
    @Transactional
    public DisplayCalendarSchedule create(DisplayCalendarScheduleCreateCommand command) {
        return displayCalendarScheduleRepository.save(DisplayCalendarSchedule.create(command));
    }

    @Override
    @Transactional
    public DisplayCalendarSchedule update(Long scheduleId, DisplayCalendarScheduleUpdateCommand command) {
        DisplayCalendarSchedule schedule = getScheduleById(scheduleId);
        schedule.update(command);
        return displayCalendarScheduleRepository.save(schedule);
    }

    @Override
    @Transactional
    public void delete(Long scheduleId) {
        getScheduleById(scheduleId);
        displayCalendarScheduleRepository.deleteById(scheduleId);
    }

    // startDate·endDate가 뒤바뀐 요청(예: 10/31~10/1)은 일정 기간과 같은 에러로 거절한다.
    // 그대로 조회하면 겹침 조건이 뒤집혀 범위 전체를 덮는 일정만 걸려 나온다
    private void validateSearchPeriod(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new BusinessException(DisplayErrorCode.INVALID_DISPLAY_SCHEDULE_PERIOD);
        }
    }

    private DisplayCalendarSchedule getScheduleById(Long scheduleId) {
        return displayCalendarScheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new BusinessException(DisplayErrorCode.DISPLAY_SCHEDULE_NOT_FOUND));
    }
}
