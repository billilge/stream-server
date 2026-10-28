package kr.ac.kookmin.stream.welfare.domain.rental.domain;

// admin_config_values에 저장되는 시험기간 설정 키. 값은 ISO-8601 날짜 문자열(yyyy-MM-dd)로 저장한다.
public final class ExamPeriodConfigKeys {

    public static final String EXAM_PERIOD_START = "EXAM_PERIOD_START";
    public static final String EXAM_PERIOD_END = "EXAM_PERIOD_END";

    private ExamPeriodConfigKeys() {}
}
