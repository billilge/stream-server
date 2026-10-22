package kr.ac.kookmin.stream.welfare.domain.notice.service;

import kr.ac.kookmin.stream.welfare.domain.notice.domain.Notice;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCreateCommand;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeUpdateCommand;

/**
 * 운영진용 공지 서비스. 작성·수정·삭제를 맡고, 목록·상세 조회는 운영진도 {@link NoticeService}를 쓴다.
 */
public interface AdminNoticeService {

    Notice create(Long createdBy, NoticeCreateCommand command);

    Notice update(Long id, NoticeUpdateCommand command);

    void delete(Long id);
}
