package kr.ac.kookmin.stream.api.admin.event.locker.response;

import com.fasterxml.jackson.annotation.JsonRawValue;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kr.ac.kookmin.stream.api.common.StorageUrlBuilder;
import kr.ac.kookmin.stream.event.domain.locker.domain.Locker;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerSectionDetail;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerSectionLayout;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerStatus;
import kr.ac.kookmin.stream.file.domain.File;

/**
 * @param layout 칸 배치 구조. 배치 구조를 아직 등록하지 않은 구역이면 {@code null}
 * @param photos 구역 실제 사진. 노출 순서대로 담고, 없으면 빈 목록이다
 */
public record AdminLockerSectionDetailResponse(
    Long sectionId,
    String section,
    Layout layout,
    List<Photo> photos,
    List<LockerItem> lockers
) {

    /**
     * @param photosById       구역 사진 파일. 삭제된 파일은 키가 없어 목록에서 조용히 빠진다
     * @param appliedLockerIds 해당 운영 회차에 이미 신청된 사물함 식별자
     */
    public static AdminLockerSectionDetailResponse of(
        LockerSectionDetail detail,
        Map<Long, File> photosById,
        Set<Long> appliedLockerIds
    ) {
        return new AdminLockerSectionDetailResponse(
            detail.section().getId(),
            detail.section().getLabel(),
            detail.layout() == null ? null : Layout.from(detail.layout()),
            toPhotos(detail, photosById),
            detail.lockers().stream()
                .map(locker -> LockerItem.of(locker, appliedLockerIds.contains(locker.getId())))
                .toList()
        );
    }

    /** 배치 구조를 등록하지 않은 구역이면 사진도 없다. */
    private static List<Photo> toPhotos(LockerSectionDetail detail, Map<Long, File> photosById) {
        if (detail.layout() == null) {
            return List.of();
        }
        return detail.layout().getPhotoFileIds().stream()
            .filter(photosById::containsKey)
            .map(fileId -> Photo.of(fileId, photosById.get(fileId)))
            .toList();
    }

    /**
     * @param version layout 형식 버전
     * @param root    저장된 JSON을 해석하지 않고 그대로 끼워 넣는다
     */
    public record Layout(
        int version,
        @JsonRawValue
        @Schema(type = "object", description = "root 블록 트리. 칸 번호는 lockers[].lockerNumber와 같다")
        String root
    ) {

        public static Layout from(LockerSectionLayout layout) {
            return new Layout(layout.getVersion(), layout.getLayout());
        }
    }

    /** 운영진은 사진을 지우거나 순서를 바꿀 때 파일 식별자가 필요하므로 URL과 함께 내려준다. */
    public record Photo(Long fileId, String fileUrl) {

        public static Photo of(Long fileId, File file) {
            return new Photo(fileId, StorageUrlBuilder.build(file.getFileKey()));
        }
    }

    /** 도메인 {@link Locker}와 이름이 겹치지 않도록 {@code LockerItem}으로 둔다. */
    public record LockerItem(
        Long lockerId,
        int lockerNumber,
        String lockerLabel,
        LockerDisplayStatus status
    ) {

        /** @param applied 해당 운영 회차에 이 사물함이 이미 신청되었는지 */
        public static LockerItem of(Locker locker, boolean applied) {
            return new LockerItem(
                locker.getId(),
                locker.getLockerNumber(),
                locker.getLockerLabel(),
                LockerDisplayStatus.from(locker, applied)
            );
        }
    }

    /**
     * 운영진 화면에 칸을 어떻게 그릴지. 저장되는 물리 상태({@link LockerStatus})와 회차 배정 여부를 합친 값이라
     * 이 응답에서만 쓰고 도메인으로 올리지 않는다({@code coding-style.md} 2-2절).
     */
    public enum LockerDisplayStatus {

        AVAILABLE,
        DISABLED,
        ASSIGNED;

        /**
         * 배정된 뒤 사용 중지된 칸은 {@code ASSIGNED}로 본다. 운영진 화면이 칸 하나를 색 하나로 그려서 둘 중
         * 하나만 고를 수 있고, 배정된 학생이 있다는 사실을 먼저 보여야 한다.
         */
        public static LockerDisplayStatus from(Locker locker, boolean applied) {
            if (applied) {
                return ASSIGNED;
            }
            // 구역 목록의 availableCount와 같은 기준으로 가른다. 배정 여부는 위에서 이미 갈랐으므로 false를 넘긴다
            return locker.isSelectable(false) ? AVAILABLE : DISABLED;
        }
    }
}
