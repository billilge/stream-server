package kr.ac.kookmin.stream.event.domain.event.domain;

/**
 * 회원이 신청한, 아직 끝나지 않은 행사 한 건. 신청과 그 행사를 함께 담는 조회용 조합 객체다. 필드를 복사하지 않고
 * 도메인 객체를 그대로 담아, 화면이 어떤 필드를 쓰는지는 응답(api)이 고른다.
 * <p>
 * 신청을 함께 싣는 이유는 홈의 "신청한 행사"를 누르면 신청 식별자로 신청 내역 상세를 열어야 해서다.
 */
public record UpcomingAppliedEvent(EventApplication application, Event event) {
}
