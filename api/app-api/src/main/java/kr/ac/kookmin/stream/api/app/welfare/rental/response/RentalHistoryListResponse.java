package kr.ac.kookmin.stream.api.app.welfare.rental.response;

import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalHistorySummary;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalStatus;

public record RentalHistoryListResponse(List<History> histories) {

    public static RentalHistoryListResponse from(List<RentalHistorySummary> summaries) {
        return new RentalHistoryListResponse(summaries.stream().map(History::from).toList());
    }

    public record History(
        Long rentalHistoryId,
        String itemName,
        String itemImageUrl,
        LocalDateTime rentAt,
        LocalDateTime returnedAt,
        RentalStatus status
    ) {

        public static History from(RentalHistorySummary summary) {
            return new History(
                summary.historyId(),
                summary.itemName(),
                ItemImageUrl.from(summary.itemImageKey()),
                summary.rentAt(),
                summary.returnedAt(),
                summary.status()
            );
        }
    }
}
