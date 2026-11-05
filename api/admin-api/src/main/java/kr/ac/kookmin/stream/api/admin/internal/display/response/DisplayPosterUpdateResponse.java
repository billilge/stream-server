package kr.ac.kookmin.stream.api.admin.internal.display.response;

import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayPoster;

public record DisplayPosterUpdateResponse(Long posterId) {

    public static DisplayPosterUpdateResponse from(DisplayPoster poster) {
        return new DisplayPosterUpdateResponse(poster.getId());
    }
}
