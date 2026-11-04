package kr.ac.kookmin.stream.internal.domain.display.service;

import java.util.List;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayPoster;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayPosterCreateCommand;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayPosterUpdateCommand;

/**
 * 운영진용 디스플레이 포스터 서비스. 포스터 조회·등록·수정·삭제와 활성 여부 변경을 맡는다.
 * 이미지 파일이 실제로 있는지는 파일 도메인이 알므로 api의 UseCase가 확인한 뒤 호출한다.
 */
public interface AdminDisplayPosterService {

    // 최신 등록 순으로 전체를 돌려준다. 포스터는 수십 장을 넘지 않아 페이지로 나누지 않는다
    List<DisplayPoster> getPosters();

    // 없으면 DISPLAY_POSTER_NOT_FOUND. 수정 전 이미지를 알아야 하는 UseCase가 쓴다
    DisplayPoster getPoster(Long posterId);

    // 등록한 포스터는 바로 활성 상태다
    DisplayPoster create(DisplayPosterCreateCommand command);

    // 포스터명과 이미지를 바꾼다. 활성 여부는 그대로 둔다
    DisplayPoster update(Long posterId, DisplayPosterUpdateCommand command);

    // 이미 같은 상태여도 그대로 성공한다
    DisplayPoster changeActive(Long posterId, boolean active);

    // 포스터 행만 지우고 지운 포스터를 돌려준다. 이미지 파일은 파일 도메인 일이라 api의 UseCase가 지운다
    DisplayPoster delete(Long posterId);
}
