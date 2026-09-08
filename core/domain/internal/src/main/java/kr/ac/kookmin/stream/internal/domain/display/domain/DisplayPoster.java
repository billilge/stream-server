package kr.ac.kookmin.stream.internal.domain.display.domain;

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
    private String imageKey;
    private boolean active;

    public static DisplayPoster of(Long id, String title, String imageKey, boolean active) {
        return new DisplayPoster(id, title, imageKey, active);
    }
}
