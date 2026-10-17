package kr.ac.kookmin.stream.event.domain.event.domain;

import java.util.List;

/**
 * 운영진 행사 상세에 필요한 도메인 객체 묶음. 화면에 맞춘 값(모집 상태 등)은 담지 않고,
 * 응답을 만드는 쪽이 행사의 계산 메서드로 꺼낸다.
 *
 * @param questions      노출 순서대로 정렬된 신청서 질문
 * @param applicantCount 유효(APPLIED) 신청자 수
 */
public record EventManageDetail(
    Event event,
    List<EventQuestion> questions,
    long applicantCount
) {

    /** 행사 이미지 id. 이미지가 없으면 {@code null}이 아니라 빈 목록이다. */
    public List<Long> imageIds() {
        return event.getImageIds() == null ? List.of() : event.getImageIds();
    }
}
