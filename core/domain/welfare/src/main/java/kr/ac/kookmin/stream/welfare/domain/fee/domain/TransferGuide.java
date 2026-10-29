package kr.ac.kookmin.stream.welfare.domain.fee.domain;

/**
 * 학생에게 지금 안내할 송금 정보 — 입력한 남은 학기 수, 그 값으로 계산한 금액, 금액이 채워진 토스 송금 링크.
 * 조회할 때마다 계산하고 저장하지 않는다. 요청 때 저장해 두는 금액은 StudentTransferStatus의 amount다.
 */
public record TransferGuide(Integer remainingSemesters, Long amount, String linkUrl) {

    public static TransferGuide of(int remainingSemesters, long amount, String linkUrl) {
        return new TransferGuide(remainingSemesters, amount, linkUrl);
    }

    // 남은 학기 수를 받지 않아 계산하지 않았다. 응답에서 남은 학기 수·금액·링크가 비어 내려간다
    public static TransferGuide notCalculated() {
        return new TransferGuide(null, null, null);
    }
}
