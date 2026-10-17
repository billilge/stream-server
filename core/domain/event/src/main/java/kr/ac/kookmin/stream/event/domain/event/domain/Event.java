package kr.ac.kookmin.stream.event.domain.event.domain;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    private String place;
    private LocalDateTime eventStartAt;
    private LocalDateTime eventEndAt;
    private LocalDateTime applyStartAt;
    private LocalDateTime applyEndAt;
    private RecruitType recruitType;
    private List<Long> imageIds;
    private int capacity;
    /** 학생회비 납부자만 신청할 수 있는지. 신청 시 검증은 아직 없다(후속 작업) */
    private boolean feePaidOnly;
    private boolean published;
    private Long createdBy;
    /** 마지막 수정 시각. DB가 채우며, 운영진 동시 수정 감지에 쓴다 */
    private LocalDateTime updatedAt;

    public static Event of(
        Long id,
        String title,
        String description,
        String place,
        LocalDateTime eventStartAt,
        LocalDateTime eventEndAt,
        LocalDateTime applyStartAt,
        LocalDateTime applyEndAt,
        RecruitType recruitType,
        List<Long> imageIds,
        int capacity,
        boolean feePaidOnly,
        boolean published,
        Long createdBy,
        LocalDateTime updatedAt
    ) {
        return new Event(
            id, title, description, place, eventStartAt, eventEndAt, applyStartAt,
            applyEndAt, recruitType, imageIds, capacity, feePaidOnly, published, createdBy, updatedAt
        );
    }

    /**
     * 새 행사를 만든다. 학생에게 바로 보이지 않도록 비공개로 시작한다. 상시 모집은 정원이 의미 없어 0으로 둔다.
     * 수정 시각은 DB가 채운다.
     */
    public static Event create(Long createdBy, EventCreateCommand command) {
        return new Event(
            null,
            command.title(),
            command.description(),
            command.place(),
            command.eventStartAt(),
            command.eventEndAt(),
            command.applyStartAt(),
            command.applyEndAt(),
            command.recruitType(),
            command.imageIds(),
            capacityOf(command.recruitType(), command.capacity()),
            command.feePaidOnly(),
            false,
            createdBy,
            null
        );
    }

    /**
     * 운영진이 입력한 값으로 행사 내용을 통째로 바꾼다. 공개 여부·작성자는 그대로 둔다.
     */
    public void update(EventUpdateCommand command) {
        this.title = command.title();
        this.description = command.description();
        this.place = command.place();
        this.eventStartAt = command.eventStartAt();
        this.eventEndAt = command.eventEndAt();
        this.applyStartAt = command.applyStartAt();
        this.applyEndAt = command.applyEndAt();
        this.recruitType = command.recruitType();
        this.imageIds = command.imageIds();
        this.capacity = capacityOf(command.recruitType(), command.capacity());
        this.feePaidOnly = command.feePaidOnly();
    }

    /**
     * 현재 시각과 신청자 수를 반영한 모집 상태를 계산한다.
     * <p>
     * 모집 상태는 저장하지 않고 신청 기간과 잔여 정원으로만 판정한다.
     * 행사 상세 조회·신청서 폼 조회·행사 신청이 같은 기준을 써야 하므로 도메인에 둔다.
     *
     * @param appliedCount status가 APPLIED인 신청 수. 선착순 모집이 아니면 쓰이지 않는다
     */
    public RecruitStatus calculateRecruitStatus(LocalDateTime now, long appliedCount) {
        if (isAfterApplyPeriod(now)) {
            return RecruitStatus.CLOSED;
        }
        if (isBeforeApplyPeriod(now)) {
            return RecruitStatus.BEFORE_OPEN;
        }
        if (isCapacityFull(appliedCount)) {
            return RecruitStatus.CLOSED;
        }
        return RecruitStatus.OPEN;
    }

    /**
     * 신청 마감까지 남은 날짜 수. 목록·상세가 같은 D-Day를 보여야 하므로 도메인에 둔다.
     * <p>
     * 시각이 아니라 날짜 단위로 세므로 마감 당일은 언제든 0이다. D-Day 배지는 모집 중일 때만 노출하기로 해
     * 그 외 상태에서는 값을 내려보내지 않는다.
     *
     * @param recruitStatus {@link #calculateRecruitStatus}로 이미 계산해둔 모집 상태
     * @return 모집 중이면 남은 날짜 수, 그 외에는 {@code null}
     */
    public Integer daysUntilDeadline(LocalDateTime now, RecruitStatus recruitStatus) {
        if (recruitStatus != RecruitStatus.OPEN) {
            return null;
        }
        return (int) ChronoUnit.DAYS.between(now.toLocalDate(), applyEndAt.toLocalDate());
    }

    /**
     * 대표 이미지 파일 id. 이미지 목록의 첫 장을 대표로 쓴다. 목록·신청 내역이 같은 장을 보여야 하므로 도메인에 둔다.
     */
    public Long thumbnailFileId() {
        return imageIds == null || imageIds.isEmpty() ? null : imageIds.getFirst();
    }

    /**
     * 운영진 화면에 보여줄 정원. 상시 모집은 인원 제한이 없어 {@code null}이다(저장값 0은 의미가 없다).
     */
    public Integer capacityLimit() {
        return recruitType == RecruitType.FIRST_COME ? capacity : null;
    }

    /**
     * 정원이 찼는지 판정한다. 선착순 모집에만 정원 제한이 있고, 상시 모집은 인원 제한이 없다.
     */
    public boolean isCapacityFull(long appliedCount) {
        return recruitType == RecruitType.FIRST_COME && appliedCount >= capacity;
    }

    /**
     * 정원 때문에만 닫힌 상태인지. 모집 상태 계산은 기간 종료·정원 마감을 모두 CLOSED로 합치지만,
     * 신청 실패 사유는 이 둘을 구분해야 하므로 정원이 유일한 사유일 때를 따로 판정한다.
     */
    public boolean isClosedByCapacityOnly(LocalDateTime now, long appliedCount) {
        return !isBeforeApplyPeriod(now)
            && !isAfterApplyPeriod(now)
            && isCapacityFull(appliedCount);
    }

    private static int capacityOf(RecruitType recruitType, Integer capacity) {
        return recruitType == RecruitType.FIRST_COME ? capacity : 0;
    }

    private boolean isBeforeApplyPeriod(LocalDateTime now) {
        return now.isBefore(applyStartAt);
    }

    /**
     * 신청 기간이 끝났는지. 모집 상태 계산뿐 아니라 신청 취소 가능 여부 판정에도 쓰인다.
     */
    public boolean isAfterApplyPeriod(LocalDateTime now) {
        return now.isAfter(applyEndAt);
    }
}
