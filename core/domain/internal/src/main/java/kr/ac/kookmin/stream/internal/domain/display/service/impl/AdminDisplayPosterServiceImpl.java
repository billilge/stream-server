package kr.ac.kookmin.stream.internal.domain.display.service.impl;

import java.util.List;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayErrorCode;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayPoster;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayPosterCreateCommand;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayPosterUpdateCommand;
import kr.ac.kookmin.stream.internal.domain.display.repository.DisplayPosterRepository;
import kr.ac.kookmin.stream.internal.domain.display.service.AdminDisplayPosterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class AdminDisplayPosterServiceImpl implements AdminDisplayPosterService {

    private final DisplayPosterRepository displayPosterRepository;

    @Override
    public List<DisplayPoster> getPosters() {
        return displayPosterRepository.findAll();
    }

    @Override
    public DisplayPoster getPoster(Long posterId) {
        return getPosterById(posterId);
    }

    @Override
    @Transactional
    public DisplayPoster create(DisplayPosterCreateCommand command) {
        return displayPosterRepository.save(DisplayPoster.create(command));
    }

    @Override
    @Transactional
    public DisplayPoster update(Long posterId, DisplayPosterUpdateCommand command) {
        DisplayPoster poster = getPosterById(posterId);
        poster.update(command);
        return displayPosterRepository.save(poster);
    }

    @Override
    @Transactional
    public DisplayPoster changeActive(Long posterId, boolean active) {
        DisplayPoster poster = getPosterById(posterId);
        poster.changeActive(active);
        return displayPosterRepository.save(poster);
    }

    @Override
    @Transactional
    public DisplayPoster delete(Long posterId) {
        DisplayPoster poster = getPosterById(posterId);
        displayPosterRepository.deleteById(posterId);
        return poster;
    }

    private DisplayPoster getPosterById(Long posterId) {
        return displayPosterRepository.findById(posterId)
            .orElseThrow(() -> new BusinessException(DisplayErrorCode.DISPLAY_POSTER_NOT_FOUND));
    }
}
