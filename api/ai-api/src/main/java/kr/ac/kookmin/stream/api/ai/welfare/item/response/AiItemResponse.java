package kr.ac.kookmin.stream.api.ai.welfare.item.response;

import java.time.format.DateTimeFormatter;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.Item;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemCategory;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemType;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ReturnPolicy;

/**
 * 챗봇 재고 조회용 물품. 답변에 쓰는 값만 담는다 — 이미지는 넣지 않는다.
 * 필드 이름은 학생 앱 물품 목록과 같게 맞췄다. 챗봇 쪽은 주소만 바꾸면 된다.
 */
public record AiItemResponse(
    Long itemId,
    String name,
    ItemCategory category,
    ItemType type,
    int count,
    ReturnPolicyResponse returnPolicy
) {

    public static AiItemResponse from(Item item) {
        ReturnPolicy returnPolicy = item.getReturnPolicy();
        return new AiItemResponse(
            item.getId(),
            item.getName(),
            item.getCategory(),
            item.getType(),
            item.getCount(),
            // 소모품은 반납 정책이 없다
            returnPolicy == null ? null : ReturnPolicyResponse.from(returnPolicy)
        );
    }

    public record ReturnPolicyResponse(int maxRentalDays, String returnDeadline) {

        private static final DateTimeFormatter DEADLINE_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

        public static ReturnPolicyResponse from(ReturnPolicy returnPolicy) {
            return new ReturnPolicyResponse(
                returnPolicy.maxRentalDays(),
                returnPolicy.returnDeadline().format(DEADLINE_FORMAT)
            );
        }
    }
}
