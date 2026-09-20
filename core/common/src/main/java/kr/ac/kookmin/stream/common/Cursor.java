package kr.ac.kookmin.stream.common;

import java.util.List;

// keyset 커서 record가 구현하는 공통 계약. format() 시그니처를 강제해 구현체마다 이름이
// 갈리는 걸 막고, 문자열을 나누고/개수를 검증하는 부분(파싱 실패 처리)만 공유한다.
// 필드 구성이 구현체마다 달라 join(포맷팅) 자체는 공유하지 않는다.
public interface Cursor {

    String format();

    static List<String> parseParts(String raw, int expectedCount, ErrorCode invalidCursorError) {
        String[] parts = raw.split("\\|", -1);
        if (parts.length != expectedCount) {
            throw new BusinessException(invalidCursorError);
        }
        return List.of(parts);
    }
}
