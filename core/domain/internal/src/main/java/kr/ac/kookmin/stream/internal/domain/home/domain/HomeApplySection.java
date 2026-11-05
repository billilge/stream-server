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

    /**
     * 운영진이 노출이나 순서를 처음 건드린 후보의 설정.
     */
    public static HomeApplySection create(HomeApplyTarget target, int displayOrder, boolean exposed) {
        return new HomeApplySection(null, target.targetType(), target.targetId(), displayOrder, exposed);
    }

    /**
     * 설정 행이 아직 없는 후보. 운영진 목록을 채우는 데만 쓰고 저장하지 않는다 — 조회는 쓰기를 하지 않는다.
     * {@code id}가 {@code null}이고 {@code displayOrder}는 쓰지 않는다(목록의 자리가 순서다).
     */
    public static HomeApplySection notConfigured(HomeApplyTarget target) {
        return new HomeApplySection(null, target.targetType(), target.targetId(), 0, false);
    }

    public HomeApplyTarget target() {
        return new HomeApplyTarget(targetType, targetId);
    }

    public void changeExposure(boolean exposed) {
        this.exposed = exposed;
    }

    public void changeDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }
}
