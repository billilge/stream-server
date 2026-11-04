package kr.ac.kookmin.stream.api.app.internal.display.usecase;

import java.util.List;
import java.util.Map;
import kr.ac.kookmin.stream.file.domain.File;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayDailySchedule;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayPoster;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.Item;

// 디스플레이 화면 조회 결과. 포스터 이미지 URL은 응답을 만들 때 posterImagesById로 만든다
public record DisplayScreen(
    List<DisplayPoster> posters,
    Map<Long, File> posterImagesById,
    List<DisplayDailySchedule> schedules,
    List<Item> items
) {
}
