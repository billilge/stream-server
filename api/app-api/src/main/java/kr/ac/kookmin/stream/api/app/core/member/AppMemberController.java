package kr.ac.kookmin.stream.api.app.core.member;

import jakarta.validation.Valid;
import kr.ac.kookmin.stream.api.app.AppApiUser;
import kr.ac.kookmin.stream.api.app.core.member.request.FcmTokenUpdateRequest;
import kr.ac.kookmin.stream.api.app.core.member.request.MemberSignUpRequest;
import kr.ac.kookmin.stream.api.app.core.member.request.PhoneNumberUpdateRequest;
import kr.ac.kookmin.stream.api.app.core.member.response.MemberResponse;
import kr.ac.kookmin.stream.api.app.core.member.usecase.MemberSignUpUseCase;
import kr.ac.kookmin.stream.api.app.core.member.usecase.MemberWithdrawUseCase;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.member.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/app/members")
@RequiredArgsConstructor
public class AppMemberController implements AppMemberApi {

    private final MemberService memberService;
    private final MemberSignUpUseCase memberSignUpUseCase;
    private final MemberWithdrawUseCase memberWithdrawUseCase;

    @Override
    @GetMapping("/me")
    public ApiResponse<MemberResponse> getMe(AppApiUser apiUser) {
        Member member = memberService.getById(apiUser.userId());
        return ApiResponse.success(MemberResponse.from(member));
    }

    @Override
    @PostMapping("/sign-up")
    public ApiResponse<Void> signUp(
        AppApiUser apiUser,
        @Valid @RequestBody MemberSignUpRequest request
    ) {
        memberSignUpUseCase.signUp(apiUser.userId(), request.toCommand());
        return ApiResponse.success();
    }

    @Override
    @PatchMapping("/me/fcm-token")
    public ApiResponse<Void> updateFcmToken(
        AppApiUser apiUser,
        @Valid @RequestBody FcmTokenUpdateRequest request
    ) {
        memberService.updateFcmToken(apiUser.userId(), request.fcmToken());
        return ApiResponse.success();
    }

    @Override
    @PatchMapping("/me/phone-number")
    public ApiResponse<Void> updatePhoneNumber(
        AppApiUser apiUser,
        @Valid @RequestBody PhoneNumberUpdateRequest request
    ) {
        memberService.updatePhoneNumber(apiUser.userId(), request.toCommand());
        return ApiResponse.success();
    }

    @Override
    @DeleteMapping("/me")
    public ApiResponse<Void> withdraw(AppApiUser apiUser) {
        memberWithdrawUseCase.withdraw(apiUser.userId());
        return ApiResponse.success();
    }
}
