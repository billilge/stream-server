package kr.ac.kookmin.stream.internal.domain.display.service;

import java.util.List;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayCalendarSchedule;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayDailySchedule;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayPoster;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplaySchedulePeriod;

/**
 * 디스플레이 포스터·학사 일정 조회 서비스. 로그인 없이 부르는 디스플레이 화면 조회와, 학생 앱·운영진 화면의 학사 일정
 * 조회가 함께 쓴다. 포스터 관리와 학사 일정 등록·수정·삭제는 {@link AdminDisplayPosterService},
 * {@link AdminDisplayCalendarScheduleService}가 맡는다.
 */
public interface DisplayService {

    // 활성 포스터를 등록 순으로
    List<DisplayPoster> getActivePosters();

    // 오늘(KST) 앞뒤 3일, 7일치 학사 일정을 날짜 오름차순으로. 일정이 없는 날도 빈 목록으로 넣어 항상 7개다
    List<DisplayDailySchedule> getDailySchedulesAroundToday();

    // 기간이 period와 하루라도 겹치는 일정. 학생 앱은 한 주·한 달 달력 범위로, 관리자 화면은 한 달 범위로 부른다
    List<DisplayCalendarSchedule> getSchedules(DisplaySchedulePeriod period);
}
