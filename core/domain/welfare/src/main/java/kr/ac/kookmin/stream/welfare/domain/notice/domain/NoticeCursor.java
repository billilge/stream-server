package kr.ac.kookmin.stream.welfare.domain.notice.domain;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.common.BusinessException;

public record NoticeCursor(boolean pinned, LocalDateTime createdAt, Long id, NoticeCategory category) {

    private static final String JOIN = "|";
    private static final String SPLIT_REGEX = "\\|";
    private static final String NO_CATEGORY = "-";

    public static NoticeCursor of(Notice notice, NoticeCategory category) {
        return new NoticeCursor(notice.isPinned(), notice.getCreatedAt(), notice.getId(), category);
    }

    // Base64 인코딩은 웹(Controller) 계층 책임이라 여기서는 순수 문자열 표현만 다룬다
    public static NoticeCursor from(String raw) {
        String[] parts = raw.split(SPLIT_REGEX, -1);
        if (parts.length != 4) {
            throw new BusinessException(NoticeErrorCode.NOTICE_INVALID_CURSOR);
        }
        try {
            boolean pinned = Boolean.parseBoolean(parts[0]);
            LocalDateTime createdAt = LocalDateTime.parse(parts[1]);
            Long id = Long.valueOf(parts[2]);
            NoticeCategory category = NO_CATEGORY.equals(parts[3]) ? null : NoticeCategory.valueOf(parts[3]);
            return new NoticeCursor(pinned, createdAt, id, category);
        } catch (RuntimeException e) {
            throw new BusinessException(NoticeErrorCode.NOTICE_INVALID_CURSOR);
        }
    }

    public String format() {
        String categoryPart = category == null ? NO_CATEGORY : category.name();
        return pinned + JOIN + createdAt + JOIN + id + JOIN + categoryPart;
    }
}
