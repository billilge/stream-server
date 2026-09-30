package kr.ac.kookmin.stream.member.domain.member.domain;

import java.util.Arrays;
import kr.ac.kookmin.stream.common.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * 학부. 학생회 부서(CouncilDepartment)와는 다른 개념이다.
 */
@Accessors(fluent = true)
@AllArgsConstructor
public enum Department {
    AI("인공지능전공", "인공지능학부"),
    SW("소프트웨어전공", "소프트웨어학부");

    // 로그인 provider가 주는 소속 문자열에서 찾는 전공명
    private final String majorName;

    // 응답에 내려주는 학부명
    @Getter
    private final String displayName;

    /** 소속 문자열을 학부로 바꾼다. 소프트웨어융합대학 전공이 아니면 가입할 수 없다. */
    public static Department fromMajor(String major) {
        return Arrays.stream(values())
            .filter(department -> major != null && major.contains(department.majorName))
            .findFirst()
            .orElseThrow(() -> new BusinessException(MemberErrorCode.DEPARTMENT_NOT_ALLOWED));
    }
}
