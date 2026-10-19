package kr.ac.kookmin.stream.member.domain.notification.domain;

import java.util.List;
import kr.ac.kookmin.stream.common.JosaUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 알림 종류와 문구. 제목은 문구표의 굵은 첫 문장, 본문은 둘째 문장이다. 한 문장짜리는 본문이 없다.
 * <p>
 * 문구의 {@code %s}에는 알림을 만들 때 넘긴 값 목록이 앞에서부터 들어간다. 제목과 본문은 같은 값 목록을 각각 처음부터 쓴다.
 * 조사는 "을(를)"처럼 적어 두면 앞 단어의 받침에 맞춰 바뀐다({@link JosaUtil}).
 * 상수 이름이 {@code notifications.type}에 저장되므로 이름을 바꾸면 쌓인 알림함 행을 읽지 못한다.
 */
@Getter
@AllArgsConstructor
public enum NotificationType {

    // 값: 행사명
    EVENT_APPLIED(NotificationCategory.EVENT, "'%s'을(를) 신청했어요.", "신청 내역에서 자세한 내용을 확인할 수 있어요."),
    EVENT_APPLICATION_CANCELED(NotificationCategory.EVENT, "'%s' 참여 신청을 취소했어요.", null),
    EVENT_STARTING_TOMORROW(NotificationCategory.EVENT, "'%s'이(가) 내일 열려요.", "행사 일정과 장소를 확인해 주세요."),
    EVENT_APPLY_CLOSING(NotificationCategory.EVENT, "'%s' 신청이 곧 마감돼요.", "참여하려면 기간 내에 신청해 주세요."),

    // 값: 없음
    LOCKER_ASSIGNED(NotificationCategory.LOCKER, "사물함을 신청했어요.", "배정된 사물함 번호를 확인해 주세요."),
    LOCKER_APPLY_CLOSING(NotificationCategory.LOCKER, "사물함 신청이 내일 마감돼요.", "이용하려면 기간 내에 신청해 주세요."),
    LOCKER_USAGE_ENDING(NotificationCategory.LOCKER, "사물함 이용 기간이 얼마 남지 않았어요.", "만료 전에 개인 물품을 미리 정리해 주세요."),

    // 값: 물품명
    RENTAL_DUE_SOON(NotificationCategory.BILLILGE, "반납까지 1시간 남았어요.", "늦지 않게 %s을(를) 반납해 주세요."),
    RENTAL_OVERDUE(NotificationCategory.BILLILGE, "반납 기한이 지났어요.", "학생회실에 방문해 %s을(를) 반납해 주세요."),

    // 값: 없음
    NOTICE_CREATED(NotificationCategory.NOTICE, "새로운 공지사항이 올라왔어요.", "자세한 내용을 확인해 주세요.");

    private final NotificationCategory category;
    private final String title;
    private final String body;

    public String formatTitle(List<String> formatValues) {
        return JosaUtil.format(title, formatValues);
    }

    // 본문이 없는 알림은 null
    public String formatBody(List<String> formatValues) {
        return body == null ? null : JosaUtil.format(body, formatValues);
    }
}
