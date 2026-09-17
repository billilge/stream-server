package kr.ac.kookmin.stream.api.admin.welfare.fee.response;

public record FeeLinkUpdateResponse(String transferLinkUrl) {
    public static FeeLinkUpdateResponse of(String transferLinkUrl) {
        return new FeeLinkUpdateResponse(transferLinkUrl);
    }
}
