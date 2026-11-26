package kr.ac.kookmin.stream.welfare.domain.chat.domain;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import kr.ac.kookmin.stream.common.BusinessException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * {@code slot}은 개수 상한을 DB 유니크로 지키려는 자리라 노출 순서와 무관하다. 순서로 겸하면 삭제마다 뒤를 당겨야 하고,
 * 동시에 지우면 빈칸이 남아 상한까지 채우지 못하게 된다.
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatFaq {

    public static final int MAX_COUNT = 5;

    private Long id;
    private String question;
    private int slot;

    public static ChatFaq of(Long id, String question, int slot) {
        return new ChatFaq(id, question, slot);
    }

    public static ChatFaq create(String question, List<ChatFaq> activeFaqs) {
        Set<Integer> usedSlots = activeFaqs.stream().map(ChatFaq::getSlot).collect(Collectors.toSet());
        int slot = IntStream.range(0, MAX_COUNT)
            .filter(candidate -> !usedSlots.contains(candidate))
            .findFirst()
            .orElseThrow(() -> new BusinessException(ChatErrorCode.CHAT_FAQ_LIMIT_EXCEEDED));
        return new ChatFaq(null, question, slot);
    }

    public void changeQuestion(String question) {
        this.question = question;
    }
}
