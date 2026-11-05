package kr.ac.kookmin.stream.api.admin.excel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * 운영진 화면의 엑셀 내보내기 파일. 시트 하나에 헤더 한 줄과 데이터 줄을 담아 xlsx로 내려준다. 헤더 모양(굵게, 회색 배경)은
 * 레거시 서버의 엑셀 내보내기를 따른다.
 * <p>
 * 값은 모두 문자열 셀로 넣는다. 숫자로 바뀌어 전화번호 앞자리 0이 사라지거나, 학생이 입력한 답이 수식으로 실행되지 않는다.
 * 내보내는 행이 수백 건 수준이라 스트리밍 없이 메모리에서 만든다.
 *
 * @param fileName 내려받을 파일 이름(확장자 포함). 한글이 들어가도 된다
 * @param rows     한 줄의 값은 {@code headers}와 같은 순서. {@code null}인 값은 빈 칸이 된다
 */
public record ExcelFile(String fileName, String sheetName, List<String> headers, List<List<String>> rows) {

    public static final String MEDIA_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    // 열 너비 단위는 글자 폭의 1/256이다. 한글은 영문 두 글자 폭이라 글자 수에 2를 곱해 넉넉히 잡고, 긴 답이 열을 끝없이
    // 넓히지 않도록 상한을 둔다
    private static final int WIDTH_PER_CHAR = 2 * 256;
    private static final int MIN_COLUMN_WIDTH = 8 * 256;
    private static final int MAX_COLUMN_WIDTH = 60 * 256;

    public ResponseEntity<byte[]> toResponseEntity() {
        ContentDisposition contentDisposition = ContentDisposition.attachment()
            .filename(fileName, StandardCharsets.UTF_8)
            .build();
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(MEDIA_TYPE))
            .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
            .body(write());
    }

    private byte[] write() {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(WorkbookUtil.createSafeSheetName(sheetName));
            writeRow(sheet.createRow(0), headers, headerStyle(workbook));
            for (int i = 0; i < rows.size(); i++) {
                writeRow(sheet.createRow(i + 1), rows.get(i), null);
            }
            fitColumnWidths(sheet);
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException("엑셀 파일을 만들지 못했습니다.", e);
        }
    }

    private static void writeRow(Row row, List<String> values, CellStyle style) {
        for (int column = 0; column < values.size(); column++) {
            String value = values.get(column);
            if (value == null) {
                continue;
            }
            Cell cell = row.createCell(column);
            cell.setCellValue(value);
            if (style != null) {
                cell.setCellStyle(style);
            }
        }
    }

    private static CellStyle headerStyle(Workbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);

        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    // 열마다 가장 긴 값에 맞춘다
    private void fitColumnWidths(Sheet sheet) {
        for (int column = 0; column < headers.size(); column++) {
            int maxLength = headers.get(column).length();
            for (List<String> row : rows) {
                String value = column < row.size() ? row.get(column) : null;
                if (value != null) {
                    maxLength = Math.max(maxLength, value.length());
                }
            }
            int width = Math.clamp((long) (maxLength + 1) * WIDTH_PER_CHAR, MIN_COLUMN_WIDTH, MAX_COLUMN_WIDTH);
            sheet.setColumnWidth(column, width);
        }
    }
}
