package kr.ac.kookmin.stream.api.app.welfare.feedback.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FeedbackCreateRequest(

    @NotBlank(message = "피드백 내용을 입력해 주세요.")
    @Size(max = 1000, message = "피드백 내용은 1000자 이하로 입력해 주세요.")
    String question
) {
}
