package kr.ac.kookmin.stream.welfare.domain.fee.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Payer {

    private Long id;
    private Long memberId;
    private String name;
    private String studentId;
    private String enrollmentYear;
    private boolean registered;

    // 확인요청이 PAID로 처리된 회원을 납부자 명부에 등록할 때 쓴다 — 이미 앱에 가입한 회원이라 registered는 항상 true
    public static Payer create(Long memberId, String name, String studentId, String enrollmentYear) {
        return new Payer(null, memberId, name, studentId, enrollmentYear, true);
    }

    public static Payer of(
        Long id,
        Long memberId,
        String name,
        String studentId,
        String enrollmentYear,
        boolean registered
    ) {
        return new Payer(id, memberId, name, studentId, enrollmentYear, registered);
    }
}
