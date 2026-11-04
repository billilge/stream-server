package kr.ac.kookmin.stream.api.admin.event.event.response;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import kr.ac.kookmin.stream.api.admin.event.event.usecase.EventApplicant;
import kr.ac.kookmin.stream.api.admin.event.event.usecase.EventApplicantList;
import kr.ac.kookmin.stream.api.common.ApiConstants;
import kr.ac.kookmin.stream.api.common.excel.ExcelFile;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationStatus;
import kr.ac.kookmin.stream.event.domain.event.domain.EventQuestion;

/**
 * 신청 현황 엑셀 내보내기. 앞 열은 신청 현황 화면과 같고, 질문별 답은 질문 순서대로 뒤에 붙인다.
 * 답은 신청 현황 표와 같은 문구({@link AdminEventApplicationListResponse.Answer})를 쓴다.
 */
public final class AdminEventApplicationExcel {

    private static final List<String> APPLICANT_HEADERS = List.of("이름", "학번", "전화번호", "신청일시", "상태");
    private static final DateTimeFormatter APPLIED_AT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    // 파일 이름에 쓸 수 없는 글자. 행사명에 들어 있으면 밑줄로 바꾼다
    private static final Pattern FILE_NAME_FORBIDDEN = Pattern.compile("[\\\\/:*?\"<>|]");

    private AdminEventApplicationExcel() {}

    /**
     * @param today 파일 이름에 붙일 내보낸 날짜
     */
    public static ExcelFile of(EventApplicantList applicantList, LocalDate today) {
        List<String> headers = new ArrayList<>(APPLICANT_HEADERS);
        applicantList.event().questions().stream().map(EventQuestion::getQuestionText).forEach(headers::add);

        String eventTitle = FILE_NAME_FORBIDDEN.matcher(applicantList.event().event().getTitle()).replaceAll("_");
        String fileName = eventTitle + "_신청자_" + today.format(DateTimeFormatter.BASIC_ISO_DATE) + ".xlsx";

        return new ExcelFile(
            fileName,
            "신청자",
            headers,
            applicantList.applicants().stream().map(AdminEventApplicationExcel::toRow).toList()
        );
    }

    // 값이 없는 칸은 null로 두어 빈 칸으로 만든다
    private static List<String> toRow(EventApplicant applicant) {
        List<String> row = new ArrayList<>();
        row.add(applicant.member() == null ? ApiConstants.WITHDRAWN_MEMBER_LABEL : applicant.memberName());
        row.add(applicant.memberStudentId());
        row.add(applicant.memberPhoneNumber());
        row.add(applicant.application().getAppliedAt().format(APPLIED_AT_FORMAT));
        row.add(statusLabel(applicant.application().getStatus()));
        applicant.answeredQuestions().stream()
            .map(answeredQuestion -> AdminEventApplicationListResponse.Answer.from(answeredQuestion).displayText())
            .forEach(row::add);
        return row;
    }

    // 관리자 화면의 상태 표시와 같은 문구
    private static String statusLabel(EventApplicationStatus status) {
        return switch (status) {
            case APPLIED -> "신청완료";
            case CANCELED -> "취소";
        };
    }
}
