package kr.ac.kookmin.stream.api.app.event.locker.response;

import com.fasterxml.jackson.annotation.JsonRawValue;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Set;
import kr.ac.kookmin.stream.api.common.StorageUrlBuilder;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerSectionDetail;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerSectionLayout;
import kr.ac.kookmin.stream.file.domain.File;

/**
 * @param layout   칸 배치 구조. 배치 구조를 아직 등록하지 않은 구역이면 {@code null}
 * @param photoUrl 구역 실제 사진. 배치 구조를 아직 등록하지 않았거나 사진 파일이 삭제됐으면 {@code null}
 */
public record LockerSectionDetailResponse(
    Long sectionId,
    String section,
    Layout layout,
    String photoUrl,
    List<LockerResponse> lockers
) {

    /**
     * @param photo            구역 사진 파일. 배치 구조를 아직 등록하지 않았거나 파일이 삭제됐으면 {@code null}
     * @param appliedLockerIds 해당 운영 회차에 이미 신청된 사물함 식별자
     * @param myLockerId       조회한 회원이 신청한 사물함. 신청하지 않았으면 {@code null}
     */
    public static LockerSectionDetailResponse of(
        LockerSectionDetail detail,
        File photo,
        Set<Long> appliedLockerIds,
        Long myLockerId
    ) {
        return new LockerSectionDetailResponse(
            detail.section().getId(),
            detail.section().getLabel(),
            detail.layout() == null ? null : Layout.from(detail.layout()),
            StorageUrlBuilder.build(photo),
            detail.lockers().stream()
                .map(locker -> LockerResponse.of(locker, appliedLockerIds.contains(locker.getId()), myLockerId))
                .toList()
        );
    }

    /**
     * @param version layout 형식 버전. 앱은 모르는 버전이면 그리지 않고 업데이트를 안내한다
     * @param root    저장된 JSON을 해석하지 않고 그대로 끼워 넣는다
     */
    public record Layout(
        int version,
        @JsonRawValue
        @Schema(type = "object", description = "root 블록 트리. 블록은 type으로 구분하고, 칸 번호는 lockers[].lockerNumber와 같다")
        String root
    ) {

        public static Layout from(LockerSectionLayout layout) {
            return new Layout(layout.getVersion(), layout.getLayout());
        }
    }
}
