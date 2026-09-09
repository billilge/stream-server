package kr.ac.kookmin.stream.db.welfare;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.Notice;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCategory;
import kr.ac.kookmin.stream.welfare.domain.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NoticeRepositoryImpl implements NoticeRepository {

    private static final String CURSOR_JOIN = "|";
    private static final String CURSOR_SPLIT_REGEX = "\\|";

    private final NoticeJpaRepository noticeJpaRepository;

    @Override
    public CursorSliceResult<Notice> findAll(NoticeCategory category, String cursor, int size) {
        String categoryName = category == null ? null : category.name();
        List<NoticeJpaEntity> entities = cursor == null
            ? noticeJpaRepository.findFirstSlice(categoryName, size + 1)
            : findNextSlice(categoryName, cursor, size + 1);

        boolean hasNext = entities.size() > size;
        List<Notice> content = entities.stream()
            .limit(size)
            .map(NoticeJpaEntity::toDomain)
            .toList();
        String nextCursor = hasNext ? encodeCursor(content.get(content.size() - 1)) : null;

        return new CursorSliceResult<>(content, hasNext, nextCursor);
    }

    @Override
    public Optional<Notice> findById(Long id) {
        return noticeJpaRepository.findByIdAndIsDeletedFalse(id).map(NoticeJpaEntity::toDomain);
    }

    private List<NoticeJpaEntity> findNextSlice(String categoryName, String cursor, int limit) {
        String[] parts = decodeCursor(cursor);
        boolean cursorPinned = Boolean.parseBoolean(parts[0]);
        LocalDateTime cursorCreatedAt = LocalDateTime.parse(parts[1]);
        Long cursorId = Long.valueOf(parts[2]);
        return noticeJpaRepository.findNextSlice(categoryName, cursorPinned, cursorCreatedAt, cursorId, limit);
    }

    private String encodeCursor(Notice notice) {
        String raw = notice.isPinned() + CURSOR_JOIN + notice.getCreatedAt() + CURSOR_JOIN + notice.getId();
        return Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    private String[] decodeCursor(String cursor) {
        return new String(Base64.getDecoder().decode(cursor), StandardCharsets.UTF_8).split(CURSOR_SPLIT_REGEX);
    }
}
