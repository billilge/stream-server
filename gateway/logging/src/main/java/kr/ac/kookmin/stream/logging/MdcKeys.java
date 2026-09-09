package kr.ac.kookmin.stream.logging;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MdcKeys {

    public static final String REQUEST_ID = "requestId";
    public static final String USER_ID = "userId";
}
