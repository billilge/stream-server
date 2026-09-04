package kr.ac.kookmin.stream.member.domain.notification.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Notification {

    private Long id;
    private Long memberId;
    private String type;
    private String formatValues;
    private boolean read;

    public static Notification of(
        Long id,
        Long memberId,
        String type,
        String formatValues,
        boolean read
    ) {
        return new Notification(id, memberId, type, formatValues, read);
    }
}
