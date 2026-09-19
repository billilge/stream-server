package kr.ac.kookmin.stream.api.admin.welfare.fee.usecase;

import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferStatus;

// AdminFeeSearchUseCase가 조합한 결과. DTO(AdminFeeSearchResponse) 변환은 Controller가 한다
// (PageResult<도메인>까지만 UseCase가 만들고, api DTO 조립은 경계에서 하기 위함).
// member는 탈퇴(소프트 삭제)한 회원의 과거 요청이면 null일 수 있다.
public record MemberFeeStatus(StudentTransferStatus status, Member member) {
}
