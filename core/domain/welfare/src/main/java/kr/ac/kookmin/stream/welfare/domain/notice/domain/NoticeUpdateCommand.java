package kr.ac.kookmin.stream.welfare.domain.notice.domain;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @param expectedUpdatedAt 수정 화면이 공지를 읽었을 때의 수정 시각. 그 사이 다른 관리자가 고쳤는지 가려내는 데 쓴다
 */
public record NoticeUpdateCommand(
    String title,
    String content,
    NoticeCategory category,
    List<Long> imageIds,
    List<Long> attachmentIds,
    LocalDateTime expectedUpdatedAt
) {
}
