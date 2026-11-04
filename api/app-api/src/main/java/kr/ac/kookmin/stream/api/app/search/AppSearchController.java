package kr.ac.kookmin.stream.api.app.search;

import jakarta.validation.Valid;
import kr.ac.kookmin.stream.api.app.search.request.SearchParams;
import kr.ac.kookmin.stream.api.app.search.response.SearchResponse;
import kr.ac.kookmin.stream.api.app.search.usecase.AppSearchUseCase;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/app/search")
@RequiredArgsConstructor
public class AppSearchController implements AppSearchApi {

    private final AppSearchUseCase appSearchUseCase;

    @Override
    @GetMapping
    public ApiResponse<SearchResponse> search(@Valid @ModelAttribute SearchParams params) {
        return ApiResponse.success(appSearchUseCase.search(params.keyword()));
    }
}
