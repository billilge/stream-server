package kr.ac.kookmin.stream.db.member;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.ac.kookmin.stream.db.common.BaseSoftDeleteEntity;
import kr.ac.kookmin.stream.member.Member;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberJpaEntity extends BaseSoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String studentNo;
    private String name;

    private MemberJpaEntity(Member member) {
        this.id = member.id();
        this.studentNo = member.studentNo();
        this.name = member.name();
    }

    public static MemberJpaEntity from(Member member) {
        return new MemberJpaEntity(member);
    }

    public Member toDomain() {
        return new Member(id, studentNo, name);
    }
}
