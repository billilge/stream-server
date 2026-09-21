package kr.ac.kookmin.stream.api.admin.welfare.fee.response;

public record FeeLinkUpdateResponse(String bank, String accountNo) {
    public static FeeLinkUpdateResponse of(String bank, String accountNo) {
        return new FeeLinkUpdateResponse(bank, accountNo);
    }
}
