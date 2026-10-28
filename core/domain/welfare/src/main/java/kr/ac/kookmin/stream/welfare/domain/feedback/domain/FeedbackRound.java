package kr.ac.kookmin.stream.welfare.domain.feedback.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import kr.ac.kookmin.stream.common.BusinessException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 관리자가 여는 열린피드백 접수 회차. 오픈/마감 시각과 함께 연도별로 관리한다(연도별로 1차부터 다시 시작).
 * answersPublishedAt은 그 회차의 답변을 학생에게 공개한 시각으로, null이면 아직 임시저장 단계다.
 */
@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FeedbackRound {

    private static final LocalTime OPENS_TIME = LocalTime.MIN;
    private static final LocalTime CLOSES_TIME = LocalTime.of(23, 59, 59);

    private Long id;
    private int year;
    private int round;
    private LocalDateTime opensAt;
    private LocalDateTime closesAt;
    private LocalDateTime answersPublishedAt;

    /**
     * 화면이 날짜만 받으므로 시각은 여기서 채운다 — 시작일 00:00:00 ~ 마감일 23:59:59.
     * 연도는 시작일 기준이고, 회차 번호는 그 연도의 다음 번호를 호출부가 계산해 넘긴다.
     */
    public static FeedbackRound create(int round, LocalDate opensOn, LocalDate closesOn) {
        if (closesOn.isBefore(opensOn)) {
            throw new BusinessException(FeedbackErrorCode.INVALID_FEEDBACK_ROUND_PERIOD);
        }
        return new FeedbackRound(
            null, opensOn.getYear(), round, opensOn.atTime(OPENS_TIME), closesOn.atTime(CLOSES_TIME), null);
    }

    public static FeedbackRound of(
        Long id,
        int year,
        int round,
        LocalDateTime opensAt,
        LocalDateTime closesAt,
        LocalDateTime answersPublishedAt
    ) {
        return new FeedbackRound(id, year, round, opensAt, closesAt, answersPublishedAt);
    }

    public boolean isAnswersPublished() {
        return answersPublishedAt != null;
    }

    /**
     * 그 회차의 답변을 학생에게 공개한다. 이미 공개한 회차는 공개 시각을 그대로 둔다 —
     * 등록하기를 다시 눌러도 결과가 같게(멱등) 하기 위함이다.
     */
    public void publishAnswers(LocalDateTime publishedAt) {
        if (isAnswersPublished()) {
            return;
        }
        this.answersPublishedAt = publishedAt;
    }
}
