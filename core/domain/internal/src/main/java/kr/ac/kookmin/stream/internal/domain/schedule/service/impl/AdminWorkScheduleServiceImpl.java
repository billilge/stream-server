package kr.ac.kookmin.stream.internal.domain.schedule.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.DateUtil;
import kr.ac.kookmin.stream.internal.domain.schedule.domain.WorkSchedule;
import kr.ac.kookmin.stream.internal.domain.schedule.domain.WorkScheduleCommand;
import kr.ac.kookmin.stream.internal.domain.schedule.domain.WorkScheduleErrorCode;
import kr.ac.kookmin.stream.internal.domain.schedule.domain.WorkScheduleWeek;
import kr.ac.kookmin.stream.internal.domain.schedule.domain.WorkWeek;
import kr.ac.kookmin.stream.internal.domain.schedule.repository.WorkScheduleRepository;
import kr.ac.kookmin.stream.internal.domain.schedule.service.AdminWorkScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class AdminWorkScheduleServiceImpl implements AdminWorkScheduleService {

    private final WorkScheduleRepository workScheduleRepository;

    @Override
    public WorkScheduleWeek getWeek(LocalDate date) {
        LocalDate today = LocalDate.now(DateUtil.KST);
        WorkWeek week = WorkWeek.from(date == null ? today : date);
        List<WorkSchedule> schedules = workScheduleRepository.findAllByWorkDateBetween(
            week.startDate(), week.endDate());
        return new WorkScheduleWeek(week, !week.isPastAt(today), schedules);
    }

    @Override
    @Transactional
    public List<WorkSchedule> register(Long memberId, List<WorkScheduleCommand> commands) {
        LocalDate today = LocalDate.now(DateUtil.KST);
        List<WorkSchedule> schedules = commands.stream()
            .map(command -> WorkSchedule.create(memberId, command))
            .toList();
        schedules.forEach(schedule -> validateNotPastWeek(schedule.getWorkDate(), today));
        validateNotConflicted(schedules);

        return workScheduleRepository.saveAll(schedules);
    }

    // 지난 주 근무 시간을 이번 주로 옮기는 것도, 이번 주 근무 시간을 지난 주로 옮기는 것도 막으려고 옮기기 전과 후를 둘 다 본다
    @Override
    @Transactional
    public WorkSchedule update(Long scheduleId, Long memberId, WorkScheduleCommand command) {
        LocalDate today = LocalDate.now(DateUtil.KST);
        WorkSchedule schedule = getById(scheduleId);
        validateNotPastWeek(schedule.getWorkDate(), today);

        schedule.change(memberId, command);
        validateNotPastWeek(schedule.getWorkDate(), today);
        validateNotConflicted(List.of(schedule));

        return workScheduleRepository.save(schedule);
    }

    @Override
    @Transactional
    public void delete(Long scheduleId) {
        WorkSchedule schedule = getById(scheduleId);
        validateNotPastWeek(schedule.getWorkDate(), LocalDate.now(DateUtil.KST));
        workScheduleRepository.deleteById(scheduleId);
    }

    private void validateNotPastWeek(LocalDate workDate, LocalDate today) {
        if (WorkWeek.from(workDate).isPastAt(today)) {
            throw new BusinessException(WorkScheduleErrorCode.PAST_WEEK_NOT_EDITABLE);
        }
    }

    // 한 시간 칸에는 근무자 한 명만 들어간다. 저장된 근무 시간 위에 요청한 근무 시간을 하나씩 올려 보며
    // 먼저 올라간 것(저장된 것·앞선 요청)과 겹치면 막는다. 근무자가 달라도 해당한다.
    // 구간 겹침은 유니크 제약으로 막을 수 없어 저장 전에 확인한다(조회-후-삽입이라 동시 등록 구멍은 남는다)
    private void validateNotConflicted(List<WorkSchedule> targets) {
        List<WorkSchedule> placed = new ArrayList<>(findSavedSchedulesOnSameDates(targets));
        for (WorkSchedule target : targets) {
            if (placed.stream().anyMatch(target::overlaps)) {
                throw new BusinessException(WorkScheduleErrorCode.WORK_SCHEDULE_CONFLICT);
            }
            placed.add(target);
        }
    }

    // 대상 근무일에 이미 저장된 근무 시간. 수정 대상의 원래 행은 자기 자신과 겹쳐 보이므로 뺀다
    private List<WorkSchedule> findSavedSchedulesOnSameDates(List<WorkSchedule> targets) {
        Set<LocalDate> workDates = targets.stream()
            .map(WorkSchedule::getWorkDate)
            .collect(Collectors.toSet());
        Set<Long> targetIds = targets.stream()
            .map(WorkSchedule::getId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        return workScheduleRepository.findAllByWorkDateIn(workDates).stream()
            .filter(saved -> !targetIds.contains(saved.getId()))
            .toList();
    }

    private WorkSchedule getById(Long scheduleId) {
        return workScheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new BusinessException(WorkScheduleErrorCode.WORK_SCHEDULE_NOT_FOUND));
    }
}
