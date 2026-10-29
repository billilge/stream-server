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
    private EventPeriod eventPeriod;
    private EventPeriod applyPeriod;
    private RecruitPolicy recruitPolicy;
    private List<Long> imageIds;
    private boolean published;
    private Long createdBy;
    /** 마지막 수정 시각. DB가 채우며, 운영진 동시 수정 감지에 쓴다 */
    private LocalDateTime updatedAt;

    /**
     * DB에 저장된 값으로 행사를 복원한다. 기간·모집 값은 표준 생성자로 만들어 검증하지 않는다. 저장값은 등록·수정 때 이미
     * 검증했고, {@code event_end_at}이 NULL인 기존 행이 있어 검증하면 조회가 깨진다.
     * <p>
     * 이미지 목록이 없으면 빈 목록으로 바꿔 둔다. 도메인 안에서는 목록이 항상 있다고 보고 null을 확인하지 않는다.
     */
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
            id, title, description, place, new EventPeriod(eventStartAt, eventEndAt),
            new EventPeriod(applyStartAt, applyEndAt), new RecruitPolicy(recruitType, capacity, feePaidOnly),
            imageIds == null ? List.of() : imageIds, published, createdBy, updatedAt
        );
    }

    /**
     * 새 행사를 만든다. 학생에게 바로 보이지 않도록 비공개로 시작한다. 수정 시각은 DB가 채운다.
     */
    public static Event create(Long createdBy, EventCreateCommand command) {
        return new Event(
            null,
            command.title(),
            command.description(),
            command.place(),
            command.eventPeriod(),
            command.applyPeriod(),
            command.recruitPolicy(),
            command.imageIds(),
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
        this.eventPeriod = command.eventPeriod();
        this.applyPeriod = command.applyPeriod();
        this.recruitPolicy = command.recruitPolicy();
        this.imageIds = command.imageIds();
    }

    // 기간·모집 값을 VO로 묶기 전부터 쓰던 getter다. 호출부가 VO 구조를 몰라도 되도록 남겨 둔다
    public LocalDateTime getEventStartAt() {
        return eventPeriod.startAt();
    }

    public LocalDateTime getEventEndAt() {
        return eventPeriod.endAt();
    }

    public LocalDateTime getApplyStartAt() {
        return applyPeriod.startAt();
    }

    public LocalDateTime getApplyEndAt() {
        return applyPeriod.endAt();
    }

    public RecruitType getRecruitType() {
        return recruitPolicy.type();
    }

    public int getCapacity() {
        return recruitPolicy.capacity();
    }

    public boolean isFeePaidOnly() {
        return recruitPolicy.feePaidOnly();
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
        return (int) ChronoUnit.DAYS.between(now.toLocalDate(), applyPeriod.endAt().toLocalDate());
    }

    /**
     * 대표 이미지 파일 id. 이미지 목록의 첫 장을 대표로 쓴다. 목록·신청 내역이 같은 장을 보여야 하므로 도메인에 둔다.
     */
    public Long thumbnailFileId() {
        return imageIds.isEmpty() ? null : imageIds.getFirst();
    }

    /** 학생 앱 노출 여부를 바꾼다. 이미 그 상태여도 그대로 둔다. */
    public void changePublished(boolean published) {
        this.published = published;
    }

    /**
     * 운영진 화면에 보여줄 정원. 규칙은 {@link RecruitPolicy#capacityLimit()}를 따른다.
     */
    public Integer capacityLimit() {
        return recruitPolicy.capacityLimit();
    }

    /**
     * 정원이 찼는지 판정한다. 규칙은 {@link RecruitPolicy#isFull(long)}을 따른다.
     */
    public boolean isCapacityFull(long appliedCount) {
        return recruitPolicy.isFull(appliedCount);
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

    private boolean isBeforeApplyPeriod(LocalDateTime now) {
        return now.isBefore(applyPeriod.startAt());
    }

    /**
     * 신청 기간이 끝났는지. 모집 상태 계산뿐 아니라 신청 취소 가능 여부 판정에도 쓰인다.
     */
    public boolean isAfterApplyPeriod(LocalDateTime now) {
        return now.isAfter(applyPeriod.endAt());
    }
}
