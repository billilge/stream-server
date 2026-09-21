package kr.ac.kookmin.stream.welfare;

// welfare 도메인 모듈 전반에서 쓰는 상수 모음.
public final class WelfareConstants {

    // 한 학기당 회비 기본 금액. 관리자가 PUT /v1/admin/fee/amount로 바꾸기 전까지의 기본값.
    public static final long DEFAULT_TRANSFER_AMOUNT = 20_000L;

    private WelfareConstants() {}
}
