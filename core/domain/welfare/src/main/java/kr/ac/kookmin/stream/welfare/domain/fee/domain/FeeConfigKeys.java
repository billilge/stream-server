package kr.ac.kookmin.stream.welfare.domain.fee.domain;

// admin_config_values에 저장되는 회비 송금 관련 키. 계좌(은행/계좌번호)와 금액을 따로 저장해서,
// 계좌만 바뀌거나 금액만 바뀌는 경우에도 서로 건드리지 않는다 — 학생에게 보여줄 링크는
// 조회 시점에 이 셋을 조합해 만든다(TossTransferLinkGenerator).
public final class FeeConfigKeys {

    public static final String TRANSFER_BANK = "FEE_TRANSFER_BANK";
    public static final String TRANSFER_ACCOUNT_NO = "FEE_TRANSFER_ACCOUNT_NO";
    public static final String TRANSFER_AMOUNT = "FEE_TRANSFER_AMOUNT";

    // 한 학기당 회비 기본 금액. 관리자가 PUT /v1/admin/fee/amount로 바꾸기 전까지의 기본값.
    public static final long DEFAULT_TRANSFER_AMOUNT = 20_000L;

    private FeeConfigKeys() {}
}
