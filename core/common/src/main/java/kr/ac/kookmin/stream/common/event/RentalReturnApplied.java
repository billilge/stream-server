package kr.ac.kookmin.stream.common.event;

/** 회원이 반납을 신청했다. */
public record RentalReturnApplied(
    Long memberId, Long rentalHistoryId, String itemName
) implements DomainEvent {}
