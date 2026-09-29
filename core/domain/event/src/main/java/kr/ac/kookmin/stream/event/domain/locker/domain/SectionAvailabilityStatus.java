package kr.ac.kookmin.stream.event.domain.locker.domain;

/**
 * 구역에 남은 사물함 수로 정해지는 표시 상태.
 * <p>
 * 판정에 구역의 속성이 쓰이지 않고 선택 가능 수와 전체 수만 필요해, 상태 타입이 자기 생성을 소유한다.
 * 구역별로 임계값이 달라지면 그때 {@link LockerSection}이 임계값을 갖고 판정을 가져가는 편이 맞다.
 */
public enum SectionAvailabilityStatus {

    PLENTY,
    NORMAL,
    ALMOST_FULL,
    FULL;

    private static final double PLENTY_RATE = 0.5;
    private static final double NORMAL_RATE = 0.2;

    /**
     * @param availableCount 선택 가능한 사물함 수
     * @param totalCount     구역의 전체 사물함 수(사용 중지된 사물함 포함)
     */
    public static SectionAvailabilityStatus from(int availableCount, int totalCount) {
        // 사물함이 하나도 없는 구역의 0 나누기도 여기서 함께 걸린다
        if (availableCount <= 0) {
            return FULL;
        }

        double availabilityRate = (double) availableCount / totalCount;
        if (availabilityRate >= PLENTY_RATE) {
            return PLENTY;
        }
        if (availabilityRate >= NORMAL_RATE) {
            return NORMAL;
        }
        return ALMOST_FULL;
    }
}
