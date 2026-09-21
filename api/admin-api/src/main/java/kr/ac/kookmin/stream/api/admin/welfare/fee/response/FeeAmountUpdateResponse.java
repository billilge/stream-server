package kr.ac.kookmin.stream.api.admin.welfare.fee.response;

public record FeeAmountUpdateResponse(Integer amount) {
    public static FeeAmountUpdateResponse of(Integer amount) {
        return new FeeAmountUpdateResponse(amount);
    }
}
