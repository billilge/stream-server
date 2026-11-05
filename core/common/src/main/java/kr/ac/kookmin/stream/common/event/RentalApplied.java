package kr.ac.kookmin.stream.common.event;

/** 회원이 물품 대여를 신청했다. 물품명·대여 시각은 알림 문구에 쓰려고 신청 시점의 값을 싣는다. */
public record RentalApplied(
    Long memberId, Long rentalHistoryId, String itemName, String rentAtTime
) implements DomainEvent {}
