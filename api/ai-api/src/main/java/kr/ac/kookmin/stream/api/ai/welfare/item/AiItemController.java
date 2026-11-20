package kr.ac.kookmin.stream.api.ai.welfare.item;

import java.util.List;
import kr.ac.kookmin.stream.api.ai.welfare.item.response.AiItemResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.Item;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemCursor;
import kr.ac.kookmin.stream.welfare.domain.rental.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/ai/billilge/items")
@RequiredArgsConstructor
public class AiItemController implements AiItemApi {

    // 학생 목록 API의 최대 크기와 같다. 물품은 수십 건 수준이라 보통 한 번에 끝난다
    private static final int PAGE_SIZE = 100;

    private final ItemService itemService;

    /**
     * 학생 물품 목록과 같은 서비스로 끝까지 넘겨 모은다. 학생 화면에 보이는 물품·수량과 자동으로 같다.
     * 인덱싱하지 않고 질문이 올 때마다 부르는 실시간 조회라, 수량은 부른 시점의 값이다.
     */
    @Override
    @GetMapping
    public ApiResponse<List<AiItemResponse>> getItems() {
        List<Item> items = CursorSliceResult.fetchAll(
            ItemCursor::from, cursor -> itemService.getItems(null, null, cursor, PAGE_SIZE));

        return ApiResponse.success(items.stream().map(AiItemResponse::from).toList());
    }
}
