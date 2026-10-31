package kr.ac.kookmin.stream.internal.domain.home.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 홈 신청 섹션의 노출 설정 한 건. 제목·기간 같은 값은 담지 않는다 — 후보인 행사·회차에서 그때그때 읽는다.
 * <p>
 * 설정이 켜져 있어도 대상이 마감되거나 미게시면 홈에서 빠진다. 그 판정은 각 도메인이 갖고 있다.
 */
@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HomeApplySection {

    private Long id;
    private HomeApplyTargetType targetType;
    private Long targetId;
    private int displayOrder;
    private boolean exposed;

    public static HomeApplySection of(
        Long id,
        HomeApplyTargetType targetType,
        Long targetId,
        int displayOrder,
        boolean exposed
    ) {
        return new HomeApplySection(id, targetType, targetId, displayOrder, exposed);
    }
}
