package kr.ac.kookmin.stream.common.event;

/** 운영진이 대여 신청을 승인했다. */
public record RentalApproved(Long memberId, Long rentalHistoryId, String itemName) implements DomainEvent {}
