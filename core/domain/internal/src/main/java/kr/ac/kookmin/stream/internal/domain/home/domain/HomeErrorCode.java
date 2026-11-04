package kr.ac.kookmin.stream.internal.domain.home.domain;

import kr.ac.kookmin.stream.common.ErrorCode;
import kr.ac.kookmin.stream.common.ErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public enum HomeErrorCode implements ErrorCode {

    HOME_BANNER_NOT_FOUND(ErrorStatus.NOT_FOUND, "존재하지 않는 배너입니다."),
    HOME_BANNER_INVALID_LANDING(ErrorStatus.BAD_REQUEST, "랜딩 종류에 맞는 연결 대상을 입력해 주세요."),
    HOME_BANNER_INVALID_EXPOSURE_PERIOD(ErrorStatus.BAD_REQUEST, "노출 종료 일시는 시작 일시보다 늦어야 합니다."),
    // 보낸 목록과 서버의 목록이 어긋났다는 뜻이라 잘못된 입력(400)이 아니라 경합(409)으로 본다
    HOME_BANNER_ORDER_MISMATCH(ErrorStatus.CONFLICT, "배너 목록이 바뀌었습니다. 새로 불러온 뒤 다시 시도해 주세요."),
    HOME_BANNER_IMAGE_REQUIRED(ErrorStatus.BAD_REQUEST, "기타 카테고리는 배너 이미지를 직접 올려야 합니다."),
    HOME_APPLY_SECTION_DUPLICATE_TARGET(ErrorStatus.BAD_REQUEST, "같은 대상을 두 번 보낼 수 없습니다."),
    // 설정 행이 없던 대상을 두 운영진이 동시에 건드려 유니크 키에 걸린 것이라 잘못된 입력(400)이 아니다
    HOME_APPLY_SECTION_CONFLICT(ErrorStatus.CONFLICT, "신청 섹션 설정이 방금 바뀌었습니다. 다시 시도해 주세요."),
    // 운영진이 입력으로 고칠 수 없는 설정 누락이라 400이 아니다
    HOME_BANNER_PRESET_NOT_CONFIGURED(
        ErrorStatus.INTERNAL_SERVER_ERROR, "카테고리의 기본 배너 이미지가 설정되지 않았습니다.");

    private final int status;
    private final String message;
}
