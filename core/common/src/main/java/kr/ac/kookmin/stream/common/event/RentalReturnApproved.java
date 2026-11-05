package kr.ac.kookmin.stream.common.event;

/** 운영진이 반납 신청을 승인했다. */
public record RentalReturnApproved(Long memberId, Long rentalHistoryId, String itemName) implements DomainEvent {}
