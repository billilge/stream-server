package kr.ac.kookmin.stream.api.admin.event.event.usecase;

import java.util.List;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.domain.EventCreateCommand;
import kr.ac.kookmin.stream.event.domain.event.service.EventService;
import kr.ac.kookmin.stream.file.domain.FileErrorCode;
import kr.ac.kookmin.stream.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 행사를 쓰기 전에 연결할 이미지 파일이 실제로 있는지 확인한다. 행사(event)와 파일(file)은 서로 다른 도메인이라
 * 도메인끼리 직접 의존하지 않고 이 UseCase가 두 Service를 조합한다.
 * <p>
 * 파일 확인과 행사 저장은 한 트랜잭션으로 묶지 않는다. 확인 직후 파일이 지워져도 행사 조회가 삭제된 파일을
 * 목록에서 조용히 제외하므로 원자성이 필요하지 않다.
 */
@Component
@RequiredArgsConstructor
public class AdminEventWriteUseCase {

    private final EventService eventService;
    private final FileService fileService;

    public Event create(Long createdBy, EventCreateCommand command) {
        requireImagesExist(command.imageIds());
        return eventService.create(createdBy, command);
    }

    // 요청 DTO가 중복을 걸러 넘기므로 조회 결과 개수와 바로 비교할 수 있다
    private void requireImagesExist(List<Long> imageIds) {
        if (fileService.findAllByIdIn(imageIds).size() != imageIds.size()) {
            throw new BusinessException(FileErrorCode.FILE_NOT_FOUND);
        }
    }
}
