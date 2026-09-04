package kr.ac.kookmin.stream.welfare.domain.fee.domain;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StudentFee {

    private Long id;
    private Long memberId;
    private int amount;
    private PaymentStatus paymentStatus;
    private String paymentLinkUrl;
    private LocalDateTime paidAt;
    private Long confirmedBy;

    public static StudentFee of(
        Long id,
        Long memberId,
        int amount,
        PaymentStatus paymentStatus,
        String paymentLinkUrl,
        LocalDateTime paidAt,
        Long confirmedBy
    ) {
        return new StudentFee(id, memberId, amount, paymentStatus, paymentLinkUrl, paidAt, confirmedBy);
    }
}
