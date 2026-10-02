package kr.ac.kookmin.stream.api.app.welfare.rental;

import jakarta.validation.Valid;
import kr.ac.kookmin.stream.api.app.AppApiUser;
import kr.ac.kookmin.stream.api.app.welfare.rental.request.ItemListParams;
import kr.ac.kookmin.stream.api.app.welfare.rental.request.RentalHistoryListParams;
import kr.ac.kookmin.stream.api.app.welfare.rental.response.ItemListItemResponse;
import kr.ac.kookmin.stream.api.app.welfare.rental.response.RentalHistoryListResponse;
import kr.ac.kookmin.stream.api.app.welfare.rental.response.ReturnRequiredListResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.dto.CursorSliceResponse;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.Item;
import kr.ac.kookmin.stream.welfare.domain.rental.service.ItemService;
import kr.ac.kookmin.stream.welfare.domain.rental.service.RentalHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/app/billilge")
@RequiredArgsConstructor
public class AppRentalController implements AppRentalApi {

    private final ItemService itemService;
    private final RentalHistoryService rentalHistoryService;

    @Override
    @GetMapping("/items")
    public ApiResponse<CursorSliceResponse<ItemListItemResponse>> getItems(
        @Valid @ModelAttribute ItemListParams params
    ) {
        CursorSliceResult<Item> result = itemService.getItems(
            params.toCategory(), params.toKeyword(), params.toCursor(), params.sizeOrDefault()
        );
        return ApiResponse.success(CursorSliceResponse.from(result, ItemListItemResponse::from));
    }

    @Override
    @GetMapping("/histories")
    public ApiResponse<RentalHistoryListResponse> getHistories(
        AppApiUser apiUser,
        @Valid @ModelAttribute RentalHistoryListParams params
    ) {
        return ApiResponse.success(RentalHistoryListResponse.from(
            rentalHistoryService.getHistories(apiUser.userId(), params.toStatus())
        ));
    }

    @Override
    @GetMapping("/histories/return-required")
    public ApiResponse<ReturnRequiredListResponse> getReturnRequired(AppApiUser apiUser) {
        return ApiResponse.success(ReturnRequiredListResponse.from(
            rentalHistoryService.getReturnRequiredRentals(apiUser.userId())
        ));
    }
}
