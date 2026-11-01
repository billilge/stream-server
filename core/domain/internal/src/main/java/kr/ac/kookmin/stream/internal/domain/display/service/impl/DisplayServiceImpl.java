package kr.ac.kookmin.stream.internal.domain.display.service.impl;

import java.time.LocalDate;
import java.util.List;
import kr.ac.kookmin.stream.common.DateUtil;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayCalendarSchedule;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayDailySchedule;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayPoster;
import kr.ac.kookmin.stream.internal.domain.display.repository.DisplayCalendarScheduleRepository;
import kr.ac.kookmin.stream.internal.domain.display.repository.DisplayPosterRepository;
import kr.ac.kookmin.stream.internal.domain.display.service.DisplayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class DisplayServiceImpl implements DisplayService {

    // 화면에 오늘 앞뒤로 며칠을 보여줄지. 레거시 디스플레이와 같은 값이다
    private static final int DAYS_AROUND_TODAY = 3;

    private final DisplayPosterRepository displayPosterRepository;
    private final DisplayCalendarScheduleRepository displayCalendarScheduleRepository;

    @Override
    public List<DisplayPoster> getActivePosters() {
        return displayPosterRepository.findAllActive();
    }

    @Override
    public List<DisplayDailySchedule> getDailySchedulesAroundToday() {
        LocalDate today = LocalDate.now(DateUtil.KST);
        LocalDate startDate = today.minusDays(DAYS_AROUND_TODAY);
        LocalDate endDate = today.plusDays(DAYS_AROUND_TODAY);
        List<DisplayCalendarSchedule> schedules = displayCalendarScheduleRepository.findAllOverlapping(startDate, endDate);

        return startDate.datesUntil(endDate.plusDays(1))
            .map(date -> new DisplayDailySchedule(date, titlesOn(schedules, date)))
            .toList();
    }

    // schedules가 시작일 → 등록 순이라 그날의 일정명도 그 순서를 따른다
    private List<String> titlesOn(List<DisplayCalendarSchedule> schedules, LocalDate date) {
        return schedules.stream()
            .filter(schedule -> schedule.includes(date))
            .map(DisplayCalendarSchedule::getTitle)
            .toList();
    }
}
