package kr.ac.kookmin.stream.api.admin.internal.schedule.usecase;

import java.util.List;
import kr.ac.kookmin.stream.internal.domain.schedule.domain.WorkWeek;

// 한 주의 근무 시간표에 근무자를 붙인 결과.
public record MemberWorkScheduleWeek(WorkWeek week, boolean editable, List<MemberWorkSchedule> schedules) {
}
