package kr.ac.kookmin.stream.api.admin.welfare.fee.request;

// 값이 없거나 0 이하인 경우를 FeeErrorCode.INVALID_FEE_AMOUNT로 응답하기 위해
// Bean Validation을 쓰지 않고 컨트롤러에서 직접 검증한다.
public record FeeAmountUpdateRequest(Integer amount) {
}
