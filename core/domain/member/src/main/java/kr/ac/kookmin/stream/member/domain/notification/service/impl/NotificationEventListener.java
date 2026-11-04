package kr.ac.kookmin.stream.member.domain.notification.service.impl;

import java.util.List;
import kr.ac.kookmin.stream.common.event.EventApplicationCanceled;
import kr.ac.kookmin.stream.common.event.EventApplied;
import kr.ac.kookmin.stream.common.event.EventApplyClosingTomorrow;
import kr.ac.kookmin.stream.common.event.EventStartingTomorrow;
import kr.ac.kookmin.stream.common.event.LockerApplyClosingTomorrow;
import kr.ac.kookmin.stream.common.event.LockerAssigned;
import kr.ac.kookmin.stream.common.event.LockerAssignedByAdmin;
import kr.ac.kookmin.stream.common.event.LockerAssignmentCanceled;
import kr.ac.kookmin.stream.common.event.LockerUsageEndingSoon;
import kr.ac.kookmin.stream.common.event.NoticeCreated;
import kr.ac.kookmin.stream.common.event.RentalApplicationCanceled;
import kr.ac.kookmin.stream.common.event.RentalApplied;
import kr.ac.kookmin.stream.common.event.RentalApproved;
import kr.ac.kookmin.stream.common.event.RentalDueSoon;
import kr.ac.kookmin.stream.common.event.RentalOverdue;
import kr.ac.kookmin.stream.common.event.RentalRejected;
import kr.ac.kookmin.stream.common.event.RentalReturnApplied;
import kr.ac.kookmin.stream.common.event.RentalReturnApproved;
import kr.ac.kookmin.stream.common.event.RentalReturnCompleted;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.member.domain.member.service.MemberService;
import kr.ac.kookmin.stream.member.domain.notification.domain.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 다른 도메인의 이벤트를 알림으로 바꾼다. 아웃박스 릴레이가 트랜잭션 밖에서 발행하므로 {@code @EventListener}로 받는다.
 * 예외를 던지면 릴레이가 같은 이벤트를 다시 발행한다.
 */
@Component
@RequiredArgsConstructor
class NotificationEventListener {

    private final NotificationDispatcher notificationDispatcher;
    private final MemberService memberService;

    // 신청·취소 알림은 누르면 신청 내역 상세로 가야 하므로 행사가 아니라 신청 ID를 대상으로 준다
    @EventListener
    public void on(EventApplied event) {
        notificationDispatcher.dispatch(
            NotificationType.EVENT_APPLIED, List.of(event.eventTitle()), event.applicationId(), List.of(event.memberId()));
    }

    @EventListener
    public void on(EventApplicationCanceled event) {
        notificationDispatcher.dispatch(
            NotificationType.EVENT_APPLICATION_CANCELED, List.of(event.eventTitle()), event.applicationId(), List.of(event.memberId()));
    }

    @EventListener
    public void on(LockerAssigned event) {
        notificationDispatcher.dispatch(
            NotificationType.LOCKER_ASSIGNED, List.of(), event.lockerPeriodId(), List.of(event.memberId()));
    }

    @EventListener
    public void on(LockerAssignedByAdmin event) {
        notificationDispatcher.dispatch(
            NotificationType.LOCKER_ASSIGNED_BY_ADMIN, List.of(), event.lockerPeriodId(), List.of(event.memberId()));
    }

    // 본인이 하지 않은 변경이고 화면에서는 신청이 사라지므로, 취소는 알림으로만 알 수 있다
    @EventListener
    public void on(LockerAssignmentCanceled event) {
        notificationDispatcher.dispatch(
            NotificationType.LOCKER_ASSIGNMENT_CANCELED, List.of(), event.lockerPeriodId(), List.of(event.memberId()));
    }

    // 공지는 활성 회원 전체에게 간다
    @EventListener
    public void on(NoticeCreated event) {
        notificationDispatcher.dispatchToAll(NotificationType.NOTICE_CREATED, List.of(), event.noticeId(), List.of());
    }

    @EventListener
    public void on(EventStartingTomorrow event) {
        notificationDispatcher.dispatch(
            NotificationType.EVENT_STARTING_TOMORROW, List.of(event.eventTitle()), event.eventId(), event.applicantIds());
    }

    // 마감 임박은 아직 신청하지 않은 회원에게 간다
    @EventListener
    public void on(EventApplyClosingTomorrow event) {
        notificationDispatcher.dispatchToAll(
            NotificationType.EVENT_APPLY_CLOSING, List.of(event.eventTitle()), event.eventId(), event.applicantIds());
    }

    @EventListener
    public void on(LockerApplyClosingTomorrow event) {
        notificationDispatcher.dispatchToAll(
            NotificationType.LOCKER_APPLY_CLOSING, List.of(), event.lockerPeriodId(), event.applicantIds());
    }

    @EventListener
    public void on(LockerUsageEndingSoon event) {
        notificationDispatcher.dispatch(
            NotificationType.LOCKER_USAGE_ENDING, List.of(), event.lockerPeriodId(), event.applicantIds());
    }

    @EventListener
    public void on(RentalDueSoon event) {
        notificationDispatcher.dispatch(
            NotificationType.RENTAL_DUE_SOON, List.of(event.itemName()), event.rentalHistoryId(), List.of(event.memberId()));
    }

    @EventListener
    public void on(RentalOverdue event) {
        notificationDispatcher.dispatch(
            NotificationType.RENTAL_OVERDUE, List.of(event.itemName()), event.rentalHistoryId(), List.of(event.memberId()));
    }

    // 신청은 본인과 운영진 모두에게 간다
    @EventListener
    public void on(RentalApplied event) {
        notificationDispatcher.dispatch(
            NotificationType.RENTAL_APPLY, List.of(event.itemName()), event.rentalHistoryId(), List.of(event.memberId()));

        Member member = memberService.getById(event.memberId());
        notificationDispatcher.dispatchToAdmins(
            NotificationType.ADMIN_RENTAL_APPLY,
            List.of(member.getName(), member.getStudentId(), event.rentAtTime(), event.itemName()),
            event.rentalHistoryId());
    }

    // 본인이 취소한 신청이라 본인에게는 알림이 가지 않는다. 운영진만 안다
    @EventListener
    public void on(RentalApplicationCanceled event) {
        Member member = memberService.getById(event.memberId());
        notificationDispatcher.dispatchToAdmins(
            NotificationType.ADMIN_RENTAL_CANCEL,
            List.of(member.getName(), member.getStudentId(), event.itemName()),
            event.rentalHistoryId());
    }

    @EventListener
    public void on(RentalReturnApplied event) {
        notificationDispatcher.dispatch(
            NotificationType.RETURN_APPLY, List.of(event.itemName()), event.rentalHistoryId(), List.of(event.memberId()));

        Member member = memberService.getById(event.memberId());
        notificationDispatcher.dispatchToAdmins(
            NotificationType.ADMIN_RETURN_APPLY,
            List.of(member.getName(), member.getStudentId(), event.itemName()),
            event.rentalHistoryId());
    }

    @EventListener
    public void on(RentalApproved event) {
        notificationDispatcher.dispatch(
            NotificationType.RENTAL_APPROVED, List.of(event.itemName()), event.rentalHistoryId(), List.of(event.memberId()));
    }

    @EventListener
    public void on(RentalRejected event) {
        notificationDispatcher.dispatch(
            NotificationType.RENTAL_REJECTED, List.of(event.itemName()), event.rentalHistoryId(), List.of(event.memberId()));
    }

    @EventListener
    public void on(RentalReturnApproved event) {
        notificationDispatcher.dispatch(
            NotificationType.RETURN_APPROVED, List.of(event.itemName()), event.rentalHistoryId(), List.of(event.memberId()));
    }

    // 급하게 확인할 내용이 아니라 알림함에만 남기고 푸시는 보내지 않는다
    @EventListener
    public void on(RentalReturnCompleted event) {
        notificationDispatcher.dispatchWithoutPush(
            NotificationType.RETURN_COMPLETED, List.of(event.itemName()), event.rentalHistoryId(), List.of(event.memberId()));
    }
}
