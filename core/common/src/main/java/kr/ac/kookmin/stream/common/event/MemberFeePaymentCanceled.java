package kr.ac.kookmin.stream.common.event;

import java.time.LocalDateTime;

/**
 * 운영진이 회원의 납부완료를 정정했다 — 납부완료였던 최신 요청을 확인필요·납부확인중으로 바꿨다.
 * occurredAt의 쓰임은 {@link MemberFeePaid}와 같다.
 */
public record MemberFeePaymentCanceled(Long memberId, Long studentTransferStatusId, LocalDateTime occurredAt)
    implements DomainEvent {}
