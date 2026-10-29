package kr.ac.kookmin.stream.event.domain.archive.service;

import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import java.util.List;
import kr.ac.kookmin.stream.event.domain.archive.domain.Archive;
import kr.ac.kookmin.stream.event.domain.archive.domain.ArchiveCreateCommand;
import kr.ac.kookmin.stream.event.domain.archive.domain.ArchiveUpdateCommand;

/**
 * 운영진 아카이빙 API가 쓰는 서비스.
 * <p>
 * 목록만 여기 둔다. 운영진 목록은 등록일 최신순이고 학생 앱 목록은 활동 시작일 내림차순이라 조회 자체가 다르다.
 * 상세 조회는 내용이 같아 운영진도 {@link ArchiveService}를 그대로 쓴다({@code coding-style.md} 2-7절의 공지와 같다).
 */
public interface AdminArchiveService {

    /**
     * 아카이빙을 등록일 최신순으로 조회한다. 학생 앱 목록이 활동 시작일 기준인 것과 달리, 운영진은 방금 올린 글을 먼저 본다.
     *
     * @param keyword           제목 일부. 비어 있으면 제목으로 좁히지 않는다
     * @param exposed 슬랑제 노출 여부. {@code null}이면 노출 여부로 좁히지 않는다
     */
    PageResult<Archive> getArchives(String keyword, Boolean exposed, PageOffset pageOffset);

    /**
     * 아카이빙을 새로 등록한다.
     * <p>
     * 사진 파일과 연결할 행사가 실제로 있는지는 이 서비스가 보지 않는다. 교차 도메인이라
     * {@code api:admin-api}의 UseCase가 확인한다.
     */
    void create(ArchiveCreateCommand command);

    /**
     * 아카이빙을 통째로 바꾼다. 사진과 연결 행사도 받은 값으로 교체한다. 삭제된 아카이빙은 없는 것으로 본다.
     */
    void update(Long archiveId, ArchiveUpdateCommand command);

    /**
     * 아카이빙을 소프트 삭제한다. 학생 앱 조회에서 바로 빠진다.
     */
    void delete(Long archiveId);

    /**
     * 여러 아카이빙의 슬랑제 페이지 노출 여부를 한 번에 바꾼다. 목록의 체크박스 일괄 변경과 행별 드롭다운 변경이
     * 같은 동작이라 하나로 받는다 — 행별 변경은 식별자가 하나인 요청이다.
     * <p>
     * 하나라도 없거나 지워진 아카이빙이 섞이면 아무것도 바꾸지 않는다.
     */
    void changeExposure(List<Long> archiveIds, boolean exposed);
}
