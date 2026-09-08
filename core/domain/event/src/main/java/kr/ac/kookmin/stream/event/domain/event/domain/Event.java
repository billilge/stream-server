package kr.ac.kookmin.stream.event.domain.event.domain;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Event {

    private Long id;
    private String title;
    private String description;
    private String target;
    private String place;
    private LocalDateTime eventStartAt;
    private LocalDateTime eventEndAt;
    private LocalDateTime applyStartAt;
    private LocalDateTime applyEndAt;
    private RecruitType recruitType;
    private List<Long> imageIds;
    private int capacity;
    private RecruitStatus recruitStatus;
    private Long createdBy;

    public static Event of(
        Long id,
        String title,
        String description,
        String target,
        String place,
        LocalDateTime eventStartAt,
        LocalDateTime eventEndAt,
        LocalDateTime applyStartAt,
        LocalDateTime applyEndAt,
        RecruitType recruitType,
        List<Long> imageIds,
        int capacity,
        RecruitStatus recruitStatus,
        Long createdBy
    ) {
        return new Event(
            id, title, description, target, place, eventStartAt, eventEndAt, applyStartAt,
            applyEndAt, recruitType, imageIds, capacity, recruitStatus, createdBy
        );
    }
}
