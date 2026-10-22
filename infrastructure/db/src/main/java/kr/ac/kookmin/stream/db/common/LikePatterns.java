package kr.ac.kookmin.stream.db.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * {@code LIKE} 검색어 유틸. 검색어를 정리하고 이스케이프한다.
 * <p>
 * 사용자 입력을 {@code CONCAT('%', :keyword, '%')}로 그대로 붙이면 검색어 안의 {@code %}·{@code _}가 와일드카드로
 * 동작해서 {@code %}만 입력해도 모든 행이 걸린다. 검색어를 {@link #escape(String)}로 정리·이스케이프해서 넘기고, 쿼리는
 * {@code LIKE CONCAT('%', :keyword, '%') ESCAPE '!'}로 받는다.
 * <p>
 * 이스케이프 문자는 {@code \}가 아니라 {@code !}다. MySQL은 문자열 리터럴에서 {@code \}를 이스케이프로 먹기 때문에
 * {@code ESCAPE '\'}를 쓰려면 SQL을 {@code '\\'}로 적어야 하는데, JPQL을 거치면 이 이중 이스케이프가 어긋나기 쉽다.
 * <p>
 * 쿼리의 {@code ESCAPE} 문자는 {@link #ESCAPE}와 같아야 한다.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LikePatterns {

    /** 쿼리의 {@code ESCAPE '!'}와 같아야 한다. */
    public static final char ESCAPE = '!';

    /**
     * 검색어를 {@code LIKE}에 넘길 수 있게 정리하고 이스케이프한다. 앞뒤 공백을 지우고, 검색어 안의 {@code %}·{@code _}와
     * 이스케이프 문자를 글자 그대로 검색되도록 앞에 이스케이프 문자를 붙인다.
     *
     * @return 정리·이스케이프한 검색어. {@code raw}가 null이거나 공백뿐이면 "검색 없음"이라 null
     */
    public static String escape(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }

        String keyword = raw.strip();
        StringBuilder escaped = new StringBuilder(keyword.length() + 4);
        for (char c : keyword.toCharArray()) {
            if (c == ESCAPE || c == '%' || c == '_') {
                escaped.append(ESCAPE);
            }
            escaped.append(c);
        }
        return escaped.toString();
    }
}
