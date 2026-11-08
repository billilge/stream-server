package kr.ac.kookmin.stream.api.ai.welfare.notice.response;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.Notice;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCategory;

/**
 * 챗봇 인덱싱용 공지. 검색과 출처 표시에 필요한 글만 담는다 — 작성자, 이미지·첨부 파일은 넣지 않는다.
 */
public record AiNoticeResponse(
    Long noticeId,
    NoticeCategory category,
    String title,
    String content,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

    public static AiNoticeResponse from(Notice notice) {
        return new AiNoticeResponse(
            notice.getId(),
            notice.getCategory(),
            notice.getTitle(),
            notice.getContent(),
            notice.getCreatedAt(),
            notice.getUpdatedAt()
        );
    }
}
