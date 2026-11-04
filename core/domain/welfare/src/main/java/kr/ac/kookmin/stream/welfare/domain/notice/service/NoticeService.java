package kr.ac.kookmin.stream.welfare.domain.notice.service;

import java.util.Collection;
import java.util.List;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.common.PreviewResult;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.Notice;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCategory;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCursor;

/**
 * 공지 조회. 학생 앱이 쓰고, 운영진도 상세 조회에 같은 API를 쓴다. 운영진의 목록 조회·작성·수정·삭제는 {@link AdminNoticeService}가 맡는다.
 */
public interface NoticeService {
    /**
     * @param keyword 제목 검색어. null이거나 공백뿐이면 검색하지 않는다
     */
    CursorSliceResult<Notice> getNotices(NoticeCategory category, String keyword, NoticeCursor cursor, int size);

    /**
     * 통합 검색의 공지 섹션. 제목에 검색어가 들어 있는 공지를 목록({@link #getNotices})과 같은 순서로 앞에서
     * {@code limit}건, 그리고 그 전체 수를 돌려준다. 종류는 가리지 않는다.
     *
     * @param keyword 제목 검색어. null이거나 공백뿐이면 검색하지 않는다
     */
    PreviewResult<Notice> searchPreview(String keyword, int limit);

    Notice getById(Long id);

    /**
     * 공지 한 건. 없거나 삭제됐으면 {@code null}이다.
     * <p>
     * 다른 화면이 공지를 곁들여 보여줄 때(운영진 배너 상세의 연결 공지) 쓴다. 공지가 사라졌다고 그 화면까지
     * 404가 되면 안 되므로 {@link #getById(Long)}와 달리 던지지 않는다.
     */
    Notice getByIdOrNull(Long id);

    /**
     * 삭제되지 않은 공지 중 식별자에 해당하는 것. 순서는 보장하지 않는다.
     * <p>
     * 여러 화면이 공지를 곁들여 보여줄 때(홈 배너의 연결 공지) 그 공지가 아직 있는지 한 번에 확인하는 데
     * 쓴다. 없는 식별자는 그냥 빠지므로 {@link #getById(Long)}와 달리 던지지 않는다.
     */
    List<Notice> getByIds(Collection<Long> ids);
}
