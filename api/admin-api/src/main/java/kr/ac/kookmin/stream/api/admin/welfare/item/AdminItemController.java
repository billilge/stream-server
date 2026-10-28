package kr.ac.kookmin.stream.api.admin.welfare.item;

import jakarta.validation.Valid;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.welfare.item.request.ItemCreateRequest;
import kr.ac.kookmin.stream.api.admin.welfare.item.request.ItemUpdateRequest;
import kr.ac.kookmin.stream.api.admin.welfare.item.response.AdminItemDetailResponse;
import kr.ac.kookmin.stream.api.admin.welfare.item.usecase.AdminItemUseCase;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.dto.PageParams;
import kr.ac.kookmin.stream.api.common.dto.PageResponse;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.Item;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemCategory;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemType;
import kr.ac.kookmin.stream.welfare.domain.rental.service.AdminItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin/billilge/items")
@RequiredArgsConstructor
public class AdminItemController implements AdminItemApi {

    private final AdminItemService adminItemService;
    private final AdminItemUseCase adminItemUseCase;

    @Override
    @GetMapping
    public ApiResponse<PageResponse<AdminItemDetailResponse>> getItems(
        AdminApiUser apiUser,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) String type,
        @RequestParam(required = false) String keyword,
        @Valid @ModelAttribute PageParams pageParams
    ) {
        PageResult<Item> result = adminItemService.search(
            ItemCategory.from(category), ItemType.from(type), keyword, pageParams.toOffset()
        );
        return ApiResponse.success(PageResponse.from(result, AdminItemDetailResponse::from));
    }

    @Override
    @PostMapping
    public ApiResponse<AdminItemDetailResponse> createItem(
        AdminApiUser apiUser,
        @Valid @RequestBody ItemCreateRequest request
    ) {
        Item item = adminItemUseCase.create(request);
        return ApiResponse.success(AdminItemDetailResponse.from(item));
    }

    @Override
    @PutMapping("/{itemId}")
    public ApiResponse<AdminItemDetailResponse> updateItem(
        AdminApiUser apiUser,
        @PathVariable Long itemId,
        @Valid @RequestBody ItemUpdateRequest request
    ) {
        Item item = adminItemUseCase.update(itemId, request);
        return ApiResponse.success(AdminItemDetailResponse.from(item));
    }

    @Override
    @DeleteMapping("/{itemId}")
    public ApiResponse<Void> deleteItem(AdminApiUser apiUser, @PathVariable Long itemId) {
        adminItemService.delete(itemId);
        return ApiResponse.success();
    }
}
