package kr.ac.kookmin.stream.welfare.domain.notice.domain;

import java.util.List;

public record NoticeCreateCommand(
    String title,
    String content,
    NoticeCategory category,
    List<Long> imageIds,
    List<Long> attachmentIds,
    Long createdBy
) {
}
