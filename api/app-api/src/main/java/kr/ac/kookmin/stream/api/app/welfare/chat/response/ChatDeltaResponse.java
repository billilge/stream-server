package kr.ac.kookmin.stream.api.app.welfare.chat.response;

/** SSE {@code delta} 이벤트의 data. 답변 조각 하나. */
public record ChatDeltaResponse(String text) {}
