package kr.ac.kookmin.stream.api.app.welfare.rental.response;

import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.common.FileUrlUtil;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalRecord;

public record ReturnRequiredListResponse(List<Rental> rentalHistories) {

    public static ReturnRequiredListResponse from(List<RentalRecord> records, String publicBaseUrl) {
        return new ReturnRequiredListResponse(records.stream().map(record -> Rental.from(record, publicBaseUrl)).toList());
    }

    public record Rental(
        Long rentalHistoryId,
        String itemName,
        String itemImageUrl,
        LocalDateTime dueAt
    ) {

        public static Rental from(RentalRecord record, String publicBaseUrl) {
            String imageKey = record.itemImageKey();
            String imageUrl = imageKey == null ? null : FileUrlUtil.buildPublicUrl(publicBaseUrl, imageKey);
            return new Rental(
                record.history().getId(),
                record.itemName(),
                imageUrl,
                record.dueAt()
            );
        }
    }
}
