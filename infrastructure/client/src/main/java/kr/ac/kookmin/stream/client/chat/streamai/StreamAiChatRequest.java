package kr.ac.kookmin.stream.client.chat.streamai;

import java.util.List;
import kr.ac.kookmin.stream.welfare.domain.chat.domain.ChatTurn;

// AI 서버 요청 바디: { "messages": [ { "role": ..., "text": ... } ] }
record StreamAiChatRequest(List<Message> messages) {

    record Message(String role, String text) {

        static Message from(ChatTurn turn) {
            return new Message(turn.role(), turn.text());
        }
    }

    static StreamAiChatRequest from(List<ChatTurn> history) {
        return new StreamAiChatRequest(history.stream().map(Message::from).toList());
    }
}
