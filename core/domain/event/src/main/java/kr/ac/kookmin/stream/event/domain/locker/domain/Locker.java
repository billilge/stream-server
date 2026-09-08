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
    private String lockerNumber;
    private String section;
    private int rowNo;
    private int columnNo;
    private LockerStatus status;

    public static Locker of(
        Long id,
        String lockerNumber,
        String section,
        int rowNo,
        int columnNo,
        LockerStatus status
    ) {
        return new Locker(id, lockerNumber, section, rowNo, columnNo, status);
    }
}
