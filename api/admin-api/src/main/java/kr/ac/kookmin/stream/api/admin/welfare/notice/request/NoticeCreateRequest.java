package kr.ac.kookmin.stream.api.admin.welfare.notice.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCategory;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCreateCommand;

/**
 * 작성자는 요청 본문이 아니라 토큰의 관리자 ID로 정한다. 본문에 {@code createdBy}가 와도 읽지 않는다.
 */
public record NoticeCreateRequest(
    @Schema(description = "제목", example = "제목 예시")
    @NotBlank(message = "제목을 입력해 주세요.")
    @Size(max = 200, message = "제목은 200자 이하로 입력해 주세요.")
    String title,

    // content는 TEXT 컬럼(최대 65,535바이트)이다. 글자당 최대 4바이트로 잡아도 넘치지 않는 길이로 막는다
    @Schema(description = "본문(HTML)", example = "<p>공지 내용입니다.</p>")
    @NotBlank(message = "본문을 입력해 주세요.")
    @Size(max = 16000, message = "본문은 16000자 이하로 입력해 주세요.")
    String content,

    @Schema(description = "공지 종류")
    @NotNull(message = "공지 종류를 입력해 주세요.")
    NoticeCategory category,

    @Schema(description = "이미지 파일 ID 목록. 첫 번째가 목록의 썸네일이 된다", example = "[101, 102]")
    @NotNull(message = "이미지 파일 목록을 입력해 주세요.")
    List<@NotNull(message = "이미지 파일 ID는 비워 둘 수 없습니다.") Long> imageFileIds,

    @Schema(description = "첨부 파일 ID 목록", example = "[201]")
    @NotNull(message = "첨부 파일 목록을 입력해 주세요.")
    List<@NotNull(message = "첨부 파일 ID는 비워 둘 수 없습니다.") Long> attachmentFileIds
) {

    public NoticeCreateCommand toCommand(Long createdBy) {
        return new NoticeCreateCommand(
            title,
            content,
            category,
            imageFileIds.stream().distinct().toList(),
            attachmentFileIds.stream().distinct().toList(),
            createdBy
        );
    }
}
