package kr.ac.kookmin.stream.api.admin.event.locker.usecase;

import java.util.List;
import java.util.Set;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.event.domain.locker.service.AdminLockerService;
import kr.ac.kookmin.stream.file.domain.FileErrorCode;
import kr.ac.kookmin.stream.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 구역 사진을 바꾸기 전에 연결할 파일이 실제로 있는지 확인한다. 사물함(event)과 파일(file)은 서로 다른 도메인이라
 * 도메인끼리 직접 의존하지 않고 이 UseCase가 두 Service를 조합한다({@code architecture.md} 6-1절).
 * <p>
 * 파일 확인과 사진 저장을 한 트랜잭션으로 묶지 않는다. 확인 직후 파일이 지워져도 구역 상세 조회가 삭제된 파일을
 * 목록에서 조용히 제외하므로 원자성이 필요하지 않다. 공지와 같은 판단이다({@code AdminNoticeWriteUseCase}).
 */
@Component
@RequiredArgsConstructor
public class AdminLockerPhotoUpdateUseCase {

    private final FileService fileService;
    private final AdminLockerService adminLockerService;

    public void changePhotos(Long sectionId, List<Long> photoFileIds) {
        validateFilesExist(photoFileIds);
        adminLockerService.changeSectionPhotos(sectionId, photoFileIds);
    }

    /**
     * 조회된 개수를 견줘 전부 있는지 본다. 중복이 남아 있으면 있는 파일도 없다고 오판하므로 호출부에 맡기지 않고
     * 여기서 묶는다.
     */
    private void validateFilesExist(List<Long> photoFileIds) {
        Set<Long> fileIds = Set.copyOf(photoFileIds);

        if (fileService.findAllByIdIn(List.copyOf(fileIds)).size() != fileIds.size()) {
            throw new BusinessException(FileErrorCode.FILE_NOT_FOUND);
        }
    }
}
