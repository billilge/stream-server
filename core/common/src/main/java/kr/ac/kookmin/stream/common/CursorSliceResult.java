package kr.ac.kookmin.stream.common;

import java.util.List;

public record CursorSliceResult<T>(List<T> content, boolean hasNext, Long nextCursor) {}
