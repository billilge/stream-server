package kr.ac.kookmin.stream.api.admin.event.archive.request;

/**
 * 아카이빙 목록을 좁히는 조건. 둘 다 없으면 전체를 조회한다.
 * <p>
 * {@code Boolean}을 {@code @RequestParam}으로 직접 받지 않고 record로 감싼다. 타입이 맞지 않는 값
 * ({@code ?isExposed=abc})이 오면 {@code @RequestParam}은 {@code MethodArgumentTypeMismatchException}을
 * 내는데, {@code GlobalExceptionHandler}가 그 예외를 다루지 않아 catch-all에 걸려 500이 된다.
 * {@code @ModelAttribute}로 받으면 바인딩 실패가 {@code MethodArgumentNotValidException}으로 나와
 * 400 {@code INVALID_INPUT}이 된다. {@code LockerPeriodParams}·{@code ArchiveListParams}와 같은 이유다.
 *
 * @param keyword             제목 일부. 비어 있으면 제목으로 좁히지 않는다
 * @param isExposed 슬랑제 페이지 노출 여부. {@code null}이면 노출 여부로 좁히지 않는다
 */
public record ArchiveSearchParams(
    String keyword,
    Boolean isExposed
) {
}
