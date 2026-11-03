package kr.ac.kookmin.stream.api.app.event.archive.usecase;

import kr.ac.kookmin.stream.event.domain.archive.domain.ArchiveDetail;

/**
 * {@link ArchiveDetailUseCase}가 조합한 결과. DTO 변환은 Controller가 한다.
 * <p>
 * {@code api:admin-api}에도 같은 record가 있다. 복제해 둔 이유는 그쪽 javadoc에 적었다.
 *
 * @param relatedEventTitle 연결한 행사의 제목. 연결이 없거나 행사가 사라졌으면 {@code null}
 */
public record ArchiveWithRelatedEvent(ArchiveDetail detail, String relatedEventTitle) {
}
