package kr.ac.kookmin.stream.welfare.domain.notice.domain;

import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.Cursor;

public record NoticeCursor(boolean pinned, LocalDateTime createdAt, Long id, NoticeCategory category)
    implements Cursor {

    private static final String JOIN = "|";
    private static final int PART_COUNT = 4;
    private static final String NO_CATEGORY = "-";

    public static NoticeCursor of(Notice notice, NoticeCategory category) {
        return new NoticeCursor(notice.isPinned(), notice.getCreatedAt(), notice.getId(), category);
    }

    public static NoticeCursor from(String raw) {
        try {
            List<String> parts = Cursor.parseParts(raw, PART_COUNT, NoticeErrorCode.NOTICE_INVALID_CURSOR);
            if (!parts.get(0).equals("true") && !parts.get(0).equals("false")) {
                throw new BusinessException(NoticeErrorCode.NOTICE_INVALID_CURSOR);
            }
            boolean pinned = Boolean.parseBoolean(parts.get(0));
            LocalDateTime createdAt = LocalDateTime.parse(parts.get(1));
            Long id = Long.valueOf(parts.get(2));
            NoticeCategory category = NO_CATEGORY.equals(parts.get(3)) ? null : NoticeCategory.valueOf(parts.get(3));
            return new NoticeCursor(pinned, createdAt, id, category);
        } catch (RuntimeException e) {
            throw new BusinessException(NoticeErrorCode.NOTICE_INVALID_CURSOR);
        }
    }

    @Override
    public String format() {
        String categoryPart = category == null ? NO_CATEGORY : category.name();
        return pinned + JOIN + createdAt + JOIN + id + JOIN + categoryPart;
    }
}
