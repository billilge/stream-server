package kr.ac.kookmin.stream.welfare.domain.fee.domain;

import kr.ac.kookmin.stream.common.BusinessException;

// https://github.com/nemokoala/TossMe 의 주요 은행 목록과 동일한 값·순서.
// bank 파라미터는 이 목록의 표시명(예: "국민")만 허용한다.
public enum Bank {
    GYEONGNAM("경남"),
    GWANGJU("광주"),
    IBK("기업"),
    KOOKMIN("국민"),
    DAEGU("대구"),
    BUSAN("부산"),
    SANLIM("산림"),
    SAEMAUL("새마을"),
    SC_JEIL("SC제일"),
    SHINHAN("신한"),
    SHINHYEOP("신협"),
    SUHYEOP("수협"),
    K_BANK("케이뱅크"),
    WOORI("우리"),
    POST_OFFICE("우체국"),
    SAVINGS_BANK("저축은행"),
    JEONBUK("전북"),
    JEJU("제주"),
    KAKAO_BANK("카카오뱅크"),
    TOSS_BANK("토스뱅크"),
    HANA("하나"),
    NONGHYEOP("농협");

    private final String displayName;

    Bank(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }

    // 잘못된 은행명은 컨트롤러가 아니라 여기서 막아, 유효한 값만 admin_config_values에 저장되게 한다.
    public static Bank from(String displayName) {
        for (Bank bank : values()) {
            if (bank.displayName.equals(displayName)) {
                return bank;
            }
        }
        throw new BusinessException(FeeErrorCode.INVALID_BANK);
    }
}
