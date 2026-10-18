package kr.ac.kookmin.stream.api.admin.welfare.notice.usecase;

import java.util.Map;
import kr.ac.kookmin.stream.file.domain.File;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.Notice;

/**
 * 운영진용 공지 상세. 공지에 그 공지가 연결한 파일 정보를 붙인 조회용 조합 객체다.
 * 존재하지 않는(삭제된) 파일은 맵에 키가 없다.
 */
public record AdminNoticeDetail(
    Notice notice,
    Map<Long, File> imagesById,
    Map<Long, File> attachmentsById
) {
}
