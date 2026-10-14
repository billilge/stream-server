package kr.ac.kookmin.stream.member.domain.notification.domain;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 알림함의 알림 한 건. 문구는 저장하지 않고 종류와 문구 값으로 다시 만든다({@link NotificationType}).
 */
@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Notification {

    // 문구 값을 한 컬럼에 순서대로 이어 붙일 때 쓰는 구분자. 행사명에 쉼표가 들어갈 수 있어 쉼표 대신 단위 구분 문자(US)를 쓴다
    private static final String FORMAT_VALUE_DELIMITER = "\u001F";

    private Long id;
    private Long memberId;
    private NotificationType type;
    // 문구 값을 순서대로 이어 붙인 문자열. 값이 없으면 null
    private String formatValues;
    // 알림을 눌렀을 때 이동할 대상의 ID. 무엇의 ID인지는 type이 정한다
    private Long targetId;
    private boolean read;

    public static Notification create(Long memberId, NotificationType type, List<String> formatValues, Long targetId) {
        String joined = formatValues.isEmpty() ? null : String.join(FORMAT_VALUE_DELIMITER, formatValues);
        return new Notification(null, memberId, type, joined, targetId, false);
    }

    public static Notification of(
        Long id,
        Long memberId,
        NotificationType type,
        String formatValues,
        Long targetId,
        boolean read
    ) {
        return new Notification(id, memberId, type, formatValues, targetId, read);
    }

    public List<String> getFormatValueList() {
        return formatValues == null ? List.of() : List.of(formatValues.split(FORMAT_VALUE_DELIMITER, -1));
    }
}
