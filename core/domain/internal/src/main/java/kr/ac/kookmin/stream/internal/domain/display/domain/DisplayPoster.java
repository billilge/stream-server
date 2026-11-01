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

    public static DisplayPoster of(Long id, String title, Long imageFileId, boolean active, LocalDateTime createdAt) {
        return new DisplayPoster(id, title, imageFileId, active, createdAt);
    }
}
