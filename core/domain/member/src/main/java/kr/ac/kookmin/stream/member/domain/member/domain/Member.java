package kr.ac.kookmin.stream.member.domain.member.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Member {

    private Long id;
    private String studentId;
    private String name;

    public static Member of(Long id, String studentId, String name) {
        return new Member(id, studentId, name);
    }
}
