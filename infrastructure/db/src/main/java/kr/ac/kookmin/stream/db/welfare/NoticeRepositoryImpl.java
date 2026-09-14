package kr.ac.kookmin.stream.db.welfare;

import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.Notice;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCategory;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCursor;
import kr.ac.kookmin.stream.welfare.domain.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NoticeRepositoryImpl implements NoticeRepository {

    private final NoticeJpaRepository noticeJpaRepository;

    @Override
    public CursorSliceResult<Notice> findAll(NoticeCategory category, NoticeCursor cursor, int size) {
        Pageable pageable = Pageable.ofSize(size + 1);
        List<NoticeJpaEntity> entities = cursor == null
            ? noticeJpaRepository.findFirstSlice(category, pageable)
            : noticeJpaRepository.findNextSlice(category, cursor.pinned(), cursor.createdAt(), cursor.id(), pageable);

        return CursorSliceResult.ofSlice(
            entities, size, NoticeJpaEntity::toDomain, notice -> NoticeCursor.of(notice, category).format()
        );
    }

    @Override
    public Optional<Notice> findById(Long id) {
        return noticeJpaRepository.findByIdAndIsDeletedFalse(id).map(NoticeJpaEntity::toDomain);
    }
}
