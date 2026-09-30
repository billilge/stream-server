package kr.ac.kookmin.stream.api.app.welfare.rental.response;

import java.time.format.DateTimeFormatter;
import java.util.Map;
import kr.ac.kookmin.stream.file.domain.FileUrl;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.Item;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemCategory;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemType;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ReturnPolicy;

public record ItemListItemResponse(
    Long itemId,
    String name,
    ItemCategory category,
    ItemType type,
    int count,
    String imageUrl,
    ReturnPolicyResponse returnPolicy
) {

    public static ItemListItemResponse from(Item item, Map<String, FileUrl> imageUrls) {
        ReturnPolicy returnPolicy = item.getReturnPolicy();
        FileUrl imageUrl = item.getImageKey() == null ? null : imageUrls.get(item.getImageKey());
        return new ItemListItemResponse(
            item.getId(),
            item.getName(),
            item.getCategory(),
            item.getType(),
            item.getCount(),
            imageUrl == null ? null : imageUrl.url(),
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
