package kr.ac.kookmin.stream.welfare.domain.notice.service;

import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.Notice;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCategory;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCreateCommand;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeUpdateCommand;

/**
 * 운영진용 공지 서비스. 목록 조회·작성·수정·삭제를 맡고, 상세 조회는 운영진도 {@link NoticeService}를 쓴다.
 */
public interface AdminNoticeService {

    /**
     * 공지 목록. 종류({@code category})와 제목 검색어({@code keyword})로 거르고 고정 우선, 최신순으로 페이지 단위
     * 조회한다. 종류가 null이면 전체, 검색어가 null이거나 공백이면 거르지 않는다.
     */
    PageResult<Notice> search(NoticeCategory category, String keyword, PageOffset pageOffset);

    Notice create(Long createdBy, NoticeCreateCommand command);

    Notice update(Long id, NoticeUpdateCommand command);

    void delete(Long id);
}
