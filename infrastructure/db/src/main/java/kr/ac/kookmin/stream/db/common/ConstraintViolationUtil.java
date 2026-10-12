package kr.ac.kookmin.stream.db.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * DB 제약 위반 예외 판별 유틸.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConstraintViolationUtil {

    /** MySQL은 제약 이름 앞에 테이블명을 붙여 줄 수 있어 포함 여부로 비교한다. */
    public static boolean isViolated(DataIntegrityViolationException e, String constraintName) {
        return e.getCause() instanceof ConstraintViolationException violation
            && violation.getConstraintName() != null
            && violation.getConstraintName().contains(constraintName);
    }
}
