package kr.ac.kookmin.stream.api.app.welfare.rental;

import jakarta.validation.Valid;
import kr.ac.kookmin.stream.api.app.AppApiUser;
import kr.ac.kookmin.stream.api.app.welfare.rental.request.ItemListParams;
import kr.ac.kookmin.stream.api.app.welfare.rental.request.RentalApplyRequest;
import kr.ac.kookmin.stream.api.app.welfare.rental.request.RentalHistoryListParams;
import kr.ac.kookmin.stream.api.app.welfare.rental.response.ItemListItemResponse;
import kr.ac.kookmin.stream.api.app.welfare.rental.response.RentalHistoryListResponse;
import kr.ac.kookmin.stream.api.app.welfare.rental.response.ReturnRequiredListResponse;
import kr.ac.kookmin.stream.api.app.welfare.rental.usecase.RentalApplyUseCase;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.dto.CursorSliceResponse;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.Item;
import kr.ac.kookmin.stream.welfare.domain.rental.service.ItemService;
import kr.ac.kookmin.stream.welfare.domain.rental.service.RentalHistoryService;
import kr.ac.kookmin.stream.welfare.domain.rental.service.RentalTimeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/app/billilge")
@RequiredArgsConstructor
public class AppRentalController implements AppRentalApi {

    private final ItemService itemService;
    private final RentalHistoryService rentalHistoryService;
    private final RentalApplyUseCase rentalApplyUseCase;

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

    @Override
    @PostMapping("/histories")
    public ApiResponse<Void> applyRental(AppApiUser apiUser, @Valid @RequestBody RentalApplyRequest request) {
        // DB에 안 닿는 순수 검증은 트랜잭션(UseCase) 진입 전에 끝낸다 — 잘못된 요청이 커넥션을 잡지 않게 한다
        RentalTimeValidator.validate(request.rentAtHour(), request.rentAtMinute());
        rentalApplyUseCase.apply(
            apiUser.userId(),
            request.itemId(),
            request.count(),
            request.rentAtHour(),
            request.rentAtMinute(),
            request.ignoreDuplicate()
        );
        return ApiResponse.success();
    }
}
