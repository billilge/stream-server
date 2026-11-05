package kr.ac.kookmin.stream.common.event;

/** 운영진이 대여 신청을 반려했다. */
public record RentalRejected(Long memberId, Long rentalHistoryId, String itemName) implements DomainEvent {}
