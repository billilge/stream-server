package kr.ac.kookmin.stream.event.domain.event.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import kr.ac.kookmin.stream.common.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

class EventApplicationStatusTest {

    @Test
    @DisplayName("값이 없으면 필터를 걸지 않는다는 뜻이라 null을 돌려준다")
    void nullMeansNoFilter() {
        assertNull(EventApplicationStatus.from(null));
    }

    @ParameterizedTest
    @DisplayName("정의된 이름은 그대로 바뀐다")
    @EnumSource(EventApplicationStatus.class)
    void parsesDefinedNames(EventApplicationStatus status) {
        assertEquals(status, EventApplicationStatus.from(status.name()));
    }

    @ParameterizedTest
    @DisplayName("정의되지 않은 값은 500이 아니라 400으로 걸러진다")
    @ValueSource(strings = {"applied", " APPLIED", "CANCELLED", "1", "-"})
    void rejectsUndefinedValues(String raw) {
        BusinessException e = assertThrows(BusinessException.class, () -> EventApplicationStatus.from(raw));

        assertEquals(EventErrorCode.EVENT_INVALID_APPLICATION_STATUS, e.getErrorCode());
    }
}
