package kr.ac.kookmin.stream.internal.domain.display.domain;

// 포스터 수정 요청. 이미지를 바꾸지 않으면 기존 파일 아이디를 그대로 보낸다. 활성 여부는 따로 바꾸므로 받지 않는다.
public record DisplayPosterUpdateCommand(String title, Long imageFileId) {
}
