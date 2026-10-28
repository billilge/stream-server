package kr.ac.kookmin.stream.api.admin.welfare.rental.usecase;

import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalRecord;

/**
 * 대여 기록 하나에 회원·처리자 정보를 묶은 조회용 조합 객체. {@code welfare}(RentalRecord)와 {@code member}
 * 두 도메인을 UseCase에서 조합한 결과라, 필드를 복사하지 않고 도메인 객체를 그대로 담는다
 * ({@code MemberFeeStatus}와 같은 패턴). 어느 쪽이든 탈퇴·삭제된 회원이면 null이다.
 */
public record AdminRentalRecordView(RentalRecord record, Member member, Member worker) {
}
