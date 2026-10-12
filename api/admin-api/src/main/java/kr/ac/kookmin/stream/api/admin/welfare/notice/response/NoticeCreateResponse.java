package kr.ac.kookmin.stream.api.admin.welfare.notice.response;

import kr.ac.kookmin.stream.welfare.domain.notice.domain.Notice;

public record NoticeCreateResponse(Long noticeId) {

    public static NoticeCreateResponse from(Notice notice) {
        return new NoticeCreateResponse(notice.getId());
    }
}
