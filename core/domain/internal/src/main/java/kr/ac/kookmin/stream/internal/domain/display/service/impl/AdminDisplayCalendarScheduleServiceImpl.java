package kr.ac.kookmin.stream.internal.domain.display.service.impl;

import java.util.List;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayCalendarSchedule;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayCalendarScheduleCreateCommand;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayCalendarScheduleUpdateCommand;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayErrorCode;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplaySchedulePeriod;
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
    public List<DisplayCalendarSchedule> getSchedules(DisplaySchedulePeriod period) {
        return displayCalendarScheduleRepository.findAllOverlapping(period);
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

    private DisplayCalendarSchedule getScheduleById(Long scheduleId) {
        return displayCalendarScheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new BusinessException(DisplayErrorCode.DISPLAY_SCHEDULE_NOT_FOUND));
    }
}
