package kr.ac.kookmin.stream.api.admin.internal.display.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import kr.ac.kookmin.stream.api.common.StorageUrlBuilder;
import kr.ac.kookmin.stream.file.domain.File;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayPoster;

public record DisplayPosterListResponse(List<Poster> posters) {

    public static DisplayPosterListResponse of(List<DisplayPoster> posters, Map<Long, File> imagesById) {
        return new DisplayPosterListResponse(posters.stream()
            .map(poster -> Poster.of(poster, imagesById))
            .toList());
    }

    // imageUrl은 이미지 파일이 지워졌으면 null이다. imageFileId는 수정 때 이미지를 그대로 두려면 다시 보내는 값이다
    public record Poster(
        Long posterId,
        String title,
        Long imageFileId,
        String imageUrl,
        boolean active,
        LocalDateTime createdAt
    ) {

        public static Poster of(DisplayPoster poster, Map<Long, File> imagesById) {
            return new Poster(
                poster.getId(),
                poster.getTitle(),
                poster.getImageFileId(),
                StorageUrlBuilder.build(poster.getImageFileId(), imagesById),
                poster.isActive(),
                poster.getCreatedAt()
            );
        }
    }
}
