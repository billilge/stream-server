package kr.ac.kookmin.stream.common.event;

import java.time.LocalDateTime;

/**
 * 회원의 학생회비 납부가 확인되었다 — 운영진이 회원의 최신 납부 요청을 납부완료로 처리했다.
 * occurredAt은 같은 회원의 납부·납부 취소가 재시도로 순서가 뒤바뀌어 도착할 때 늦은 쪽을 가려내는 데 쓴다.
 */
public record MemberFeePaid(Long memberId, Long studentTransferStatusId, LocalDateTime occurredAt)
    implements DomainEvent {}
