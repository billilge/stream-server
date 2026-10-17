package kr.ac.kookmin.stream.client.chat.streamai;

// SSE 이벤트 하나. data는 없을 수 있다
record SseEvent(String name, String data) {}
