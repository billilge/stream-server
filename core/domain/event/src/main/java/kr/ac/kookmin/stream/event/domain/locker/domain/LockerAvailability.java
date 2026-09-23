package kr.ac.kookmin.stream.event.domain.locker.domain;

/**
 * 배치도에 그릴 사물함 한 건. 선택 가능 여부는 사물함 자체 상태와 해당 운영 회차의 신청 여부를 함께 본다.
 */
public record LockerAvailability(
    Long lockerId,
    String lockerLabel,
    int lockerNumber,
    int rowNo,
    int columnNo,
    boolean available,
    boolean mine
) {

    /**
     * @param applied 해당 운영 회차에 이 사물함이 이미 신청되었는지
     * @param mine    그 신청이 조회한 회원의 것인지
     */
    public static LockerAvailability of(Locker locker, boolean applied, boolean mine) {
        return new LockerAvailability(
            locker.getId(),
            locker.getLockerLabel(),
            locker.getLockerNumber(),
            locker.getRowNo(),
            locker.getColumnNo(),
            locker.isSelectable(applied),
            mine
        );
    }
}
