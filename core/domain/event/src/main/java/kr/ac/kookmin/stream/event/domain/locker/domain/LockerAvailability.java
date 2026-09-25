package kr.ac.kookmin.stream.event.domain.locker.domain;

/**
 * 배치도에 그릴 사물함 한 건. 선택 가능 여부는 사물함 자체 상태와 해당 운영 회차의 신청 여부를 함께 본다.
 * <p>
 * 조회한 회원에 따라 달라지는 값은 담지 않는다. 내 사물함 표시처럼 보는 사람이 기준인 값은 이 결과에
 * {@code LockerService#getLockerByMemberId}로 읽은 사물함을 맞춰봐서 표현 계층에서 만든다.
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
