package kr.ac.kookmin.stream.common.event;

/** 대여 물품의 반납 기한이 지났는데 아직 반납하지 않았다. 반납할 때까지 매일 기록된다. */
public record RentalOverdue(Long memberId, Long rentalHistoryId, String itemName) implements DomainEvent {}
