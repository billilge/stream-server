package kr.ac.kookmin.stream.db.member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import kr.ac.kookmin.stream.db.common.BaseTimeEntity;
import kr.ac.kookmin.stream.member.domain.member.domain.MemberTermAgreement;
import kr.ac.kookmin.stream.member.domain.member.domain.TermType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "member_term_agreements",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_member_term_agreements_member_id_term_type",
            columnNames = {"member_id", "term_type"}
        )
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberTermAgreementJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_term_agreement_id")
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(name = "term_type", nullable = false, length = 30)
    private TermType termType;

    @Column(name = "term_version", nullable = false, length = 20)
    private String termVersion;

    @Column(nullable = false)
    private boolean agreed;

    @Column(name = "agreed_at")
    private LocalDateTime agreedAt;

    private MemberTermAgreementJpaEntity(MemberTermAgreement agreement) {
        this.id = agreement.getId();
        this.memberId = agreement.getMemberId();
        this.termType = agreement.getTermType();
        this.termVersion = agreement.getTermVersion();
        this.agreed = agreement.isAgreed();
        this.agreedAt = agreement.getAgreedAt();
    }

    public static MemberTermAgreementJpaEntity from(MemberTermAgreement agreement) {
        return new MemberTermAgreementJpaEntity(agreement);
    }

    public MemberTermAgreement toDomain() {
        return MemberTermAgreement.of(id, memberId, termType, termVersion, agreed, agreedAt);
    }
}
