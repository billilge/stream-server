package kr.ac.kookmin.stream.welfare.domain.rental.domain;

// admin_config_values에 저장되는 대여 이용 제한 기간 설정 키. 날짜는 ISO-8601 문자열(yyyy-MM-dd)로 저장한다.
public final class RentalBlockPeriodConfigKeys {

    public static final String RENTAL_BLOCK_START = "RENTAL_BLOCK_START";
    public static final String RENTAL_BLOCK_END = "RENTAL_BLOCK_END";
    public static final String RENTAL_BLOCK_REASON = "RENTAL_BLOCK_REASON";

    private RentalBlockPeriodConfigKeys() {}
}
