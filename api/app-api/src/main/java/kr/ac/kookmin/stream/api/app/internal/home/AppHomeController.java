package kr.ac.kookmin.stream.api.app.internal.home;

import kr.ac.kookmin.stream.api.app.AppApiUser;
import kr.ac.kookmin.stream.api.app.internal.home.response.HomeResponse;
import kr.ac.kookmin.stream.api.app.internal.home.usecase.AppHomeUseCase;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/app/home")
@RequiredArgsConstructor
public class AppHomeController implements AppHomeApi {

    private final AppHomeUseCase appHomeUseCase;

    @Override
    @GetMapping
    public ApiResponse<HomeResponse> getHome(AppApiUser apiUser) {
        return ApiResponse.success(appHomeUseCase.getHome(apiUser.userId()));
    }
}
