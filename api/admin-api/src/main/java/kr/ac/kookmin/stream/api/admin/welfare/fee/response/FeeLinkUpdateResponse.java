package kr.ac.kookmin.stream.api.admin.welfare.fee.response;

public record FeeLinkUpdateResponse(String paymentLinkUrl) {
    public static FeeLinkUpdateResponse of(String paymentLinkUrl) {
        return new FeeLinkUpdateResponse(paymentLinkUrl);
    }
}
