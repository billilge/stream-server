package kr.ac.kookmin.stream.event.domain.archive.service;

import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.event.domain.archive.domain.Archive;

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
}
