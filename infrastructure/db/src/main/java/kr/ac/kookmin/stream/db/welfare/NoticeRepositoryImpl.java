package kr.ac.kookmin.stream.db.welfare;

import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.Notice;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCategory;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCursor;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeErrorCode;
import kr.ac.kookmin.stream.welfare.domain.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
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

    // 낙관적 락 충돌은 커밋할 때 터지면 이 try/catch 밖이라 변환할 곳이 없다. 그래서 UPDATE를 여기서 바로 보낸다
    // (INSERT에는 WHERE가 없어 충돌하지 않는다). 재시도하지 않고 바로 알리는 이유는 NoticeJpaEntity 주석 참고
    @Override
    public Notice save(Notice notice) {
        try {
            return noticeJpaRepository.saveAndFlush(NoticeJpaEntity.from(notice)).toDomain();
        } catch (OptimisticLockingFailureException e) {
            throw new BusinessException(NoticeErrorCode.NOTICE_UPDATE_CONFLICT);
        }
    }

    @Override
    public boolean delete(Long id) {
        return noticeJpaRepository.findByIdAndIsDeletedFalse(id)
            .map(entity -> {
                entity.delete();
                try {
                    noticeJpaRepository.flush();
                } catch (OptimisticLockingFailureException e) {
                    // 읽은 뒤 UPDATE 전에 다른 요청이 먼저 삭제했다. 순차 요청과 같게 없는 공지로 다룬다
                    return false;
                }
                return true;
            })
            .orElse(false);
    }
}
