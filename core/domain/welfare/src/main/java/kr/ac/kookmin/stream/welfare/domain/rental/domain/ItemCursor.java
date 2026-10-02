package kr.ac.kookmin.stream.welfare.domain.rental.domain;

import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.Cursor;

/**
 * 물품 목록의 keyset 커서. 정렬 기준(이름 오름차순 + itemId 오름차순)과 짝을 이룬다.
 */
public record ItemCursor(String name, Long id) implements Cursor {

    private static final char JOIN = '|';

    public static ItemCursor of(Item item) {
        return new ItemCursor(item.getName(), item.getId());
    }

    // 이름에 구분자가 들어 있어도 되도록 id가 오는 마지막 구분자를 기준으로 나눈다(id는 숫자라 구분자를 포함하지 않는다)
    public static ItemCursor from(String raw) {
        int separator = raw.lastIndexOf(JOIN);
        if (separator < 0) {
            throw new BusinessException(RentalErrorCode.ITEM_INVALID_CURSOR);
        }
        try {
            return new ItemCursor(raw.substring(0, separator), Long.valueOf(raw.substring(separator + 1)));
        } catch (NumberFormatException e) {
            throw new BusinessException(RentalErrorCode.ITEM_INVALID_CURSOR);
        }
    }

    @Override
    public String format() {
        return name + JOIN + id;
    }
}
