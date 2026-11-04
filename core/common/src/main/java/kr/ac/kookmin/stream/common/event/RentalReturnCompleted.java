package kr.ac.kookmin.stream.common.event;

/** 반납이 완료됐다. 급하게 확인할 내용이 아니라 알림함에만 남기고 푸시는 보내지 않는다. */
public record RentalReturnCompleted(Long memberId, Long rentalHistoryId, String itemName) implements DomainEvent {}
