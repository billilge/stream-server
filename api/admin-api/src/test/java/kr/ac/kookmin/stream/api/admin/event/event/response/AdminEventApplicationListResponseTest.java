package kr.ac.kookmin.stream.api.admin.event.event.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import kr.ac.kookmin.stream.api.admin.event.event.usecase.EventApplicant;
import kr.ac.kookmin.stream.api.admin.event.event.usecase.EventApplicantPage;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.common.Role;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplication;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationAnswer;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationDetail;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationStatus;
import kr.ac.kookmin.stream.event.domain.event.domain.EventManageDetail;
import kr.ac.kookmin.stream.event.domain.event.domain.EventQuestion;
import kr.ac.kookmin.stream.event.domain.event.domain.QuestionType;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitType;
import kr.ac.kookmin.stream.member.domain.member.domain.Department;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 신청 현황 응답 조립. 특히 표에 보일 답변 문구(displayText)를 질문 유형별로 맞게 만드는지 본다.
 */
class AdminEventApplicationListResponseTest {

    private static final LocalDateTime APPLIED_AT = LocalDateTime.of(2026, 10, 3, 14, 22, 7);

    private static final EventQuestion PHONE = EventQuestion.of(
        201L, 15L, "전화번호", QuestionType.SHORT_TEXT, true, 1, List.of(), false);
    private static final EventQuestion ATTEND = EventQuestion.of(
        202L, 15L, "참석 여부", QuestionType.SINGLE_CHOICE, true, 2, List.of("참석", "불참"), false);
    private static final EventQuestion SPORTS = EventQuestion.of(
        203L, 15L, "참여 종목", QuestionType.MULTIPLE_CHOICE, false, 3, List.of("축구", "농구", "피구"), false);

    private static final Event EVENT = Event.of(
        15L, "2026학년도 새내기 배움터", "설명", "미래관", APPLIED_AT.plusDays(30), APPLIED_AT.plusDays(31),
        APPLIED_AT.minusDays(7), APPLIED_AT.plusDays(10), RecruitType.FIRST_COME, List.of(), 200,
        false, true, 1L, APPLIED_AT
    );

    private static Member member(String name, String studentId) {
        return Member.of(101L, studentId, name, Department.SW, null, null, null, null, Role.STUDENT, null);
    }

    private static EventApplicant applicant(Member member, List<EventApplicationAnswer> answers) {
        EventApplication application =
            EventApplication.of(11L, 15L, 101L, EventApplicationStatus.APPLIED, APPLIED_AT, null);
        return new EventApplicant(application, member,
            EventApplicationDetail.AnsweredQuestion.listOf(List.of(PHONE, ATTEND, SPORTS), answers));
    }

    private static AdminEventApplicationListResponse responseOf(EventApplicant applicant) {
        EventApplicantPage page = new EventApplicantPage(
            new EventManageDetail(EVENT, List.of(PHONE, ATTEND, SPORTS), 2),
            3,
            PageResult.of(List.of(applicant), 0, 20, 1)
        );
        return AdminEventApplicationListResponse.from(page);
    }

    private static List<String> displayTextsOf(AdminEventApplicationListResponse response) {
        return response.applications().content().getFirst().answers().stream()
            .map(AdminEventApplicationListResponse.Answer::displayText)
            .toList();
    }

    @Test
    @DisplayName("화면 제목·신청 수·답변 열 헤더를 행사에서 가져온다")
    void header() {
        AdminEventApplicationListResponse response = responseOf(applicant(member("김학생", "20261234"), List.of()));

        assertEquals(15L, response.eventId());
        assertEquals("2026학년도 새내기 배움터", response.eventTitle());
        assertEquals(3, response.totalApplicationCount());
        assertEquals(2, response.appliedCount());
        assertEquals(List.of("전화번호", "참석 여부", "참여 종목"),
            response.questions().stream().map(AdminEventApplicationListResponse.Question::questionText).toList());
    }

    @Test
    @DisplayName("신청자 정보를 담는다")
    void applicantInfo() {
        AdminEventApplicationListResponse.Application row =
            responseOf(applicant(member("김학생", "20261234"), List.of())).applications().content().getFirst();

        assertEquals(11L, row.applicationId());
        assertEquals("김학생", row.name());
        assertEquals("20261234", row.studentId());
        assertEquals(APPLIED_AT, row.appliedAt());
        assertEquals(EventApplicationStatus.APPLIED, row.status());
    }

    @Test
    @DisplayName("탈퇴한 회원의 신청은 이름·학번을 비운다")
    void withdrawnMember() {
        AdminEventApplicationListResponse.Application row =
            responseOf(applicant(null, List.of())).applications().content().getFirst();

        assertNull(row.name());
        assertNull(row.studentId());
    }

    @Nested
    @DisplayName("표에 보일 답변 문구")
    class DisplayText {

        @Test
        @DisplayName("질문 순서대로, 텍스트형은 입력한 답을, 선택형은 고른 선택지 문구를 쉼표로 잇는다")
        void formatsByQuestionType() {
            List<EventApplicationAnswer> answers = List.of(
                // 저장 순서가 질문 순서와 달라도 질문 순서로 맞춘다
                EventApplicationAnswer.of(3L, 11L, 203L, null, List.of(0, 2)),
                EventApplicationAnswer.of(1L, 11L, 201L, "010-1234-5678", null),
                EventApplicationAnswer.of(2L, 11L, 202L, null, List.of(1))
            );

            assertEquals(List.of("010-1234-5678", "불참", "축구, 피구"),
                displayTextsOf(responseOf(applicant(member("김학생", "20261234"), answers))));
        }

        @Test
        @DisplayName("답하지 않은 질문은 null이다")
        void unanswered() {
            List<EventApplicationAnswer> answers = List.of(
                EventApplicationAnswer.of(1L, 11L, 201L, "010-1234-5678", null));

            assertEquals(Arrays.asList("010-1234-5678", null, null),
                displayTextsOf(responseOf(applicant(member("김학생", "20261234"), answers))));
        }

        @Test
        @DisplayName("범위를 벗어난 선택지 번호는 건너뛰고, 남는 게 없으면 null이다")
        void skipsOutOfRangeOptions() {
            List<EventApplicationAnswer> answers = List.of(
                EventApplicationAnswer.of(2L, 11L, 202L, null, List.of(5)),
                EventApplicationAnswer.of(3L, 11L, 203L, null, List.of(1, 9)));

            List<String> texts = displayTextsOf(responseOf(applicant(member("김학생", "20261234"), answers)));

            assertNull(texts.get(1));
            assertEquals("농구", texts.get(2));
        }
    }
}
