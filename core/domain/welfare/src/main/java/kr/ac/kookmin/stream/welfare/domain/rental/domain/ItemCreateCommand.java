package kr.ac.kookmin.stream.welfare.domain.rental.domain;

public record ItemCreateCommand(
    String name,
    ItemCategory category,
    ItemType type,
    int count,
    String imageKey,
    ReturnPolicy returnPolicy
) {
}
