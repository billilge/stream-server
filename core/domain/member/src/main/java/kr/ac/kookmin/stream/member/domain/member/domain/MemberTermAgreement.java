package kr.ac.kookmin.stream.member.domain.member.domain;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberTermAgreement {

    private Long id;
    private Long memberId;
    private TermType termType;
    private String termVersion;
    private boolean agreed;
    private LocalDateTime agreedAt;

    public static MemberTermAgreement of(
        Long id,
        Long memberId,
        TermType termType,
        String termVersion,
        boolean agreed,
        LocalDateTime agreedAt
    ) {
        return new MemberTermAgreement(id, memberId, termType, termVersion, agreed, agreedAt);
    }

    // 현재 약관 버전으로 동의 기록을 만든다. 동의하지 않았으면 동의 시각은 null이다
    public static MemberTermAgreement create(Long memberId, TermType termType, boolean agreed, LocalDateTime now) {
        return new MemberTermAgreement(null, memberId, termType, termType.version(), agreed, agreed ? now : null);
    }

    // 이미 기록이 있는 약관은 새 행을 만들지 않고 현재 버전으로 덮어쓴다. 버전과 동의 여부가 그대로면 최초 동의 시각을 지키도록 건너뛴다
    public void update(boolean agreed, LocalDateTime now) {
        if (termType.version().equals(this.termVersion) && this.agreed == agreed) {
            return;
        }
        this.termVersion = termType.version();
        this.agreed = agreed;
        this.agreedAt = agreed ? now : null;
    }
}
