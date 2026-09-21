package kr.ac.kookmin.stream.common;

public record PageOffset(int page, int size) {

    public static PageOffset of(int page, int size) {
        return new PageOffset(page, size);
    }
}
