package kr.ac.kookmin.stream.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ErrorStatus {

    public static final int INTERNAL_SERVER_ERROR = 500;
    public static final int UNAUTHORIZED = 401;
    public static final int NOT_FOUND = 404;
    public static final int BAD_REQUEST = 400;
    public static final int CONFLICT = 409;
    public static final int FORBIDDEN = 403;
}
