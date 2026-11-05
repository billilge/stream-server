package kr.ac.kookmin.stream.api.app.internal.config;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.app.internal.config.response.ServiceLinkResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;

/**
 * 학생 앱 서비스 링크 API의 문서 명세. 구현은 {@link AppConfigController}가 맡는다.
 */
@Tag(name = "서비스 링크", description = "학생 앱 외부 링크(설정 화면·학생회비 캡처 단계) 조회")
public interface AppConfigApi {

    @Operation(summary = "서비스 링크 조회",
        description = """
            설정 화면 서비스 정보의 '의견 보내기'(구글폼)와 '개인정보 처리방침'(노션) 링크,
            학생회비 송금 캡처 단계의 'K스마트멘토' 링크를 조회한다.
            등록되지 않은 링크는 null이다.""")
    ApiResponse<ServiceLinkResponse> getServiceLinks();
}
