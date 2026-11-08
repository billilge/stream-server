package kr.ac.kookmin.stream.api.ai.welfare.notice;

import java.util.ArrayList;
import java.util.List;
import kr.ac.kookmin.stream.api.ai.welfare.notice.response.AiNoticeResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.Notice;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCursor;
import kr.ac.kookmin.stream.welfare.domain.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/ai/notices")
@RequiredArgsConstructor
public class AiNoticeController implements AiNoticeApi {

    // 학생 목록 API의 최대 크기와 같다. 공지가 수백 건 수준이라 몇 번이면 끝난다
    private static final int PAGE_SIZE = 100;

    private final NoticeService noticeService;

    /**
     * 학생 목록과 같은 서비스로 끝까지 넘겨 모은다. 삭제된 공지가 빠지는 규칙이 학생 화면과 자동으로 같다.
     * 챗봇은 매번 전체를 받아 인덱스를 다시 만들므로, 응답에 없는 공지는 인덱스에서도 빠진다.
     */
    @Override
    @GetMapping
    public ApiResponse<List<AiNoticeResponse>> getNotices() {
        List<Notice> notices = new ArrayList<>();
        NoticeCursor cursor = null;
        do {
            CursorSliceResult<Notice> page = noticeService.getNotices(null, null, cursor, PAGE_SIZE);
            notices.addAll(page.content());
            cursor = page.hasNext() ? NoticeCursor.from(page.nextCursor()) : null;
        } while (cursor != null);

        return ApiResponse.success(notices.stream().map(AiNoticeResponse::from).toList());
    }
}
