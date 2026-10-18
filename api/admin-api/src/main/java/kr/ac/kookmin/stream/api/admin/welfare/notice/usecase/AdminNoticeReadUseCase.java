package kr.ac.kookmin.stream.api.admin.welfare.notice.usecase;

import java.util.List;
import kr.ac.kookmin.stream.file.service.FileService;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.Notice;
import kr.ac.kookmin.stream.welfare.domain.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 운영진 공지 상세를 조회한다. 공지(welfare)와 그 공지가 연결한 파일(file)은 서로 다른 도메인이라 이 UseCase가 두 Service를 조합한다.
 */
@Component
@RequiredArgsConstructor
public class AdminNoticeReadUseCase {

    private final NoticeService noticeService;
    private final FileService fileService;

    public AdminNoticeDetail getDetail(Long noticeId) {
        Notice notice = noticeService.getNotice(noticeId);

        return new AdminNoticeDetail(
            notice,
            fileService.findAllByIdIn(orEmpty(notice.getImageIds())),
            fileService.findAllByIdIn(orEmpty(notice.getAttachmentIds()))
        );
    }

    private List<Long> orEmpty(List<Long> ids) {
        return ids == null ? List.of() : ids;
    }
}
