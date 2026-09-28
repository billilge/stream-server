package kr.ac.kookmin.stream.api.app.welfare.rental.response;

import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalHistory;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalRecord;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalStatus;

public record RentalHistoryListResponse(List<History> histories) {

    public static RentalHistoryListResponse from(List<RentalRecord> records) {
        return new RentalHistoryListResponse(records.stream().map(History::from).toList());
    }

    public record History(
        Long rentalHistoryId,
        String itemName,
        String itemImageUrl,
        LocalDateTime rentAt,
        LocalDateTime returnedAt,
        RentalStatus status
    ) {

        public static History from(RentalRecord record) {
            RentalHistory history = record.history();
            return new History(
                history.getId(),
                record.itemName(),
                ItemImageUrl.from(record.itemImageKey()),
                history.getRentAt(),
                history.getReturnedAt(),
                history.getRentalStatus()
            );
        }
    }
}
