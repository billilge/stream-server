package kr.ac.kookmin.stream.event.domain.locker.domain;

/**
 * 배치도에 그릴 사물함 한 건. 조회한 회원에 따라 달라지는 값은 담지 않는다.
 */
public record LockerAvailability(
    Long lockerId,
    String lockerLabel,
    int lockerNumber,
    int rowNo,
    int columnNo,
    boolean available
) {

    /**
     * @param applied 해당 운영 회차에 이 사물함이 이미 신청되었는지
     */
    public static LockerAvailability of(Locker locker, boolean applied) {
        return new LockerAvailability(
            locker.getId(),
            locker.getLockerLabel(),
            locker.getLockerNumber(),
            locker.getRowNo(),
            locker.getColumnNo(),
            locker.isSelectable(applied)
        );
    }
}
