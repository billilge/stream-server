package kr.ac.kookmin.stream.api.admin.internal.display.usecase;

import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.file.domain.File;
import kr.ac.kookmin.stream.file.domain.FileCategory;
import kr.ac.kookmin.stream.file.domain.FileErrorCode;
import kr.ac.kookmin.stream.file.service.FileService;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayPoster;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayPosterCreateCommand;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayPosterUpdateCommand;
import kr.ac.kookmin.stream.internal.domain.display.service.AdminDisplayPosterService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 포스터를 쓰기 전에 포스터용으로 올린 이미지 파일인지 확인하고, 포스터를 지우거나 이미지를 바꾸면 더는 쓰이지 않는 이전 파일을 지운다.
 * 포스터(internal)와 파일(file)은 서로 다른 도메인이라 도메인끼리 직접 의존하지 않고 이 UseCase가 두 Service를 조합한다.
 * <p>
 * 공지·행사는 소프트 삭제라 지운 뒤에도 행이 파일을 가리키므로 파일을 남긴다. 포스터는 물리 삭제라 남기면 아무도 가리키지 않는
 * 고아 파일이 되므로 여기서 지운다. 파일 삭제는 포스터 쪽 트랜잭션이 커밋된 뒤에 한다 — 반대로 하면 포스터 쪽이 롤백될 때
 * 이미지 없는 포스터가 남는다. 파일 삭제가 실패해도 포스터 변경은 이미 끝났으므로 요청은 성공으로 끝내고 로그만 남긴다.
 * <p>
 * 파일 확인과 포스터 저장은 한 트랜잭션으로 묶지 않는다. 확인 직후 파일이 지워져도 조회가 이미지 URL을 null로 내려줄 뿐이라
 * 원자성이 필요하지 않다.
 */
@Component
@RequiredArgsConstructor
public class AdminDisplayPosterWriteUseCase {

    private static final Logger log = LoggerFactory.getLogger(AdminDisplayPosterWriteUseCase.class);

    private final AdminDisplayPosterService adminDisplayPosterService;
    private final FileService fileService;

    public DisplayPoster create(DisplayPosterCreateCommand command) {
        validatePosterImage(command.imageFileId());
        return adminDisplayPosterService.create(command);
    }

    public DisplayPoster update(Long posterId, DisplayPosterUpdateCommand command) {
        Long previousImageFileId = adminDisplayPosterService.getPoster(posterId).getImageFileId();
        validatePosterImage(command.imageFileId());

        DisplayPoster updated = adminDisplayPosterService.update(posterId, command);
        if (!previousImageFileId.equals(updated.getImageFileId())) {
            deleteImageQuietly(previousImageFileId);
        }
        return updated;
    }

    public void delete(Long posterId) {
        DisplayPoster deleted = adminDisplayPosterService.delete(posterId);
        deleteImageQuietly(deleted.getImageFileId());
    }

    // 포스터를 지우거나 이미지를 바꾸면 이 파일도 지우므로 포스터용(DISPLAY_POSTER)으로 올린 파일만 받는다.
    // 다른 용도의 파일(공지 이미지, 회비 인증 캡처 등)이 들어오면 그 파일이 포스터와 함께 지워진다.
    // 용도가 다른 파일도 없는 파일과 같은 에러로 거절한다
    private void validatePosterImage(Long imageFileId) {
        File image = fileService.findById(imageFileId)
            .orElseThrow(() -> new BusinessException(FileErrorCode.FILE_NOT_FOUND));
        if (image.getCategory() != FileCategory.DISPLAY_POSTER) {
            throw new BusinessException(FileErrorCode.FILE_NOT_FOUND);
        }
    }

    // 이미 지워진 파일(FILE_NOT_FOUND)이나 저장소 오류는 요청을 실패시키지 않는다. 남은 파일을 찾을 수 있게 아이디를 남긴다
    private void deleteImageQuietly(Long imageFileId) {
        try {
            fileService.delete(imageFileId);
        } catch (RuntimeException e) {
            log.warn("디스플레이 포스터 이전 이미지 삭제 실패: imageFileId={}", imageFileId, e);
        }
    }
}
