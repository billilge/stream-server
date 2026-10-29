package kr.ac.kookmin.stream.api.admin.welfare.item.usecase;

import kr.ac.kookmin.stream.api.admin.welfare.item.request.ItemCreateRequest;
import kr.ac.kookmin.stream.api.admin.welfare.item.request.ItemUpdateRequest;
import kr.ac.kookmin.stream.file.service.FileService;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.Item;
import kr.ac.kookmin.stream.welfare.domain.rental.service.AdminItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 물품 등록·수정. {@code file}(이미지 파일 조회)과 {@code rental}(물품 저장) 두 도메인을 조합한다
 * (coding-style.md 2-9절). 실제 쓰기는 {@code AdminItemService}의 트랜잭션 하나뿐이라 여기엔
 * {@code @Transactional}을 걸지 않는다.
 */
@Component
@RequiredArgsConstructor
public class AdminItemUseCase {

    private final FileService fileService;
    private final AdminItemService adminItemService;

    public Item create(ItemCreateRequest request) {
        String imageKey = fileService.resolveKey(request.imageFileId());
        return adminItemService.create(request.toCommand(imageKey));
    }

    public Item update(Long itemId, ItemUpdateRequest request) {
        String imageKey = request.imageFileId() == null ? null : fileService.resolveKey(request.imageFileId());
        return adminItemService.update(itemId, request.toCommand(imageKey));
    }
}
