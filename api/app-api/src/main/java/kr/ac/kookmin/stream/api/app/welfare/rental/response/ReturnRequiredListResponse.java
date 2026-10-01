package kr.ac.kookmin.stream.api.app.welfare.rental.response;

import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.api.common.StorageUrlBuilder;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalRecord;

public record ReturnRequiredListResponse(List<Rental> rentalHistories) {

    public static ReturnRequiredListResponse from(List<RentalRecord> records) {
        return new ReturnRequiredListResponse(records.stream().map(Rental::from).toList());
    }

    public record Rental(
        Long rentalHistoryId,
        String itemName,
        String itemImageUrl,
        LocalDateTime dueAt
    ) {

        public static Rental from(RentalRecord record) {
            String imageKey = record.itemImageKey();
            String imageUrl = imageKey == null ? null : StorageUrlBuilder.build(imageKey);
            return new Rental(
                record.history().getId(),
                record.itemName(),
                imageUrl,
                record.dueAt()
            );
        }
    }
}
