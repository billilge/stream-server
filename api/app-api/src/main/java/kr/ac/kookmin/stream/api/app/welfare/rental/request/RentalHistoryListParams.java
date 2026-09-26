package kr.ac.kookmin.stream.api.app.welfare.rental.request;

import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalStatus;

public record RentalHistoryListParams(String status) {

    public RentalStatus toStatus() {
        return RentalStatus.from(status);
    }
}
