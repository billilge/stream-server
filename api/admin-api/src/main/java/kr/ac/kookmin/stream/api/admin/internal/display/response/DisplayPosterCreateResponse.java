package kr.ac.kookmin.stream.api.admin.internal.display.response;

import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayPoster;

public record DisplayPosterCreateResponse(Long posterId) {

    public static DisplayPosterCreateResponse from(DisplayPoster poster) {
        return new DisplayPosterCreateResponse(poster.getId());
    }
}
