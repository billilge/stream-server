package kr.ac.kookmin.stream.api.app.welfare.rental.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import kr.ac.kookmin.stream.file.domain.FileUrl;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalHistory;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalRecord;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalStatus;

public record RentalHistoryListResponse(List<History> histories) {

    public static RentalHistoryListResponse from(List<RentalRecord> records, Map<String, FileUrl> imageUrls) {
        return new RentalHistoryListResponse(records.stream().map(record -> History.from(record, imageUrls)).toList());
    }

    public record History(
        Long rentalHistoryId,
        String itemName,
        String itemImageUrl,
        LocalDateTime rentAt,
        LocalDateTime returnedAt,
        RentalStatus status
    ) {

        public static History from(RentalRecord record, Map<String, FileUrl> imageUrls) {
            RentalHistory history = record.history();
            String imageKey = record.itemImageKey();
            FileUrl imageUrl = imageKey == null ? null : imageUrls.get(imageKey);
            return new History(
                history.getId(),
                record.itemName(),
                imageUrl == null ? null : imageUrl.url(),
                history.getRentAt(),
                history.getReturnedAt(),
                history.getRentalStatus()
            );
        }
    }
}
