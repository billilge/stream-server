package kr.ac.kookmin.stream.api.app.internal.config;

import kr.ac.kookmin.stream.api.app.internal.config.response.ServiceLinkResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.internal.domain.config.domain.ServiceLinkConfigKeys;
import kr.ac.kookmin.stream.internal.domain.config.service.AdminConfigValueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/app/service-links")
@RequiredArgsConstructor
public class AppConfigController implements AppConfigApi {

    private final AdminConfigValueService adminConfigValueService;

    @Override
    @GetMapping
    public ApiResponse<ServiceLinkResponse> getServiceLinks() {
        return ApiResponse.success(new ServiceLinkResponse(
            adminConfigValueService.getValue(ServiceLinkConfigKeys.FEEDBACK_FORM_URL).orElse(null),
            adminConfigValueService.getValue(ServiceLinkConfigKeys.PRIVACY_POLICY_URL).orElse(null)
        ));
    }
}
