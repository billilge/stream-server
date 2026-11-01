package kr.ac.kookmin.stream.internal.domain.display.repository;

import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayPoster;

public interface DisplayPosterRepository {

    Optional<DisplayPoster> findById(Long id);

    // 최신 등록 순(id 내림차순)
    List<DisplayPoster> findAll();

    // 활성 포스터를 등록 순(id 오름차순)으로
    List<DisplayPoster> findAllActive();

    DisplayPoster save(DisplayPoster poster);

    void deleteById(Long id);
}
