package kr.ac.kookmin.stream.api.app.welfare.rental.response;

import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.common.FileUrlUtil;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalHistory;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalRecord;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalStatus;

public record RentalHistoryListResponse(List<History> histories) {

    public static RentalHistoryListResponse from(List<RentalRecord> records, String publicBaseUrl) {
        return new RentalHistoryListResponse(records.stream().map(record -> History.from(record, publicBaseUrl)).toList());
    }

    public record History(
        Long rentalHistoryId,
        String itemName,
        String itemImageUrl,
        LocalDateTime rentAt,
        LocalDateTime returnedAt,
        RentalStatus status
    ) {

        public static History from(RentalRecord record, String publicBaseUrl) {
            RentalHistory history = record.history();
            String imageKey = record.itemImageKey();
            String imageUrl = imageKey == null ? null : FileUrlUtil.buildPublicUrl(publicBaseUrl, imageKey);
            return new History(
                history.getId(),
                record.itemName(),
                imageUrl,
                history.getRentAt(),
                history.getReturnedAt(),
                history.getRentalStatus()
            );
        }
    }
}
