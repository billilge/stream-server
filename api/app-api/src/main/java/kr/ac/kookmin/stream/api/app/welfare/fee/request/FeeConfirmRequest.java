package kr.ac.kookmin.stream.api.app.welfare.fee.request;

import kr.ac.kookmin.stream.welfare.domain.fee.domain.GradeSemester;

// 학년·학기 검증은 GradeSemester.of에서 하므로, 여기서는 Bean Validation을 붙이지 않는다.
public record FeeConfirmRequest(Integer grade, Integer semester) {

    public GradeSemester toGradeSemester() {
        return GradeSemester.of(grade, semester);
    }
}
