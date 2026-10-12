package kr.ac.kookmin.stream.member.domain.member.domain;

/**
 * 로그인 provider가 준 회원 프로필. major·academicStatus는 원문이며, 학부 변환은 {@link Department#fromMajor}가 한다.
 */
public record MemberProfileCommand(
    String studentId,
    String name,
    String major,
    String academicStatus
) {}
