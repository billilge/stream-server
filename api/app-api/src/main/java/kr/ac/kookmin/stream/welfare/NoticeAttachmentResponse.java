package kr.ac.kookmin.stream.welfare;

public record NoticeAttachmentResponse(Long fileId, String fileName, String fileUrl) {

    public static NoticeAttachmentResponse from(Long fileId) {
        return new NoticeAttachmentResponse(fileId, null, null);
    }
}
