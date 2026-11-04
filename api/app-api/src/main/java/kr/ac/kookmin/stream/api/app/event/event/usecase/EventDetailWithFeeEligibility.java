package kr.ac.kookmin.stream.api.app.event.event.usecase;

import kr.ac.kookmin.stream.event.domain.event.domain.EventDetail;

/**
 * {@link EventDetailUseCase}가 조합한 결과. DTO 변환은 Controller가 한다.
 *
 * @param feeEligible 학생회비 조건으로 신청할 수 있는지. 납부자 전용이 아니면 항상 {@code true}이고, 납부자 전용이면
 *                    납부자 명부에 있을 때만 {@code true}다. 모집 기간·정원은 보지 않는다
 */
public record EventDetailWithFeeEligibility(EventDetail detail, boolean feeEligible) {
}
