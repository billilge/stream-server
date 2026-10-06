package kr.ac.kookmin.stream.api.app.core.member;

import jakarta.validation.Valid;
import kr.ac.kookmin.stream.api.app.AppApiUser;
import kr.ac.kookmin.stream.api.app.core.member.request.MemberSignUpRequest;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.member.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/app/members")
@RequiredArgsConstructor
public class AppMemberController implements AppMemberApi {

    private final MemberService memberService;

    @Override
    @PostMapping("/sign-up")
    public ApiResponse<Void> signUp(
        AppApiUser apiUser,
        @Valid @RequestBody MemberSignUpRequest request
    ) {
        memberService.signUp(apiUser.userId(), request.toCommand());
        return ApiResponse.success();
    }
}
