package kr.ac.kookmin.stream.common;

import java.util.List;

// keyset 커서 record가 구현하는 공통 계약. 각 커서는 자기 필드를 문자열 리스트로 바꾸는 방법만 알면 되고,
// 그 리스트를 합치고/나누고/개수를 검증하는 부분은 여기서 공유한다.
public interface Cursor {

    List<String> toParts();

    default String format() {
        return String.join("|", toParts());
    }

    static List<String> parseParts(String raw, int expectedCount, ErrorCode invalidCursorError) {
        String[] parts = raw.split("\\|", -1);
        if (parts.length != expectedCount) {
            throw new BusinessException(invalidCursorError);
        }
        return List.of(parts);
    }
}
