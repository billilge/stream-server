package kr.ac.kookmin.stream.api.admin.core.member.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/** 회원들을 관리자 관리 명단에서 뺀다. 권한을 학생으로 되돌리고 학생회 부서를 비운다. */
public record CouncilReleaseRequest(

    @NotEmpty(message = "해제할 회원을 한 명 이상 선택해 주세요.")
    @Schema(description = "해제할 회원 식별자", example = "[1, 2]")
    List<@NotNull(message = "회원 식별자는 비워 둘 수 없습니다.") Long> memberIds
) {

    public List<Long> distinctMemberIds() {
        return memberIds.stream().distinct().toList();
    }
}
