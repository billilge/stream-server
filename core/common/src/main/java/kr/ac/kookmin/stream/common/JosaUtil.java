package kr.ac.kookmin.stream.common;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 조사 유틸. 문구 템플릿의 {@code %s}에 값을 차례로 넣고, 템플릿에 적어 둔 "을(를)"·"이(가)"·"은(는)"·"과(와)"를
 * 앞 단어의 받침에 맞는 조사 하나로 바꾼다.
 * <p>
 * 앞 단어는 따옴표·괄호·문장부호·공백을 건너뛴 마지막 글자로 판단한다("'봄 축제!'을(를)" → "'봄 축제!'를").
 * 한글 음절과 숫자만 판단하고, 영문처럼 판단할 수 없으면 "을(를)" 표기를 그대로 둔다.
 * 값(행사명 등) 안에 같은 표기가 있어도 바꾸지 않도록 템플릿 부분만 바꾼다.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JosaUtil {

    private static final String PLACEHOLDER = "%s";
    private static final Pattern PARTICLE = Pattern.compile("(을|이|은|과)\\((를|가|는|와)\\)");
    // 0(영)·1(일)·3(삼)·6(육)·7(칠)·8(팔)은 받침이 있다
    private static final String DIGITS_WITH_FINAL_CONSONANT = "013678";

    private static final char HANGUL_SYLLABLE_FIRST = '가';
    private static final char HANGUL_SYLLABLE_LAST = '힣';
    private static final int FINAL_CONSONANT_COUNT = 28;

    public static String format(String template, List<String> values) {
        String[] segments = template.split(PLACEHOLDER, -1);
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < segments.length; i++) {
            appendResolved(formatted, segments[i]);
            if (i < segments.length - 1) {
                formatted.append(values.get(i));
            }
        }
        return formatted.toString();
    }

    // 조사 표기 앞의 단어는 이미 붙여 둔 앞부분과 이 조각에서 표기 앞까지를 이어서 본다
    private static void appendResolved(StringBuilder formatted, String segment) {
        Matcher matcher = PARTICLE.matcher(segment);
        int copiedUntil = 0;
        while (matcher.find()) {
            formatted.append(segment, copiedUntil, matcher.start());
            Boolean hasFinalConsonant = hasFinalConsonant(lastLetterOrDigit(formatted));
            formatted.append(hasFinalConsonant == null
                ? matcher.group()
                : hasFinalConsonant ? matcher.group(1) : matcher.group(2));
            copiedUntil = matcher.end();
        }
        formatted.append(segment, copiedUntil, segment.length());
    }

    private static Character lastLetterOrDigit(CharSequence text) {
        for (int i = text.length() - 1; i >= 0; i--) {
            char c = text.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                return c;
            }
        }
        return null;
    }

    // 판단할 수 없으면 null
    private static Boolean hasFinalConsonant(Character c) {
        if (c == null) {
            return null;
        }
        if (c >= HANGUL_SYLLABLE_FIRST && c <= HANGUL_SYLLABLE_LAST) {
            return (c - HANGUL_SYLLABLE_FIRST) % FINAL_CONSONANT_COUNT != 0;
        }
        if (Character.isDigit(c)) {
            return DIGITS_WITH_FINAL_CONSONANT.indexOf(c) >= 0;
        }
        return null;
    }
}
