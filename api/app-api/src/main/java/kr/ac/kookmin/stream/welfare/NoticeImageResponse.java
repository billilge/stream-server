package kr.ac.kookmin.stream.welfare;

public record NoticeImageResponse(Long fileId, String fileUrl) {

    public static NoticeImageResponse from(Long fileId) {
        return new NoticeImageResponse(fileId, null);
    }
}
