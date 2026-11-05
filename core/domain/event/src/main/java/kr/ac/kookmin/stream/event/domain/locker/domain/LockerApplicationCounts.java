package kr.ac.kookmin.stream.event.domain.locker.domain;

/**
 * 운영진 신청 현황 상단의 개수. 검색어·상태 필터와 상관없이 회차 전체를 센다 — 필터를 건 결과의 건수는
 * 페이지 응답이 이미 내려준다.
 *
 * @param total   취소를 포함한 전체 신청 수
 * @param applied 취소되지 않은 신청 수. 취소 수는 {@code total - applied}다
 */
public record LockerApplicationCounts(long total, long applied) {
}
