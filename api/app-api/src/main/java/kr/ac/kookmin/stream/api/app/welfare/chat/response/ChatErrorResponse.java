package kr.ac.kookmin.stream.api.app.welfare.chat.response;

/** SSE {@code error} 이벤트의 data. 학생에게 보여줄 안내문. */
public record ChatErrorResponse(String message) {}
