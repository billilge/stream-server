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
