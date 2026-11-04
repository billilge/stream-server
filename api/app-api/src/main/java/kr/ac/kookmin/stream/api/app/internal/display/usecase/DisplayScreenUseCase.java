package kr.ac.kookmin.stream.api.app.internal.display.usecase;

import java.util.List;
import java.util.Map;
import kr.ac.kookmin.stream.file.domain.File;
import kr.ac.kookmin.stream.file.service.FileService;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayPoster;
import kr.ac.kookmin.stream.internal.domain.display.service.DisplayService;
import kr.ac.kookmin.stream.welfare.domain.rental.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 오프라인 디스플레이 화면에 필요한 값을 한 번에 모은다. 포스터·학사 일정(internal), 물품 재고(welfare),
 * 포스터 이미지(file)가 서로 다른 도메인이라 이 UseCase가 세 Service를 조합한다.
 * <p>
 * 읽기만 하므로 트랜잭션을 걸지 않는다. 조회 사이에 값이 바뀌어도 다음 갱신 때 맞춰진다.
 */
@Component
@RequiredArgsConstructor
public class DisplayScreenUseCase {

    private final DisplayService displayService;
    private final ItemService itemService;
    private final FileService fileService;

    public DisplayScreen getScreen() {
        List<DisplayPoster> posters = displayService.getActivePosters();
        Map<Long, File> posterImagesById = fileService.findAllByIdIn(
            posters.stream().map(DisplayPoster::getImageFileId).toList());

        return new DisplayScreen(
            posters, posterImagesById, displayService.getDailySchedulesAroundToday(), itemService.getAllItems());
    }
}
