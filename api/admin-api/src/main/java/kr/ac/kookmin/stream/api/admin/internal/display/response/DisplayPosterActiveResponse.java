package kr.ac.kookmin.stream.api.admin.internal.display.response;

import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayPoster;

public record DisplayPosterActiveResponse(Long posterId, boolean active) {

    public static DisplayPosterActiveResponse from(DisplayPoster poster) {
        return new DisplayPosterActiveResponse(poster.getId(), poster.isActive());
    }
}
