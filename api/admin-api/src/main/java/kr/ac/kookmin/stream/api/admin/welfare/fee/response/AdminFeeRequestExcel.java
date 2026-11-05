package kr.ac.kookmin.stream.api.admin.welfare.fee.response;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import kr.ac.kookmin.stream.api.admin.excel.ExcelFile;
import kr.ac.kookmin.stream.api.admin.welfare.fee.usecase.MemberFeeStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferStatus;

/**
 * 납부 확인 요청 엑셀 내보내기. 열과 표시 문구는 관리자 웹이 직접 만들던 명단 파일과 같다. 이름·학번·학과는 목록 응답
 * ({@link AdminFeeSearchResponse})과 같은 값을 쓴다.
 */
public final class AdminFeeRequestExcel {

    private static final List<String> HEADERS =
        List.of("이름", "학번", "학과", "요청일시", "남은 학기", "요청 금액", "납부일", "납부 상태", "관리자 안내");
    private static final DateTimeFormatter REQUESTED_AT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private AdminFeeRequestExcel() {}

    /**
     * @param today 파일 이름에 붙일 내보낸 날짜
     */
    public static ExcelFile of(List<MemberFeeStatus> requests, LocalDate today) {
        return new ExcelFile(
            "학생회비_납부확인요청_" + today.format(DateTimeFormatter.BASIC_ISO_DATE) + ".xlsx",
            "납부 확인 요청",
            HEADERS,
            requests.stream().map(AdminFeeRequestExcel::toRow).toList()
        );
    }

    // 값이 없는 칸은 null로 두어 빈 칸으로 만든다
    private static List<String> toRow(MemberFeeStatus request) {
        AdminFeeSearchResponse listRow = AdminFeeSearchResponse.from(request);
        StudentTransferStatus transferStatus = request.transferStatus();
        return Arrays.asList(
            listRow.name(),
            listRow.studentId(),
            listRow.department(),
            transferStatus.getRequestedAt().format(REQUESTED_AT_FORMAT),
            transferStatus.getRemainingSemesters() == null ? null : transferStatus.getRemainingSemesters() + "학기",
            transferStatus.getAmount() == null ? null : String.format(Locale.KOREA, "%,d원", transferStatus.getAmount()),
            paidDate(transferStatus),
            statusLabel(transferStatus.getStatus()),
            transferStatus.getRejectReason()
        );
    }

    // 납부일은 납부 완료로 처리한 날짜다. 다른 상태로 되돌린 요청은 처리 시각이 남아 있어도 비운다
    private static String paidDate(StudentTransferStatus transferStatus) {
        if (transferStatus.getStatus() != TransferStatus.PAID || transferStatus.getConfirmedAt() == null) {
            return null;
        }
        return transferStatus.getConfirmedAt().toLocalDate().toString();
    }

    // 관리자 화면의 납부 상태 표시와 같은 문구
    private static String statusLabel(TransferStatus status) {
        return switch (status) {
            case PENDING -> "납부확인중";
            case PAID -> "납부완료";
            case REJECTED -> "확인필요";
        };
    }
}
