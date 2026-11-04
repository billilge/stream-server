package kr.ac.kookmin.stream.common.event;

/** 회원이 자신의 대여 신청(승인 전)을 취소했다. 운영진 공용 알림에만 쓰인다. */
public record RentalApplicationCanceled(
    Long memberId, Long rentalHistoryId, String itemName
) implements DomainEvent {}
