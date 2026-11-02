package kr.ac.kookmin.stream.api.admin.internal.home.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerCategory;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerCreateCommand;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerLandingType;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerUpdateCommand;

/**
 * 배너 등록과 수정이 같은 본문을 받는다. 수정이 입력값 전체를 통째로 바꾸는 방식이라 등록과 모양이 같아,
 * 두 record로 나누면 같은 선언이 두 벌 생긴다({@code ArchiveWriteRequest}와 같은 이유다).
 * <p>
 * 표시 순서는 어느 쪽도 받지 않는다. 등록은 서버가 맨 뒤로 정하고, 수정은 순서를 손대지 않는다.
 * <p>
 * {@code imageFileId}는 {@code ETC}에만 필요해 어노테이션으로 필수를 걸 수 없다 — 다른 필드(카테고리)에
 * 따라 갈리는 규칙이라 {@code AdminHomeBannerWriteUseCase}가 본다.
 * <p>
 * 랜딩 타입과 값의 짝({@code NOTICE}·{@code EVENT}면 {@code targetId},
 * {@code EXTERNAL_LINK}면 {@code linkUrl})은 한 필드만 봐서는 판정할 수 없어 도메인이 검증한다.
 */
public record AdminHomeBannerWriteRequest(
    @Schema(description = "제목. 줄바꿈(\\n)을 넣을 수 있다", example = "제휴 혜택 안내")
    @NotBlank(message = "제목을 입력해 주세요.")
    @Size(max = 100, message = "제목은 100자 이하로 입력해 주세요.")
    String title,

    @Schema(description = "부제목. 운영진 목록에는 보이지 않고 학생 앱 홈과 배너 상세에 보인다", example = "교내 식당 10% 할인")
    @Size(max = 100, message = "부제목은 100자 이하로 입력해 주세요.")
    String subtitle,

    @Schema(description = "카테고리. 배경 이미지가 이 값으로 정해진다")
    @NotNull(message = "배너 카테고리를 입력해 주세요.")
    HomeBannerCategory category,

    @Schema(description = "직접 올린 배경 이미지의 파일 ID. ETC일 때만 보낸다 — 나머지 카테고리는 서버가 "
        + "프리셋으로 채우므로 보내도 무시한다", example = "101")
    Long imageFileId,

    @Schema(description = "배너를 눌렀을 때 가는 곳")
    @NotNull(message = "랜딩 종류를 입력해 주세요.")
    HomeBannerLandingType landingType,

    @Schema(description = "연결할 공지·행사 ID. NOTICE·EVENT일 때만 채운다", example = "12")
    Long targetId,

    @Schema(description = "외부 링크 주소. EXTERNAL_LINK일 때만 채운다", example = "https://example.com")
    @Size(max = 2048, message = "링크 주소는 2048자 이하로 입력해 주세요.")
    String linkUrl,

    @Schema(description = "노출 시작 일시", example = "2026-03-02T09:00:00")
    @NotNull(message = "노출 시작 일시를 입력해 주세요.")
    LocalDateTime exposureStartAt,

    @Schema(description = "노출 종료 일시", example = "2026-03-31T23:59:00")
    @NotNull(message = "노출 종료 일시를 입력해 주세요.")
    LocalDateTime exposureEndAt
) {

    public HomeBannerCreateCommand toCreateCommand() {
        return new HomeBannerCreateCommand(
            title, subtitle, category, imageFileId, landingType, targetId, linkUrl,
            exposureStartAt, exposureEndAt
        );
    }

    public HomeBannerUpdateCommand toUpdateCommand() {
        return new HomeBannerUpdateCommand(
            title, subtitle, category, imageFileId, landingType, targetId, linkUrl,
            exposureStartAt, exposureEndAt
        );
    }
}
