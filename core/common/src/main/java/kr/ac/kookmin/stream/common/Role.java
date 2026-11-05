package kr.ac.kookmin.stream.common;

public enum Role {
    STUDENT,
    ADMIN;

    public static Role from(String value) {
        if (value == null) {
            return null;
        }
        try {
            return Role.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(CommonErrorCode.INVALID_INPUT);
        }
    }
}
