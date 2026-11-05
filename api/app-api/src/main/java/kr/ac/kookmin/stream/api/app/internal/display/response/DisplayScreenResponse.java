package kr.ac.kookmin.stream.api.app.internal.display.response;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import kr.ac.kookmin.stream.api.app.internal.display.usecase.DisplayScreen;
import kr.ac.kookmin.stream.api.common.StorageUrlBuilder;
import kr.ac.kookmin.stream.file.domain.File;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayDailySchedule;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayPoster;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.Item;

public record DisplayScreenResponse(
    List<Poster> posters,
    List<DailySchedule> schedules,
    List<StockItem> items
) {

    public static DisplayScreenResponse from(DisplayScreen screen) {
        return new DisplayScreenResponse(
            screen.posters().stream().map(poster -> Poster.of(poster, screen.posterImagesById())).toList(),
            screen.schedules().stream().map(DailySchedule::from).toList(),
            screen.items().stream().map(StockItem::from).toList()
        );
    }

    // imageUrl은 이미지 파일이 지워졌으면 null이다
    public record Poster(Long posterId, String title, String imageUrl) {

        public static Poster of(DisplayPoster poster, Map<Long, File> imagesById) {
            return new Poster(
                poster.getId(), poster.getTitle(), StorageUrlBuilder.build(poster.getImageFileId(), imagesById));
        }
    }

    // 일정이 없는 날은 titles가 빈 배열이다
    public record DailySchedule(LocalDate date, List<String> titles) {

        public static DailySchedule from(DisplayDailySchedule schedule) {
            return new DailySchedule(schedule.date(), schedule.titles());
        }
    }

    // 물품 이미지는 파일 아이디가 아니라 키로 저장되어 있다. 이미지가 없으면 imageUrl이 null이다
    public record StockItem(String itemName, int count, String imageUrl) {

        public static StockItem from(Item item) {
            return new StockItem(item.getName(), item.getCount(), StorageUrlBuilder.build(item.getImageKey()));
        }
    }
}
