package kr.ac.kookmin.stream.event.domain.event.domain;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 운영진 행사 상세. 수정 화면의 초기값으로 쓰므로 행사 전체와 신청서 질문을 그대로 담고,
 * 조회 시점의 신청자 수와 모집 상태를 덧붙인다.
 *
 * @param questions 노출 순서대로 정렬된 신청서 질문
 */
public record EventManageDetail(
    Event event,
    List<EventQuestion> questions,
    long applicantCount,
    RecruitStatus recruitStatus
) {

    public static EventManageDetail of(Event event, List<EventQuestion> questions, long applicantCount,
        LocalDateTime now) {
        return new EventManageDetail(event, questions, applicantCount, event.calculateRecruitStatus(now, applicantCount));
    }

    /** 행사 이미지 id. 이미지가 없으면 {@code null}이 아니라 빈 목록이다. */
    public List<Long> imageIds() {
        return event.getImageIds() == null ? List.of() : event.getImageIds();
    }
}
