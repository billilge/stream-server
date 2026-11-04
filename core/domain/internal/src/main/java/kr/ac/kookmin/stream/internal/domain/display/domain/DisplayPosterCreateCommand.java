package kr.ac.kookmin.stream.internal.domain.display.domain;

// 포스터 등록 요청. 활성 여부는 서버가 정하므로(등록 즉시 활성) 받지 않는다.
public record DisplayPosterCreateCommand(String title, Long imageFileId) {
}
