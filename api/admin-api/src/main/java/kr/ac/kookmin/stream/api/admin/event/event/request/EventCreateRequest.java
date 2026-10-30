package kr.ac.kookmin.stream.api.admin.event.event.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.event.domain.event.domain.EventCreateCommand;
import kr.ac.kookmin.stream.event.domain.event.domain.EventPeriod;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitPolicy;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitType;

/**
 * 작성자는 요청 본문이 아니라 토큰의 관리자 ID로 정한다. 공개 여부는 받지 않고 비공개로 시작한다.
 */
public record EventCreateRequest(
    @Schema(description = "행사 제목", example = "체육대회")
    @NotBlank(message = "행사 제목을 입력해 주세요.")
    @Size(max = 200, message = "행사 제목은 200자 이하로 입력해 주세요.")
    String title,

    // description은 TEXT 컬럼(최대 65,535바이트)이다. 글자당 최대 4바이트로 잡아도 넘치지 않는 길이로 막는다
    @Schema(description = "행사 소개글", example = "학과 대항 체육대회입니다.")
    @NotBlank(message = "행사 소개글을 입력해 주세요.")
    @Size(max = 16000, message = "행사 소개글은 16000자 이하로 입력해 주세요.")
    String description,

    @Schema(description = "행사 장소", example = "종합운동장")
    @Size(max = 255, message = "행사 장소는 255자 이하로 입력해 주세요.")
    String place,

    @Schema(description = "행사 시작 일시", example = "2026-11-01T10:00:00")
    @NotNull(message = "행사 시작 일시를 입력해 주세요.")
    LocalDateTime eventStartAt,

    @Schema(description = "행사 종료 일시. 시작 일시보다 늦어야 한다", example = "2026-11-01T18:00:00")
    @NotNull(message = "행사 종료 일시를 입력해 주세요.")
    LocalDateTime eventEndAt,

    @Schema(description = "신청 시작 일시", example = "2026-10-14T10:00:00")
    @NotNull(message = "신청 시작 일시를 입력해 주세요.")
    LocalDateTime applyStartAt,

    @Schema(description = "신청 마감 일시. 시작 일시보다 늦어야 한다", example = "2026-10-25T23:59:59")
    @NotNull(message = "신청 마감 일시를 입력해 주세요.")
    LocalDateTime applyEndAt,

    @Schema(description = "모집 방식. 선착순이면 FIRST_COME, 인원 제한이 없으면 OPEN")
    @NotNull(message = "모집 방식을 입력해 주세요.")
    RecruitType recruitType,

    @Schema(description = "모집 정원. FIRST_COME이면 1 이상 필요하고, OPEN이면 무시한다", example = "100")
    Integer capacity,

    @Schema(description = "학생회비 납부자만 신청 가능 여부")
    @NotNull(message = "학생회비 납부자 전용 여부를 입력해 주세요.")
    Boolean feePaidOnly,

    @Schema(description = "행사 이미지 파일 ID 목록. 첫 번째가 대표 이미지가 된다", example = "[31, 32]")
    @NotNull(message = "이미지 파일 목록을 입력해 주세요.")
    @Size(max = 10, message = "이미지는 10장까지 등록할 수 있습니다.")
    List<@NotNull(message = "이미지 파일 ID는 비워 둘 수 없습니다.") Long> imageIds,

    @Schema(description = "신청서 질문 목록. 목록 순서대로 신청서에 노출된다")
    @NotNull(message = "신청서 질문 목록을 입력해 주세요.")
    @Valid
    List<@NotNull(message = "신청서 질문이 비어 있습니다.") EventQuestionRequest> questions
) {

    public EventCreateCommand toCommand() {
        return new EventCreateCommand(
            title,
            description,
            place,
            EventPeriod.of(eventStartAt, eventEndAt),
            EventPeriod.of(applyStartAt, applyEndAt),
            RecruitPolicy.of(recruitType, capacity, feePaidOnly),
            imageIds.stream().distinct().toList(),
            questions.stream().map(EventQuestionRequest::toCommand).toList()
        );
    }
}
