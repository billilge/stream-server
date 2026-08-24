package kr.ac.kookmin.stream.common;

/**
 * 학생회 부서. ADMIN에게만 부여되며, member 도메인의 학부(Department)와는 다른 개념이다.
 */
public enum CouncilDepartment {
    PRESIDENCY,       // 회장단
    EXECUTIVE,        // 집행부
    GENERAL_AFFAIRS,  // 총무부
    PLANNING,         // 기획부
    PR,               // 홍보부
    MEDIA,            // 미디어부
    WELFARE,          // 복지부
    COMMUNICATION     // 소통부
}
