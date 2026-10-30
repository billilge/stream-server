package kr.ac.kookmin.stream.api.admin.welfare.rental.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import kr.ac.kookmin.stream.api.common.ApiConstants;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.Item;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalHistory;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalRecord;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalStatus;

/** 대여 기록 하나의 상세. 처리자 이력은 상태 변경 처리자 로그 조회 API가 따로 맡는다. */
public record AdminRentalHistoryResponse(
    @Schema(description = "대여 기록 ID", example = "501")
    Long rentalId,
    @Schema(description = "대여자")
    MemberSummary member,
    @Schema(description = "대여 물품")
    ItemSummary item,
    @Schema(description = "대여 수량", example = "1")
    int rentedCount,
    @Schema(description = "실제 수령 시각. 아직 수령 전이면 null")
    LocalDateTime rentAt,
    @Schema(description = "반납 기한. 소모품이거나 반납 정책이 없거나 아직 수령 전이면 null")
    LocalDateTime dueAt,
    @Schema(description = "반납 완료 시각. 아직 반납 전이면 null")
    LocalDateTime returnedAt,
    @Schema(description = "대여 상태")
    RentalStatus rentalStatus,
    @Schema(description = "물품 코드. 없으면 null", example = "우산1")
    String itemCode
) {

    public static AdminRentalHistoryResponse from(RentalRecord record, Member member) {
        RentalHistory history = record.history();
        Item item = record.item();
        LocalDateTime dueAt = (item != null && item.getReturnPolicy() != null && history.getRentAt() != null)
            ? item.getReturnPolicy().dueAt(history.getRentAt())
            : null;

        return new AdminRentalHistoryResponse(
            history.getId(),
            MemberSummary.of(history.getMemberId(), member),
            ItemSummary.from(item),
            history.getRentedCount(),
            history.getRentAt(),
            dueAt,
            history.getReturnedAt(),
            history.getRentalStatus(),
            history.getItemCode()
        );
    }

    public record MemberSummary(Long memberId, String name, String studentId, String department) {

        // member가 null이면 탈퇴(소프트 삭제)한 회원이다 — 이름 대신 placeholder, 학번·학부는 알 수 없어 null
        public static MemberSummary of(Long memberId, Member member) {
            return new MemberSummary(
                memberId,
                member != null ? member.getName() : ApiConstants.WITHDRAWN_MEMBER_LABEL,
                member != null ? member.getStudentId() : null,
                member != null ? member.getDepartment().displayName() : null
            );
        }
    }

    public record ItemSummary(Long itemId, String name) {

        // 물품이 삭제됐으면(하드 삭제) null이다
        public static ItemSummary from(Item item) {
            return item == null ? null : new ItemSummary(item.getId(), item.getName());
        }
    }
}
