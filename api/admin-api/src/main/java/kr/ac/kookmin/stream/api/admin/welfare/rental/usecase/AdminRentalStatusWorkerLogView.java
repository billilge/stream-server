package kr.ac.kookmin.stream.api.admin.welfare.rental.usecase;

import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalStatusWorkerLog;

/** 처리자 로그 한 건에 처리자 정보를 묶은 조회용 조합 객체. */
public record AdminRentalStatusWorkerLogView(RentalStatusWorkerLog log, Member worker) {
}
