package kr.ac.kookmin.stream.internal.domain.display.service;

import java.util.List;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayDailySchedule;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayPoster;

/**
 * 오프라인 디스플레이 화면용 조회 서비스. 로그인 없이 부르는 공개 화면 조회가 쓴다.
 * 포스터·학사 일정 관리는 {@link AdminDisplayPosterService}, {@link AdminDisplayCalendarScheduleService}가 맡는다.
 */
public interface DisplayService {

    // 활성 포스터를 등록 순으로
    List<DisplayPoster> getActivePosters();

    // 오늘(KST) 앞뒤 3일, 7일치 학사 일정을 날짜 오름차순으로. 일정이 없는 날도 빈 목록으로 넣어 항상 7개다
    List<DisplayDailySchedule> getDailySchedulesAroundToday();
}
