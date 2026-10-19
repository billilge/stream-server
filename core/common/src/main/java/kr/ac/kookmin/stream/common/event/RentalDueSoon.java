package kr.ac.kookmin.stream.common.event;

/** 대여 물품의 반납 기한이 1시간 남았다. 물품명은 알림 문구에 쓰려고 기록 시점의 값을 싣는다. */
public record RentalDueSoon(Long memberId, Long rentalHistoryId, String itemName) implements DomainEvent {}
