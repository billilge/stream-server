package kr.ac.kookmin.stream.api.app.welfare.rental.response;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import kr.ac.kookmin.stream.api.common.StorageUrlBuilder;
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
    ReturnPolicyResponse returnPolicy,
    String returnDeadlineLabel
) {

    private static final DateTimeFormatter HOUR_FORMAT = DateTimeFormatter.ofPattern("H");
    private static final DateTimeFormatter DUE_DATE_FORMAT = DateTimeFormatter.ofPattern("M/d");

    public static ItemListItemResponse from(Item item) {
        ReturnPolicy returnPolicy = item.getReturnPolicy();
        String imageUrl = StorageUrlBuilder.build(item.getImageKey());
        return new ItemListItemResponse(
            item.getId(),
            item.getName(),
            item.getCategory(),
            item.getType(),
            item.getCount(),
            imageUrl,
            returnPolicy == null ? null : ReturnPolicyResponse.from(returnPolicy),
            returnPolicy == null ? null : returnDeadlineLabel(returnPolicy)
        );
    }

    // 오늘 대여한다고 가정했을 때의 반납 기한 문구. 0일이면 당일(시각까지), 1일이면 "내일", 그 밖은 날짜(M/d)로 보여준다
    private static String returnDeadlineLabel(ReturnPolicy returnPolicy) {
        int maxRentalDays = returnPolicy.maxRentalDays();
        if (maxRentalDays == 0) {
            return "오늘 " + returnPolicy.returnDeadline().format(HOUR_FORMAT) + "시까지 반납";
        }
        if (maxRentalDays == 1) {
            return "내일까지 반납";
        }
        LocalDate dueDate = LocalDate.now().plusDays(maxRentalDays);
        return dueDate.format(DUE_DATE_FORMAT) + "까지 반납";
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
