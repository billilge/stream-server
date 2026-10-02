package kr.ac.kookmin.stream.member.domain.notification.domain;

import java.util.List;

/**
 * 토큰별 발송 결과. 멤버와의 매핑은 토큰을 넘긴 호출 측이 갖는다.
 * INVALID_TOKEN 정리는 memberId가 아니라 토큰 값으로 해야 발송 이후 새로 등록된 토큰을 지우지 않는다.
 */
public record PushSendResult(List<PushSendOutcome> outcomes) {

    public static PushSendResult of(List<String> tokens, PushSendStatus status) {
        return new PushSendResult(tokens.stream()
            .map(token -> new PushSendOutcome(token, status))
            .toList());
    }

    public List<String> tokensWith(PushSendStatus status) {
        return outcomes.stream()
            .filter(outcome -> outcome.status() == status)
            .map(PushSendOutcome::token)
            .toList();
    }
}
