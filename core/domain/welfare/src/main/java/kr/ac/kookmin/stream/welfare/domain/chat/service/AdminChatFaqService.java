package kr.ac.kookmin.stream.welfare.domain.chat.service;

/**
 * 운영진용 추천 질문 서비스. 등록·수정·삭제를 맡고, 목록 조회는 운영진도 {@link ChatFaqService}를 쓴다.
 */
public interface AdminChatFaqService {

    void create(String question);

    void updateQuestion(Long id, String question);

    void delete(Long id);
}
