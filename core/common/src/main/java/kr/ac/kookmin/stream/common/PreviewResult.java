package kr.ac.kookmin.stream.common;

import java.util.List;

/**
 * 앞의 몇 건과 전체 수. 홈처럼 "미리보기 N건 + 더보기"를 그리는 화면이 쓴다.
 * <p>
 * 커서({@link CursorSliceResult})나 페이지({@link PageResult})와 달리 이어서 더 읽지 않는다. 전체를 돌려주고
 * 화면이 자르면 목록이 긴 회원의 홈에 이력 전체가 실리므로, 자르는 일을 조회가 맡고 전체 수를 함께 준다.
 *
 * @param items 미리보기 목록. 전체 수보다 적을 수 있다
 */
public record PreviewResult<T>(List<T> items, long totalCount) {
}
