package kr.ac.kookmin.stream.event.domain.locker.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Locker {

    private Long id;
    private Long sectionId;
    /** 화면에 표시할 사물함 이름. "A-37" 형태로 관리자가 직접 입력하며 번호에서 유도하지 않는다. */
    private String lockerLabel;
    /** 블록 안에서 이어지는 사물함 순번. A-1 구역이 38번까지면 A-2 구역은 39번부터 시작한다. */
    private int lockerNumber;
    private int rowNo;
    private int columnNo;
    private LockerStatus status;

    public static Locker of(
        Long id,
        Long sectionId,
        String lockerLabel,
        int lockerNumber,
        int rowNo,
        int columnNo,
        LockerStatus status
    ) {
        return new Locker(id, sectionId, lockerLabel, lockerNumber, rowNo, columnNo, status);
    }

    /**
     * 해당 운영 회차에서 선택할 수 있는지. 사물함 자체 상태와 신청 여부를 함께 본다.
     * <p>
     * 구역 목록의 선택 가능 수와 구역 상세의 선택 가능 여부가 같은 기준을 써야 하므로 도메인에 둔다.
     *
     * @param applied 해당 운영 회차에 이 사물함이 이미 신청되었는지
     */
    public boolean isSelectable(boolean applied) {
        return status == LockerStatus.AVAILABLE && !applied;
    }
}
