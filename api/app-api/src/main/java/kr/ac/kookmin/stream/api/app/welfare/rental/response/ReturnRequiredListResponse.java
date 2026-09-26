package kr.ac.kookmin.stream.api.app.welfare.rental.response;

import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ReturnRequiredRental;

public record ReturnRequiredListResponse(List<Rental> rentalHistories) {

    public static ReturnRequiredListResponse from(List<ReturnRequiredRental> rentals) {
        return new ReturnRequiredListResponse(rentals.stream().map(Rental::from).toList());
    }

    public record Rental(
        Long rentalHistoryId,
        String itemName,
        String itemImageUrl,
        LocalDateTime dueAt
    ) {

        public static Rental from(ReturnRequiredRental rental) {
            return new Rental(
                rental.historyId(),
                rental.itemName(),
                ItemImageUrl.from(rental.itemImageKey()),
                rental.dueAt()
            );
        }
    }
}
