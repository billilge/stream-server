package kr.ac.kookmin.stream.welfare.domain.rental.domain;

/**
 * @param baseCount 수정 폼을 열 때 조회한 재고. {@code count}와의 차이만큼만 지금 재고를 증감한다.
 * @param imageKey null이면 기존 이미지를 유지한다({@code AdminItemServiceImpl}에서 처리).
 */
public record ItemUpdateCommand(
    String name,
    ItemCategory category,
    ItemType type,
    int count,
    int baseCount,
    String imageKey,
    ReturnPolicy returnPolicy
) {
}
