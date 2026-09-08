package kr.ac.kookmin.stream.event.domain.archive.domain;

import java.time.LocalDate;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Archive {

    private Long id;
    private String title;
    private String content;
    private LocalDate startDate;
    private LocalDate endDate;
    private String location;
    private String departmentName;
    private List<Long> imageIds;

    public static Archive of(
        Long id,
        String title,
        String content,
        LocalDate startDate,
        LocalDate endDate,
        String location,
        String departmentName,
        List<Long> imageIds
    ) {
        return new Archive(id, title, content, startDate, endDate, location, departmentName, imageIds);
    }
}
