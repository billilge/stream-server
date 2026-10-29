package kr.ac.kookmin.stream.api.app.event.archive.usecase;

import kr.ac.kookmin.stream.event.domain.archive.domain.ArchiveDetail;

/**
 * {@link ArchiveDetailUseCase}가 조합한 결과. DTO 변환은 Controller가 한다.
 * <p>
 * {@code api:admin-api}에도 같은 이름의 record가 있다. 두 모듈은 서로를 모르고, 몇 줄을 공유하자고
 * {@code api:common-api}가 event 도메인에 의존하게 만들 이유가 없어 복제해 둔다 — 운영진과 학생 앱의
 * 연결 행사 판정이 이미 갈려 있어(미게시 행사) 앞으로 더 달라질 자리이기도 하다.
 *
 * @param relatedEventTitle 연결한 행사의 제목. 연결이 없거나 행사가 사라졌으면 {@code null}
 */
public record ArchiveWithRelatedEvent(ArchiveDetail detail, String relatedEventTitle) {
}
