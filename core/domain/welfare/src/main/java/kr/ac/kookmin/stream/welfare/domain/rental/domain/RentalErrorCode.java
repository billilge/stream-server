package kr.ac.kookmin.stream.welfare.domain.rental.domain;

import kr.ac.kookmin.stream.common.ErrorCode;
import kr.ac.kookmin.stream.common.ErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public enum RentalErrorCode implements ErrorCode {

    ITEM_INVALID_CURSOR(ErrorStatus.BAD_REQUEST, "유효하지 않은 커서입니다."),
    ITEM_NOT_FOUND(ErrorStatus.NOT_FOUND, "존재하지 않는 물품입니다."),
    MEMBER_IS_NOT_PAYER(ErrorStatus.BAD_REQUEST, "회비를 납부한 회원만 이용할 수 있습니다."),
    ITEM_OUT_OF_STOCK(ErrorStatus.BAD_REQUEST, "재고가 부족합니다."),
    RENTAL_ITEM_DUPLICATED(ErrorStatus.BAD_REQUEST, "이미 대여 중인 물품입니다."),
    INVALID_RENTAL_TIME_RANGE(ErrorStatus.BAD_REQUEST, "대여 가능 시간이 아니에요. 10시-17시 사이로 선택해 주세요."),
    INVALID_RENTAL_TIME_LUNCH_BREAK(ErrorStatus.BAD_REQUEST, "점심시간(12시-13시)에는 대여가 불가능해요.");

    private final int status;
    private final String message;
}
