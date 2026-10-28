package kr.ac.kookmin.stream.welfare.domain.rental.domain;

/**
 * @param imageKey null이면 기존 이미지를 유지한다({@code AdminItemServiceImpl}에서 처리).
 */
public record ItemUpdateCommand(
    String name,
    ItemCategory category,
    ItemType type,
    int count,
    String imageKey,
    ReturnPolicy returnPolicy
) {
}
