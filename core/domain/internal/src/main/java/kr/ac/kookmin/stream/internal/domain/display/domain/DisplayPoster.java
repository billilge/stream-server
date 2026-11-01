package kr.ac.kookmin.stream.internal.domain.display.domain;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DisplayPoster {

    private Long id;
    private String title;
    private Long imageFileId;
    private boolean active;
    private LocalDateTime createdAt;

    // 새로 등록한 포스터는 바로 디스플레이에 노출한다. 등록 시각은 DB가 채운다
    public static DisplayPoster create(DisplayPosterCreateCommand command) {
        return new DisplayPoster(null, command.title(), command.imageFileId(), true, null);
    }

    public static DisplayPoster of(Long id, String title, Long imageFileId, boolean active, LocalDateTime createdAt) {
        return new DisplayPoster(id, title, imageFileId, active, createdAt);
    }

    // 포스터명과 이미지만 바꾼다. 활성 여부는 changeActive로만 바꾼다
    public void update(DisplayPosterUpdateCommand command) {
        this.title = command.title();
        this.imageFileId = command.imageFileId();
    }

    public void changeActive(boolean active) {
        this.active = active;
    }
}
