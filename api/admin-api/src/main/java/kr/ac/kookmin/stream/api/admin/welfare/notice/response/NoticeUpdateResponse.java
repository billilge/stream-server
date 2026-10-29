package kr.ac.kookmin.stream.api.admin.welfare.notice.response;

import kr.ac.kookmin.stream.welfare.domain.notice.domain.Notice;

public record NoticeUpdateResponse(Long noticeId) {

    public static NoticeUpdateResponse from(Notice notice) {
        return new NoticeUpdateResponse(notice.getId());
    }
}
