package kr.ac.kookmin.stream.api.app.internal.display;

import kr.ac.kookmin.stream.api.app.internal.display.response.DisplayScreenResponse;
import kr.ac.kookmin.stream.api.app.internal.display.usecase.DisplayScreenUseCase;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/display")
@RequiredArgsConstructor
public class AppDisplayController implements AppDisplayApi {

    private final DisplayScreenUseCase displayScreenUseCase;

    @Override
    @GetMapping
    public ApiResponse<DisplayScreenResponse> getScreen() {
        return ApiResponse.success(DisplayScreenResponse.from(displayScreenUseCase.getScreen()));
    }
}
