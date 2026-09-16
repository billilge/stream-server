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
}
