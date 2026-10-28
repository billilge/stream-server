package kr.ac.kookmin.stream.api.admin.welfare.rental.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.api.admin.welfare.rental.usecase.AdminRentalStatusWorkerLogView;
import kr.ac.kookmin.stream.api.common.ApiConstants;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalStatus;

public record AdminRentalStatusWorkerLogResponse(List<Entry> logs) {

    public static AdminRentalStatusWorkerLogResponse from(List<AdminRentalStatusWorkerLogView> views) {
        return new AdminRentalStatusWorkerLogResponse(views.stream().map(Entry::from).toList());
    }

    public record Entry(
        @Schema(description = "처리자 회원 ID", example = "2")
        Long workerId,
        @Schema(description = "처리자 이름. 탈퇴한 회원이면 placeholder", example = "홍길동")
        String workerName,
        @Schema(description = "그 처리로 바뀐 상태")
        RentalStatus rentalStatus,
        @Schema(description = "처리 시각")
        LocalDateTime changedAt
    ) {

        // worker가 null이면 탈퇴(소프트 삭제)한 회원의 과거 처리 기록이다 — 이름 대신 쓰는 표시용 placeholder.
        public static Entry from(AdminRentalStatusWorkerLogView view) {
            return new Entry(
                view.log().getWorkerId(),
                view.worker() != null ? view.worker().getName() : ApiConstants.WITHDRAWN_MEMBER_LABEL,
                view.log().getRentalStatus(),
                view.log().getCreatedAt()
            );
        }
    }
}
