package kr.ac.kookmin.stream.welfare.domain.notice.service;

import kr.ac.kookmin.stream.welfare.domain.notice.domain.Notice;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCreateCommand;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeUpdateCommand;

/**
 * 운영진용 공지 서비스. 작성·수정·삭제를 맡고, 목록·상세 조회는 운영진도 {@link NoticeService}를 쓴다.
 */
public interface AdminNoticeService {

    Notice create(Long createdBy, NoticeCreateCommand command);

    /**
     * 공지를 수정한다. 명령의 수정 시각이 저장된 값과 다르면(그 사이 다른 관리자가 고친 경우)
     * {@code NOTICE_UPDATE_CONFLICT}로 거부한다. 읽은 직후 다른 요청이 같은 내용을 먼저 고친 경우도 같다.
     */
    Notice update(Long id, NoticeUpdateCommand command);

    void delete(Long id);
}
