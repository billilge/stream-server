package kr.ac.kookmin.stream.api.app.welfare.rental.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import kr.ac.kookmin.stream.file.domain.FileUrl;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalRecord;

public record ReturnRequiredListResponse(List<Rental> rentalHistories) {

    public static ReturnRequiredListResponse from(List<RentalRecord> records, Map<String, FileUrl> imageUrls) {
        return new ReturnRequiredListResponse(records.stream().map(record -> Rental.from(record, imageUrls)).toList());
    }

    public record Rental(
        Long rentalHistoryId,
        String itemName,
        String itemImageUrl,
        LocalDateTime dueAt
    ) {

        public static Rental from(RentalRecord record, Map<String, FileUrl> imageUrls) {
            String imageKey = record.itemImageKey();
            FileUrl imageUrl = imageKey == null ? null : imageUrls.get(imageKey);
            return new Rental(
                record.history().getId(),
                record.itemName(),
                imageUrl == null ? null : imageUrl.url(),
                record.dueAt()
            );
        }
    }
}
