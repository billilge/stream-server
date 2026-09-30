package kr.ac.kookmin.stream.api.app.welfare.rental;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import kr.ac.kookmin.stream.api.app.AppApiUser;
import kr.ac.kookmin.stream.api.app.welfare.rental.request.ItemListParams;
import kr.ac.kookmin.stream.api.app.welfare.rental.request.RentalHistoryListParams;
import kr.ac.kookmin.stream.api.app.welfare.rental.response.ItemListItemResponse;
import kr.ac.kookmin.stream.api.app.welfare.rental.response.RentalHistoryListResponse;
import kr.ac.kookmin.stream.api.app.welfare.rental.response.ReturnRequiredListResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.dto.CursorSliceResponse;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.file.domain.FileUrl;
import kr.ac.kookmin.stream.file.service.FileService;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.Item;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalRecord;
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
    private final FileService fileService;

    @Override
    @GetMapping("/items")
    public ApiResponse<CursorSliceResponse<ItemListItemResponse>> getItems(
        @Valid @ModelAttribute ItemListParams params
    ) {
        CursorSliceResult<Item> result = itemService.getItems(
            params.toCategory(), params.toKeyword(), params.toCursor(), params.sizeOrDefault()
        );
        Map<String, FileUrl> imageUrls = fileService.publicUrls(
            result.content().stream().map(Item::getImageKey).filter(key -> key != null).distinct().toList()
        );
        return ApiResponse.success(CursorSliceResponse.from(result, item -> ItemListItemResponse.from(item, imageUrls)));
    }

    @Override
    @GetMapping("/histories")
    public ApiResponse<RentalHistoryListResponse> getHistories(
        AppApiUser apiUser,
        @Valid @ModelAttribute RentalHistoryListParams params
    ) {
        List<RentalRecord> records = rentalHistoryService.getHistories(apiUser.userId(), params.toStatus());
        return ApiResponse.success(RentalHistoryListResponse.from(records, resolveImageUrls(records)));
    }

    @Override
    @GetMapping("/histories/return-required")
    public ApiResponse<ReturnRequiredListResponse> getReturnRequired(AppApiUser apiUser) {
        List<RentalRecord> records = rentalHistoryService.getReturnRequiredRentals(apiUser.userId());
        return ApiResponse.success(ReturnRequiredListResponse.from(records, resolveImageUrls(records)));
    }

    private Map<String, FileUrl> resolveImageUrls(List<RentalRecord> records) {
        return fileService.publicUrls(
            records.stream().map(RentalRecord::itemImageKey).filter(key -> key != null).distinct().toList()
        );
    }
}
